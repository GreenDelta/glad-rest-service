package com.greendelta.lca.search.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.core.util.IOUtils;

import com.greendelta.lca.search.aggregations.SearchAggregation;
import com.sun.jersey.api.uri.UriComponent;

class Util {

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
		return removeFilter(name, filters, "");
	}

	static int removeIntFilter(String name, Map<String, Set<String>> filters, int defaultValue) {
		String value = removeFilter(name, filters, Integer.toString(defaultValue));
		return Integer.parseInt(value);
	}

	private static String removeFilter(String name, Map<String, Set<String>> filters, String defaultValue) {
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

	static String checkParameters(Map<String, Set<String>> parameters) {
		for (String key : parameters.keySet()) {
			for (String value : parameters.get(key)) {
				if (Defs.LONG_FIELDS.contains(key)) {
					return "No filtering supported for field '" + key + "'";
				} else if (Defs.BOOLEAN_FIELDS.contains(key)) {
					String message = checkBoolean(key, value);
					if (message != null) {
						return message;
					}
				} else if (Defs.STRING_FIELDS.contains(key)) {
					List<String> valid = getValidValues(key);
					if (valid != null && !valid.contains(value)) {
						return "Value '" + value + "' is unsupported for field '" + key + "', valid values are: "
								+ join(valid);
					}
				} else {
					return "Unsupported field '" + key + "'";
				}
			}
		}
		return null;
	}

	static String checkValues(Map<String, Object> data) {
		for (SearchAggregation aggregation : Aggregations.ALL) {
			String field = aggregation.name;
			String val = data.get(field) != null ? data.get(field).toString() : null;
			List<String> valid = getValidValues(field);
			if (val == null || valid == null || valid.contains(val))
				continue;
			return "Value '" + val + "' is unsupported for field '" + field + "', valid values are: " + join(valid);
		}
		for (String key : data.keySet()) {
			String error = null;
			if (Defs.BOOLEAN_FIELDS.contains(key)) {
				error = checkBoolean(key, data.get(key));
			} else if (Defs.LONG_FIELDS.contains(key)) {
				error = checkLong(key, data.get(key));
			} else if (!Defs.STRING_FIELDS.contains(key)) {
				error = "Unsupported field '" + key + "'";
			}
			if (error != null) {
				return error;
			}
		}
		for (String key : Defs.REQUIRED_FIELDS) {
			if (data.get(key) != null && !data.get(key).toString().isEmpty())
				continue;
			if (!Defs.DEFAULT_VALUES.containsKey(key))
				return "Missing value for required field '" + key + "'";
			data.put(key, Defs.DEFAULT_VALUES.get(key));
		}
		return null;
	}

	private static String checkBoolean(String key, Object v) {
		if (v == null || v instanceof Boolean)
			return null;
		String value = v.toString().toLowerCase();
		if (!value.equals("true") && !value.equals("false"))
			return "Value '" + value + "' for field '" + key + "' is not a boolean value";
		return null;
	}

	private static String checkLong(String key, Object v) {
		if (v == null || v instanceof Long || v instanceof Integer)
			return null;
		try {
			Long.parseLong(v.toString());
		} catch (NumberFormatException e) {
			return "Value '" + v.toString() + "' for field '" + key + "' is not a long value";
		}
		return null;
	}

	private static String join(List<String> values) {
		String s = "[";
		for (int i = 0; i < values.size(); i++) {
			if (i != 0) {
				s += ", ";
			}
			s += values.get(i);
		}
		return s + "]";
	}

	private static List<String> getValidValues(String filter) {
		if (Aggregations.PROCESS_TYPE.name.equals(filter))
			return Defs.PROCESS_TYPES;
		if (Aggregations.AGGREGATION_TYPE.name.equals(filter))
			return Defs.AGGREGATION_TYPES;
		if (Aggregations.LICENSE_TYPE.name.equals(filter))
			return Defs.LICENSE_TYPES;
		if (Aggregations.MODELLING_PRINCIPLE.name.equals(filter))
			return Defs.MODELLING_PRINCIPLES;
		if (Aggregations.MODELLING_APPROACH.name.equals(filter))
			return Defs.MODELLING_APPROACHES;
		if (Aggregations.FORMAT.name.equals(filter))
			return Defs.FORMATS;
		return null;
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

}
