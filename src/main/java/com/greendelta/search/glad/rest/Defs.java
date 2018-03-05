package com.greendelta.search.glad.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Defs {

	static final String[] FULL_TEXT_FIELDS = { "name", "category", "completeness", "sampleRepresentativeness",
			"samplingProcedure", "technology", "representativeness", "biogenicCarbon", "description" };
	static final List<String> REQUIRED_FIELDS = list("refId", "dataSetUrl", "format", "name", "processType",
			"aggregationType", "licenseType", "modellingPrinciple", "modellingApproach");
	static final List<String> STRING_FIELDS = list("refId", "processType", "supportedNomenclatures",
			"modellingPrinciple", "modellingApproach", "aggregationType", "licenseType", "name", "category",
			"categories", "categoryPaths", "location", "completeness", "sampleRepresentativeness", "samplingProcedure",
			"technology", "representativeness", "biogenicCarbon", "reviewer", "copyrightHolder", "license", "contact",
			"description", "dataSetUrl", "format");
	static final List<String> CALCULATED_FIELDS = list("categoryPaths");
	static final List<String> TIME_FIELDS = list("validFrom", "validUntil");
	static final List<String> BOOLEAN_FIELDS = list("reviewed", "copyrightProtected");
	static final List<String> FILTER_TYPES = list("TERM");
	static final List<String> AGGREGATION_TYPES = list("HORIZONTAL", "VERTICAL", "NONE", "UNKNOWN");
	static final List<String> LICENSE_TYPES = list("FREE", "MIXED", "CHARGED", "UNKNOWN");
	static final List<String> MODELLING_PRINCIPLES = list("ATTRIBUTIONAL", "CONSEQUENTIAL", "UNKNOWN");
	static final List<String> MODELLING_APPROACHES = list("PHYSICAL", "ECONOMIC", "CAUSAL", "NONE", "UNKNOWN");
	static final List<String> NOMENCLATURES = list("ILCD");
	static final List<String> PROCESS_TYPES = list("UNIT", "SYSTEM", "UNKNOWN");
	static final List<String> FORMATS = list("ILCD", "JSON-LD", "OTHER", "UNKNOWN");
	static final Map<String, String> DEFAULT_VALUES = new HashMap<>();

	static List<String> list(String... values) {
		return Arrays.asList(values);
	}

	static {
		DEFAULT_VALUES.put("aggregationType", "UNKNOWN");
		DEFAULT_VALUES.put("licenseType", "UNKNOWN");
		DEFAULT_VALUES.put("modellingPrinciple", "UNKNOWN");
		DEFAULT_VALUES.put("modellingApproach", "UNKNOWN");
		DEFAULT_VALUES.put("processType", "UNKNOWN");
		DEFAULT_VALUES.put("format", "UNKNOWN");
	}

}
