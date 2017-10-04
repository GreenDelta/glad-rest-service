package com.greendelta.lca.search.rest;

import java.util.HashMap;
import java.util.Map;

import com.greendelta.lca.search.aggregations.SearchAggregation;
import com.greendelta.lca.search.aggregations.TermsAggregation;

class Aggregations {

	static final TermsAggregation PROCESS_TYPE = term("processType", "processType");
	static final TermsAggregation MODELLING_PRINCIPLE = term("modellingPrinciple", "modellingPrinciple");
	static final TermsAggregation MODELLING_APPROACH = term("modellingApproach", "modellingApproach");
	static final TermsAggregation NOMENCLATURE = term("supportedNomenclatures", "supportedNomenclatures");
	static final TermsAggregation AGGREGATION_TYPE = term("aggregationType", "aggregationType");
	static final TermsAggregation LICENSE_TYPE = term("licenseType", "licenseType");

	static final SearchAggregation[] ALL = {
			PROCESS_TYPE, MODELLING_PRINCIPLE, MODELLING_APPROACH, NOMENCLATURE, AGGREGATION_TYPE, LICENSE_TYPE
	};

	static final Map<String, SearchAggregation> AS_MAP;

	static {
		AS_MAP = new HashMap<>();
		for (SearchAggregation aggregation : ALL) {
			AS_MAP.put(aggregation.name, aggregation);
		}
	}

	private static TermsAggregation term(String name, String field) {
		return new TermsAggregation(name, field);
	}

}
