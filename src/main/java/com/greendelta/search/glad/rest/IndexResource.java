package com.greendelta.search.glad.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.greendelta.search.glad.rest.Data.InvalidInputException;
import com.greendelta.search.wrapper.SearchClient;

@Path("search/index")
public class IndexResource {

	private final SearchClient client;

	public IndexResource() {
		client = SearchInitializer.getClient();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") String id) {
		Map<String, Object> data = client.get(id);
		if (data == null)
			return Response.status(Status.NOT_FOUND).build();
		return Response.ok(data).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(Map<String, Object> content) {
		try {
			Data.validateValues(content);
		} catch (InvalidInputException e) {
			return Response.status(422).entity(e.getMessage()).build();
		}
		String id = content.get("refId").toString();
		boolean exists = client.get(id) != null;
		if (exists)
			return Response.status(Status.CONFLICT).location(url("search/" + id)).build();
		DataFill.categoryInfo(content);
		DataFill.sectorInfo(content);
		DataFill.timeInfo(content);
		client.index(id, content);
		return Response.created(url(id)).build();
	}

	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@PathParam("id") String id, Map<String, Object> content) {
		boolean exists = client.get(id) != null;
		if (!id.equals(content.get("refId")))
			return Response.status(422).entity("refId field did not match id in url").build();
		try {
			Data.validateValues(content);
		} catch (InvalidInputException e) {
			return Response.status(422).entity(e.getMessage()).build();
		}
		DataFill.categoryInfo(content);
		DataFill.sectorInfo(content);
		DataFill.timeInfo(content);
		client.index(id, content);
		if (exists)
			return Response.noContent().location(url("search/" + id)).build();
		return Response.created(url("")).build();
	}

	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") String id) {
		client.remove(id);
		return Response.noContent().build();
	}

	private URI url(String url) {
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
