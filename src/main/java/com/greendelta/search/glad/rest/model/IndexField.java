package com.greendelta.search.glad.rest.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.script.ScriptEngine;
import org.elasticsearch.script.SearchScript;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface IndexField {

	IndexType type();

	String defaultValue() default "";

	boolean required() default false;

	boolean aggregatable() default false;

	public static class MyExpertScriptEngine implements ScriptEngine {
		@Override
		public String getType() {
			return "expert_scripts";
		}

		@Override
		public <T> T compile(String scriptName, String scriptSource, ScriptContext<T> context,
				Map<String, String> params) {
			if (context.equals(SearchScript.CONTEXT) == false) {
				throw new IllegalArgumentException(getType() + " scripts cannot be used for context [" + context.name
						+ "]");
			}
			// we use the script "source" as the script identifier
			if ("pure_df".equals(scriptSource)) {
				SearchScript.Factory factory = (p, lookup) -> new SearchScript.LeafFactory() {
					final String field;
					final String term;
					{
						if (p.containsKey("field") == false) {
							throw new IllegalArgumentException("Missing parameter [field]");
						}
						if (p.containsKey("term") == false) {
							throw new IllegalArgumentException("Missing parameter [term]");
						}
						field = p.get("field").toString();
						term = p.get("term").toString();
					}

					@Override
					public SearchScript newInstance(LeafReaderContext context) throws IOException {
						PostingsEnum postings = context.reader().postings(new Term(field, term));
						if (postings == null) {
							// the field and/or term don't exist in this
							// segment, so always return 0
							return new SearchScript(p, lookup, context) {
								@Override
								public double runAsDouble() {
									return 0.0d;
								}
							};
						}
						return new SearchScript(p, lookup, context) {
							int currentDocid = -1;

							@Override
							public void setDocument(int docid) {
								// advance has undefined behavior calling with a
								// docid <= its current docid
								if (postings.docID() < docid) {
									try {
										postings.advance(docid);
									} catch (IOException e) {
										throw new UncheckedIOException(e);
									}
								}
								currentDocid = docid;
							}

							@Override
							public double runAsDouble() {
								if (postings.docID() != currentDocid) {
									// advance moved past the current doc, so
									// this doc has no occurrences of the term
									return 0.0d;
								}
								try {
									return postings.freq();
								} catch (IOException e) {
									throw new UncheckedIOException(e);
								}
							}
						};
					}

					@Override
					public boolean needs_score() {
						return false;
					}
				};
				return context.factoryClazz.cast(factory);
			}
			throw new IllegalArgumentException("Unknown script name " + scriptSource);
		}

		@Override
		public void close() {
			// optionally close resources
		}
	}

}