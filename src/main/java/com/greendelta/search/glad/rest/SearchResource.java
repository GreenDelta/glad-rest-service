package com.greendelta.search.glad.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

@Path("search")
public class SearchResource {

	private final SearchClient client;

	public SearchResource() {
		client = SearchInitializer.getClient();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context UriInfo uriInfo) {
		Map<String, Set<String>> parameters = Util.getQueryParameters(uriInfo);
		String query = Util.removeStringFilter("query", parameters);
		String sortBy = Util.removeStringFilter("sortBy", parameters);
		String sortOrderValue = Util.removeStringFilter("sortOrder", parameters, SearchSorting.ASC.name());
		SearchSorting sortOrder = SearchSorting.valueOf(sortOrderValue);
		int page = Util.removeIntFilter("page", parameters, 1);
		int pageSize = Util.removeIntFilter("pageSize", parameters, SearchQuery.DEFAULT_PAGE_SIZE);
		Set<String> queryFields = parameters.remove("queryFields");
		Set<String> aggregations = parameters.remove("aggregate");
		try {
			Data.parameters(parameters);
			SearchResult<Map<String, Object>> result = client
					.search(createQuery(query, page, pageSize, sortBy, sortOrder, parameters, queryFields, aggregations));
			for (AggregationResult aResult : result.aggregations) {
				if (aResult.name.equals("categoryPaths")) {
					aResult.group("/");
				} else if (aResult.name.equals("sectorPaths")) {
					aResult.group("", this::splitSectors);
				}
			}
			return Response.ok(result).build();
		} catch (InvalidInputException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	private String[] splitSectors(String value) {
		String[] split = new String[value.length() / 2];
		for (int i = 0; i < split.length; i++) {
			split[i] = value.substring(i * 2, i * 2 + 2);
		}
		return split;
	}

	private SearchQuery createQuery(String query, int page, int pageSize, String sortBy, SearchSorting sortOrder,
			Map<String, Set<String>> filters, Set<String> queryFields, Set<String> aggregations)
			throws InvalidInputException {
		SearchQueryBuilder builder = new SearchQueryBuilder()
				.query(query, getQueryFields(queryFields))
				.page(page)
				.pageSize(pageSize);
		if (sortBy != null && !sortBy.isEmpty()) {
			builder.sortBy(sortBy, sortOrder);
		}
		for (SearchAggregation aggregation : getAggregations(aggregations)) {
			builder.aggregation(aggregation);
		}
		for (String filter : filters.keySet()) {
			SearchAggregation aggregation = Data.AGGREGATIONS.get(filter);
			for (String value : filters.get(filter)) {
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

	private Collection<SearchAggregation> getAggregations(Set<String> names) throws InvalidInputException {
		if (names == null)
			return Data.AGGREGATIONS.values();
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

	private Set<SearchFilterValue> getRangeValueSet(IndexType type, String value) {
		int splitIndex = value.indexOf(',');
		Object from = type.parse(value.substring(0, splitIndex));
		Object to = type.parse(value.substring(splitIndex + 1));
		Set<SearchFilterValue> values = new HashSet<>();
		values.add(SearchFilterValue.from(from));
		values.add(SearchFilterValue.to(to));
		return values;
	}

}
