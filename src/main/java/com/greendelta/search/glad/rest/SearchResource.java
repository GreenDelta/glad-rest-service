package com.greendelta.search.glad.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.greendelta.search.glad.rest.Data.InvalidInputException;
import com.greendelta.search.glad.rest.model.IndexType;
import com.greendelta.search.wrapper.Conjunction;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.SearchSorting;
import com.greendelta.search.wrapper.aggregations.SearchAggregation;
import com.greendelta.search.wrapper.aggregations.results.AggregationResult;
import com.greendelta.search.wrapper.score.Score;

@Path("search")
public class SearchResource {

	private final SearchClient client;

	public SearchResource() {
		client = SearchInitializer.getClient();
	}

	private class Input {
		private final String query;
		private final String sortBy;
		private final SearchSorting sortOrder;
		private final int page;
		private final int pageSize;
		private final String[] queryFields;
		private final List<SearchAggregation> aggregations;
		private final Map<String, Set<String>> filters;
		private final List<Score> scores;

		private Input(UriInfo uriInfo) throws InvalidInputException {
			Map<String, Set<String>> parameters = Util.getQueryParameters(uriInfo);
			query = Util.removeStringFilter("query", parameters);
			sortBy = Util.removeStringFilter("sortBy", parameters);
			String sortOrderValue = Util.removeStringFilter("sortOrder", parameters, SearchSorting.ASC.name());
			sortOrder = SearchSorting.valueOf(sortOrderValue);
			page = Util.removeIntFilter("page", parameters, 1);
			pageSize = Util.removeIntFilter("pageSize", parameters, SearchQuery.DEFAULT_PAGE_SIZE);
			queryFields = getQueryFields(parameters.remove("queryFields"));
			aggregations = getAggregations(parameters.remove("aggregate"));
			if (sortBy == null || sortBy.isEmpty()) {
				scores = getScores(parameters);
			} else {
				scores = new ArrayList<>();
			}
			filters = parameters;
			Data.validateParameters(parameters);
		}

		private List<SearchAggregation> getAggregations(Set<String> names) throws InvalidInputException {
			if (names == null)
				return new ArrayList<>(Data.AGGREGATIONS.values());
			List<SearchAggregation> aggregations = new ArrayList<>();
			for (String name : names) {
				SearchAggregation aggregation = Data.AGGREGATIONS.get(name);
				if (aggregation == null)
					throw new InvalidInputException("No aggregation available for field '" + name + "'");
				aggregations.add(aggregation);
			}
			return aggregations;
		}

		private String[] getQueryFields(Set<String> names) throws InvalidInputException {
			if (names == null || names.isEmpty())
				return Data.FULL_TEXT_FIELDS;
			List<String> all = Arrays.asList(Data.FULL_TEXT_FIELDS);
			for (String name : names)
				if (!all.contains(name))
					throw new InvalidInputException("Field '" + name + "' is not a query field");
			return names.toArray(new String[names.size()]);
		}

		private List<Score> getScores(Map<String, Set<String>> parameters) throws InvalidInputException {
			List<Score> scores = new ArrayList<>();
			addScore(scores, parameters, "amountDeviation", Scores::amountDeviation);
			addScore(scores, parameters, "completeness", Scores::completeness);
			addScore(scores, parameters, "representativeness", Scores::representativeness);
			addTimeScore(scores, parameters);
			addGeographyScore(scores, parameters);
			addTechnologyScore(scores, parameters);
			for (String key : parameters.keySet())
				if (key.startsWith("score-"))
					throw new InvalidInputException("No scoring available for field '" + key.substring(6) + "'");
			return scores;
		}

		private void addScore(List<Score> scores, Map<String, Set<String>> parameters, String type,
				Supplier<Score> supplier) {
			if (!parameters.containsKey("score-" + type))
				return;
			parameters.remove("score-" + type);
			scores.add(supplier.get());
		}

		private void addTimeScore(List<Score> scores, Map<String, Set<String>> parameters) {
			if (!parameters.containsKey("score-time"))
				return;
			Long time = Util.removeLongFilter("score-time", parameters);
			scores.add(Scores.time(time));
		}

		private void addGeographyScore(List<Score> scores, Map<String, Set<String>> parameters)
				throws InvalidInputException {
			if (!parameters.containsKey("score-geography"))
				return;
			String geography = Util.removeStringFilter("score-geography", parameters);
			String error = "score-geography must define latitude and longitude separated by a , e.g.: 52.13,43.12";
			if (!geography.contains(","))
				throw new InvalidInputException(error);
			try {
				double latitude = Double.parseDouble(geography.split(",")[0].trim());
				double longitude = Double.parseDouble(geography.split(",")[1].trim());
				scores.add(Scores.geography(latitude, longitude));
			} catch (NumberFormatException e) {
				throw new InvalidInputException(error);
			}
		}

		private void addTechnologyScore(List<Score> scores, Map<String, Set<String>> parameters)
				throws InvalidInputException {
			if (!parameters.containsKey("score-technology"))
				return;
			String technology = Util.removeStringFilter("score-technology", parameters);
			String error = "score-technology must define a valid unspsc code and co2pe code separated by a , e.g.: 50454302,1.1.1";
			if (!technology.contains(","))
				throw new InvalidInputException(error);
			String unspscCode = getValidatedUnspscCode(technology, error);
			String co2peCode = getValidatedCo2peCode(technology, error);
			scores.add(Scores.technology(unspscCode, co2peCode));
		}

		private String getValidatedUnspscCode(String technology, String error) throws InvalidInputException {
			String unspscCode = technology.split(",")[0];
			if (unspscCode.length() > 8)
				throw new InvalidInputException(error);
			try {
				Integer.parseInt(unspscCode);
			} catch (NumberFormatException e) {
				throw new InvalidInputException(error);
			}
			while (unspscCode.length() < 8) {
				unspscCode = unspscCode + "0";
			}
			return unspscCode;
		}

		private String getValidatedCo2peCode(String technology, String error) throws InvalidInputException {
			String co2peCode = technology.split(",")[1];
			if (co2peCode.split(".").length > 3)
				throw new InvalidInputException(error);
			for (String s : co2peCode.split(".")) {
				try {
					Integer.parseInt(s);
				} catch (NumberFormatException e) {
					throw new InvalidInputException(error);
				}
			}
			while (co2peCode.split("\\.").length < 3) {
				co2peCode = co2peCode + ".0";
			}
			return co2peCode;
		}

	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context UriInfo uriInfo) {
		try {
			Input in = new Input(uriInfo);
			SearchResult<Map<String, Object>> result = client.search(createQuery(in));
			group(result.aggregations);
			return Response.ok(result).build();
		} catch (InvalidInputException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	private SearchQuery createQuery(Input in) throws InvalidInputException {
		SearchQueryBuilder builder = new SearchQueryBuilder()
				.query(in.query, in.queryFields)
				.page(in.page)
				.pageSize(in.pageSize);
		if (in.sortBy != null && !in.sortBy.isEmpty()) {
			builder.sortBy(in.sortBy, in.sortOrder);
		}
		for (SearchAggregation aggregation : in.aggregations) {
			builder.aggregation(aggregation);
		}
		for (Score score : in.scores) {
			builder.score(score);
		}
		for (String filter : in.filters.keySet()) {
			SearchAggregation aggregation = Data.AGGREGATIONS.get(filter);
			for (String value : in.filters.get(filter)) {
				IndexType type = Data.TYPES.get(filter);
				if (type.isNumeric() && value.startsWith(">")) {
					builder.filter(filter, SearchFilterValue.from(type.parse(value.substring(1))));
				} else if (type.isNumeric() && value.startsWith("<")) {
					builder.filter(filter, SearchFilterValue.to(type.parse(value.substring(1))));
				} else if (type.isNumeric() && value.contains(",")) {
					builder.filter(filter, getRangeValueSet(type, value), Conjunction.AND);
				} else if (aggregation != null) {
					builder.aggregation(aggregation, value);
				} else if (type == IndexType.TEXT) {
					builder.filter(filter, SearchFilterValue.wildcard(value));
				} else {
					builder.filter(filter, SearchFilterValue.term(type.parse(value)));
				}
			}
		}
		return builder.build();
	}

	private Set<SearchFilterValue> getRangeValueSet(IndexType type, String value) {
		int splitIndex = value.indexOf(',');
		Object from = type.parse(value.substring(0, splitIndex));
		Object to = type.parse(value.substring(splitIndex + 1));
		Set<SearchFilterValue> values = new HashSet<>();
		values.add(SearchFilterValue.from(from));
		values.add(SearchFilterValue.to(to));
		return values;
	}

	private void group(List<AggregationResult> aggregations) {
		for (AggregationResult aResult : aggregations) {
			if (aResult.name.equals("categoryPaths")) {
				aResult.group("/");
			} else if (aResult.name.equals("sectorPaths")) {
				aResult.group("", this::splitSectors);
			}
		}
	}

	private String[] splitSectors(String value) {
		String[] split = new String[value.length() / 2];
		for (int i = 0; i < split.length; i++) {
			split[i] = value.substring(i * 2, i * 2 + 2);
		}
		return split;
	}

}
