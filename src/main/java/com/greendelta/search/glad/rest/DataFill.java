package com.greendelta.search.glad.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class DataFill {

	static void timeInfo(Map<String, Object> content) {
		if (content.containsKey("validFrom") && !content.containsKey("validFromYear")) {
			content.put("validFromYear", toYear(content.get("validFrom")));
		} else if (!content.containsKey("validFrom") && content.containsKey("validFromYear")) {
			content.put("validFrom", toDate(content.get("validFromYear")));
		}
		if (content.containsKey("validUntil") && !content.containsKey("validUntilYear")) {
			content.put("validUntilYear", toYear(content.get("validUntil")));
		} else if (!content.containsKey("validUntil") && content.containsKey("validUntilYear")) {
			content.put("validUntil", toDate(content.get("validUntilYear")));
		}
	}

	static void unspscCodeInfo(Map<String, Object> map) {
		String code = map.get("unspscCode") != null ? map.get("unspscCode").toString() : null;
		if (code == null || code.isEmpty())
			return;
		while (code.length() < 8)
			code = code + "0";
		if (code.length() > 8)
			code = code.substring(0, 8);
		List<String> paths = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			paths.add(code.substring(0, i * 2 + 2));
		}
		map.put("unspscCode", code);
		map.put("unspscPaths", paths);
	}

	static void co2peCodeInfo(Map<String, Object> map) {
		String code = map.get("co2peCode") != null ? map.get("co2peCode").toString() : null;
		if (code == null || code.isEmpty())
			return;
		while (code.split(".").length < 3)
			code = code + ".0";
		List<String> paths = new ArrayList<>();
		String current = "";
		for (String part : code.split(".")) {
			if (!current.isEmpty())
				current += ".";
			current += part;
			paths.add(current);
		}
		map.put("co2peCode", code);
		map.put("co2pePaths", paths);
	}

	static void categoryInfo(Map<String, Object> map) {
		List<String> categories = toList(map.get("categories"));
		if (categories == null || categories.size() == 0)
			return;
		String category = "";
		for (String cat : categories) {
			if (!category.isEmpty()) {
				category += '/';
			}
			category += cat;
		}
		List<String> categoryPaths = new ArrayList<>();
		String current = null;
		for (String cat : categories) {
			if (current == null) {
				current = cat;
			} else {
				current += '/' + cat;
			}
			categoryPaths.add(current);
		}
		map.put("categories", categories);
		map.put("categoryPaths", categoryPaths);
		map.put("category", category);
	}

	private static Integer toYear(Object date) {
		if (date == null || date.toString().isEmpty())
			return null;
		long l = Long.parseLong(date.toString());
		if (l == 0)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(l);
		return c.get(Calendar.YEAR);
	}

	private static Long toDate(Object date) {
		if (date == null || date.toString().isEmpty())
			return null;
		int l = Integer.parseInt(date.toString());
		if (l == 0)
			return null;
		Calendar c = Calendar.getInstance();
		c.set(l, 0, 1, 0, 0, 0);
		return c.getTimeInMillis();
	}

	@SuppressWarnings("unchecked")
	private static List<String> toList(Object value) {
		if (value == null)
			return Collections.emptyList();
		if (value instanceof Collection)
			return new ArrayList<>((Collection<String>) value);
		if (value instanceof String[])
			return Arrays.asList((String[]) value);
		return Collections.singletonList(value.toString());
	}

}
