package com.greendelta.lca.search.rest;

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

import com.greendelta.lca.search.SearchClient;
import com.greendelta.lca.search.SearchFilterValue.Type;
import com.greendelta.lca.search.SearchQuery;
import com.greendelta.lca.search.SearchQueryBuilder;
import com.greendelta.lca.search.SearchResult;
import com.greendelta.lca.search.aggregations.SearchAggregation;

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
		SearchResult result = client.search(createQuery(query, page, pageSize, parameters));
		return Response.ok(result).build();
	}

	private SearchQuery createQuery(String query, int page, int pageSize, Map<String, Set<String>> filters) {
		SearchQueryBuilder builder = new SearchQueryBuilder()
				.query(query)
				.page(page)
				.pageSize(pageSize);
		for (SearchAggregation aggregation : Aggregations.ALL) {
			builder.aggregation(aggregation);
		}
		for (String filter : filters.keySet()) {
			SearchAggregation aggregation = Aggregations.AS_MAP.get(filter);
			for (String value : filters.get(filter)) {
				if (aggregation == null) {
					builder.filter(filter, value, Type.WILDCART);
				} else {
					builder.aggregation(aggregation, value);
				}
			}
		}
		return builder.build();
	}

}
