package com.greendelta.search.glad.rest;

import java.util.HashMap;
import java.util.Map;

import com.greendelta.search.wrapper.aggregations.SearchAggregation;
import com.greendelta.search.wrapper.aggregations.TermsAggregation;

class Aggregations {

	static final TermsAggregation PROCESS_TYPE = term("processType");
	static final TermsAggregation MODELLING_PRINCIPLE = term("modellingPrinciple");
	static final TermsAggregation MODELLING_APPROACH = term("modellingApproach");
	static final TermsAggregation NOMENCLATURE = term("supportedNomenclatures");
	static final TermsAggregation AGGREGATION_TYPE = term("aggregationType");
	static final TermsAggregation LICENSE_TYPE = term("licenseType");
	static final TermsAggregation FORMAT = term("format");

	static final SearchAggregation[] ALL = {
			PROCESS_TYPE, MODELLING_PRINCIPLE, MODELLING_APPROACH, NOMENCLATURE, AGGREGATION_TYPE, LICENSE_TYPE, FORMAT
	};

	static final Map<String, SearchAggregation> AS_MAP;

	static {
		AS_MAP = new HashMap<>();
		for (SearchAggregation aggregation : ALL) {
			AS_MAP.put(aggregation.name, aggregation);
		}
	}

	private static TermsAggregation term(String field) {
		return new TermsAggregation(field);
	}

}
