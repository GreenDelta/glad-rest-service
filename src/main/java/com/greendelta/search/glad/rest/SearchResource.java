package com.greendelta.search.glad.rest;

import java.util.HashSet;
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

import com.greendelta.search.wrapper.Conjunction;
import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.SearchFilterValue;
import com.greendelta.search.wrapper.SearchQuery;
import com.greendelta.search.wrapper.SearchQueryBuilder;
import com.greendelta.search.wrapper.SearchResult;
import com.greendelta.search.wrapper.aggregations.SearchAggregation;

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
		int page = Util.removeIntFilter("page", parameters, 1);
		int pageSize = Util.removeIntFilter("pageSize", parameters, SearchQuery.DEFAULT_PAGE_SIZE);
		String error = Util.checkParameters(parameters);
		if (error != null) {
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		SearchResult<Map<String, Object>> result = client.search(createQuery(query, page, pageSize, parameters));
		return Response.ok(result).build();
	}

	private SearchQuery createQuery(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		SearchQueryBuilder builder = new SearchQueryBuilder()
				.query(query, Defs.FULL_TEXT_FIELDS)
				.page(page)
				.pageSize(pageSize);
		for (SearchAggregation aggregation : Aggregations.ALL) {
			builder.aggregation(aggregation);
		}
		for (String filter : filters.keySet()) {
			SearchAggregation aggregation = Aggregations.AS_MAP.get(filter);
			for (String value : filters.get(filter)) {
				boolean isLongField = Defs.TIME_FIELDS.contains(filter);
				if (aggregation != null) {
					builder.aggregation(aggregation, value);
				} else if (!isLongField) {
					builder.filter(filter, SearchFilterValue.wildcard(value));
				} else if (value.startsWith(">")) {
					builder.filter(filter, SearchFilterValue.from(Long.parseLong(value.substring(1))));
				} else if (value.startsWith("<")) {
					builder.filter(filter, SearchFilterValue.to(Long.parseLong(value.substring(1))));
				} else if (value.contains(",")) {
					builder.filter(filter, getRangeValueSet(value), Conjunction.AND);
				} else {
					builder.filter(filter, SearchFilterValue.term(Long.parseLong(value)));
				}
			}
		}
		return builder.build();
	}

	private Set<SearchFilterValue> getRangeValueSet(String value) {
		int splitIndex = value.indexOf(',');
		Long from = Long.parseLong(value.substring(0, splitIndex));
		Long to = Long.parseLong(value.substring(splitIndex + 1));
		Set<SearchFilterValue> values = new HashSet<>();
		values.add(SearchFilterValue.from(from));
		values.add(SearchFilterValue.to(to));
		return values;
	}

}
