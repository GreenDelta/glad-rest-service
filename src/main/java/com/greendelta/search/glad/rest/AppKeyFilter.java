package com.greendelta.search.glad.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

public class AppKeyFilter implements Filter {

	private static String API_KEY;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		API_KEY = Util.getProperty("api.key");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (!API_KEY.equals(httpRequest.getHeader("api-key"))) {
			httpResponse.sendError(Status.UNAUTHORIZED.getStatusCode());
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
