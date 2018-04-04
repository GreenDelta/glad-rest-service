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

	static void sectorInfo(Map<String, Object> map) {
		String sector = map.get("sector") != null ? map.get("sector").toString() : null;
		if (sector == null || sector.isEmpty())
			return;
		while (sector.length() < 8)
			sector = sector + "0";
		if (sector.length() > 8)
			sector = sector.substring(0, 8);
		List<String> sectorPaths = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			sectorPaths.add(sector.substring(0, i * 2 + 2));
		}
		map.put("sector", sector);
		map.put("sectorPaths", sectorPaths);
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
