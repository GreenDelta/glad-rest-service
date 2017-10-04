package com.greendelta.lca.search.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Defs {

	static final List<String> STRING_FIELDS = new ArrayList<>(Arrays.asList(
			"refId", "processType", "supportedNomenclatures", "modellingPrinciple", "modellingApproach",
			"aggregationType", "licenseType", "name", "category", "location", "completeness",
			"sampleRepresentativeness", "samplingProcedure", "technology", "representativeness", "biogenicCarbon",
			"reviewer", "copyrightHolder", "license", "contact", "description", "dataSetUrl", "format"));
	static final List<String> LONG_FIELDS = new ArrayList<>(Arrays.asList(
			"validFrom", "validUntil"));
	static final List<String> BOOLEAN_FIELDS = new ArrayList<>(Arrays.asList(
			"reviewed", "copyrightProtected"));
	static final List<String> REQUIRED_FIELDS = new ArrayList<>(Arrays.asList(
			"refId", "dataSetUrl", "format", "name", "processType", "aggregationType", "licenseType",
			"modellingPrinciple", "modellingApproach"));
	static final List<String> FILTER_TYPES = new ArrayList<>(Arrays.asList(
			"TERM"));
	static final List<String> AGGREGATION_TYPES = new ArrayList<>(Arrays.asList(
			"HORIZONTAL", "VERTICAL", "NONE", "UNKNOWN"));
	static final List<String> LICENSE_TYPES = new ArrayList<>(Arrays.asList(
			"FREE", "MIXED", "CHARGED", "UNKNOWN"));
	static final List<String> MODELLING_PRINCIPLES = new ArrayList<>(Arrays.asList(
			"ATTRIBUTIONAL", "CONSEQUENTIAL", "UNKNOWN"));
	static final List<String> MODELLING_APPROACHES = new ArrayList<>(Arrays.asList(
			"PHYSICAL", "ECONOMIC", "CAUSAL", "NONE", "UNKNOWN"));
	static final List<String> NOMENCLATURES = new ArrayList<>(Arrays.asList(
			"ILCD"));
	static final List<String> PROCESS_TYPES = new ArrayList<>(Arrays.asList(
			"UNIT", "SYSTEM", "UNKNOWN"));
	static final List<String> FORMATS = new ArrayList<>(Arrays.asList(
			"ILCD", "JSON-LD", "OTHER", "UNKNOWN"));
	static final Map<String, String> DEFAULT_VALUES = new HashMap<>();

	static {
		DEFAULT_VALUES.put("aggregationType", "UNKNOWN");
		DEFAULT_VALUES.put("licenseType", "UNKNOWN");
		DEFAULT_VALUES.put("modellingPrinciple", "UNKNOWN");
		DEFAULT_VALUES.put("modellingApproach", "UNKNOWN");
		DEFAULT_VALUES.put("processType", "UNKNOWN");
		DEFAULT_VALUES.put("format", "UNKNOWN");
	}

}
