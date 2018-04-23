package com.greendelta.search.glad.rest;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.greendelta.search.glad.rest.model.Index;
import com.greendelta.search.glad.rest.model.IndexField;
import com.greendelta.search.glad.rest.model.IndexType;
import com.greendelta.search.wrapper.aggregations.SearchAggregation;
import com.greendelta.search.wrapper.aggregations.TermsAggregation;

class Data {

	public static final Map<String, SearchAggregation> AGGREGATIONS;
	public static final Map<String, IndexType> TYPES;
	public static final String[] FULL_TEXT_FIELDS;
	private static final Map<String, Boolean> REQUIRED;
	private static final Map<String, String> DEFAULTS;
	private static final Map<String, Set<String>> VALID_VALUES;

	static {
		Map<String, SearchAggregation> aggregationsMap = new HashMap<>();
		Map<String, IndexType> types = new HashMap<>();
		Map<String, Boolean> required = new HashMap<>();
		Map<String, String> defaults = new HashMap<>();
		Map<String, Set<String>> validValues = new HashMap<>();
		Set<String> fullText = new HashSet<>();
		for (Field f : Index.class.getDeclaredFields()) {
			IndexField info = f.getAnnotation(IndexField.class);
			if (info == null)
				continue;
			String field = f.getName();
			if (info.aggregatable()) {
				SearchAggregation aggregation = new TermsAggregation(field);
				aggregationsMap.put(field, aggregation);
			}
			types.put(field, info.type());
			required.put(field, info.required());
			if (!info.defaultValue().equals(""))
				defaults.put(field, info.defaultValue());
			if (f.getType().isEnum()) {
				Set<String> constants = new HashSet<>();
				for (Object o : f.getType().getEnumConstants()) {
					constants.add(((Enum<?>) o).name());
				}
				validValues.put(field, constants);
			}
			if (info.type() == IndexType.TEXT) {
				fullText.add(field);
			}
		}
		TYPES = Collections.unmodifiableMap(types);
		REQUIRED = Collections.unmodifiableMap(required);
		AGGREGATIONS = Collections.unmodifiableMap(aggregationsMap);
		DEFAULTS = Collections.unmodifiableMap(defaults);
		VALID_VALUES = Collections.unmodifiableMap(validValues);
		FULL_TEXT_FIELDS = fullText.toArray(new String[fullText.size()]);
	}

	static void validateParameters(Map<String, Set<String>> parameters) throws InvalidInputException {
		for (String field : parameters.keySet()) {
			for (String value : parameters.get(field)) {
				Data.checkValue(field, value);
			}
		}
	}

	static void validateValues(Map<String, Object> data) throws InvalidInputException {
		for (String field : data.keySet()) {
			if (field.equals("categoryPaths") || field.equals("sectorPaths") || field.equals("category"))
				throw new InvalidInputException("Field can not be set, but is calculated internally");
			String value = data.get(field) != null ? data.get(field).toString() : null;
			Data.checkValue(field, value);
			if (value != null)
				continue;
			String defaultValue = Data.DEFAULTS.get(field);
			if (defaultValue == null && Data.REQUIRED.get(field))
				throw new InvalidInputException("Missing value for required field '" + field + "'");
			data.put(field, defaultValue);
		}
	}

	private static void checkValue(String field, String value) throws InvalidInputException {
		IndexType type = TYPES.get(field);
		if (type == null)
			throw new InvalidInputException("Unsupported field '" + field + "'");
		switch (type) {
		case DATE:
			checkDate(field, value);
			break;
		case INTEGER:
			checkInteger(field, value);
			break;
		case BOOLEAN:
			checkBoolean(field, value);
			break;
		case DOUBLE:
			checkDouble(field, value);
			break;
		case KEYWORD:
			checkString(field, value);
			break;
		case TEXT:
			checkString(field, value);
			break;
		}
	}

	private static void checkBoolean(String field, Object v) throws InvalidInputException {
		if (v == null || v instanceof Boolean)
			return;
		String value = v.toString().toLowerCase();
		if (value.equals("true") || value.equals("false"))
			return;
		String message = "Value '" + value + "' for field '" + field + "' is not a boolean value";
		throw new InvalidInputException(message);
	}

	private static void checkDate(String field, Object v) throws InvalidInputException {
		if (v == null)
			return;
		String value = v.toString();
		if (value.contains(",")) {
			int index = value.indexOf(',');
			checkInteger(field, value.substring(0, index));
			checkInteger(field, value.substring(index + 1));
		} else {
			if (value.startsWith("<") || value.startsWith(">")) {
				value = value.substring(1);
			}
			checkInteger(field, value);
		}
	}

	private static void checkInteger(String field, Object v) throws InvalidInputException {
		if (v == null || v instanceof Long || v instanceof Integer)
			return;
		try {
			Long.parseLong(v.toString());
		} catch (NumberFormatException e) {
			String message = "Value '" + v.toString() + "' for field '" + field + "' is not an integer value";
			throw new InvalidInputException(message);
		}
	}

	private static void checkDouble(String field, Object v) throws InvalidInputException {
		if (v == null || v instanceof Double || v instanceof Float)
			return;
		try {
			Double.parseDouble(v.toString());
		} catch (NumberFormatException e) {
			String message = "Value '" + v.toString() + "' for field '" + field + "' is not a double value";
			throw new InvalidInputException(message);
		}
	}

	private static void checkString(String field, Object v) throws InvalidInputException {
		if (v == null)
			return;
		String value = v.toString();
		Set<String> valid = VALID_VALUES.get(field);
		if (valid == null || valid.contains(value))
			return;
		String message = "Value '" + value + "' is unsupported for field '" + field + "', "
				+ "valid values are: " + join(valid);
		throw new InvalidInputException(message);
	}

	private static String join(Iterable<?> values) {
		String s = "[";
		for (Object value : values) {
			if (!s.equals("[")) {
				s += ", ";
			}
			s += value.toString();
		}
		return s + "]";
	}

	public static class InvalidInputException extends Exception {

		private static final long serialVersionUID = 5248278072282483292L;

		public InvalidInputException(String message) {
			super(message);
		}

	}

}
