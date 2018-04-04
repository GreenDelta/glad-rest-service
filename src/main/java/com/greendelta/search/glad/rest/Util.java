package com.greendelta.search.glad.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.core.util.IOUtils;

import com.sun.jersey.api.uri.UriComponent;

class Util {

	private static final Properties PROPERTIES = new Properties();

	static {
		try {
			PROPERTIES.load(Util.class.getResourceAsStream("app.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Map<String, Set<String>> getQueryParameters(UriInfo uriInfo) {
		Map<String, Set<String>> filters = new HashMap<>();
		for (String key : uriInfo.getQueryParameters().keySet()) {
			Set<String> filterBy = filters.get(key);
			String dKey = decode(key);
			if (filterBy == null)
				filters.put(dKey, filterBy = new HashSet<>());
			List<String> values = uriInfo.getQueryParameters().get(key);
			if (values == null)
				continue;
			for (String value : values) {
				filterBy.add(decode(value));
			}
		}
		return filters;
	}

	static String removeStringFilter(String name, Map<String, Set<String>> filters) {
		return removeStringFilter(name, filters, "");
	}

	static int removeIntFilter(String name, Map<String, Set<String>> filters, int defaultValue) {
		String value = removeStringFilter(name, filters, Integer.toString(defaultValue));
		return Integer.parseInt(value);
	}

	static String removeStringFilter(String name, Map<String, Set<String>> filters, String defaultValue) {
		Set<String> value = filters.remove(name);
		if (value == null)
			return defaultValue;
		if (value.size() == 0)
			return defaultValue;
		String first = value.iterator().next();
		if (first == null || first.isEmpty())
			return defaultValue;
		return first;
	}

	private static String decode(String value) {
		return UriComponent.decode(value, UriComponent.Type.PATH_SEGMENT);
	}

	static String getResource(String name) {
		InputStream stream = Util.class.getResourceAsStream(name);
		if (stream == null)
			return null;
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(new InputStreamReader(stream), writer);
		} catch (IOException e) {
			return null;
		}
		return writer.toString();
	}

	static String getProperty(String key) {
		return PROPERTIES.getProperty(key);
	}

}
