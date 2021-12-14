package com.greendelta.search.glad.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;

import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.os.OsRestClient;

public class SearchInitializer implements ServletContextListener {

	private static SearchClient client;
	private static RestHighLevelClient tClient;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		createClient();
	}

	private static void createClient() {
		String host = Util.getProperty("search.host");
		String index = Util.getProperty("search.index");
		String portString = Util.getProperty("search.port");
		if (portString == null || portString.isEmpty()) {
			portString = "9200";
		}
		int port = Integer.parseInt(portString);
		tClient = new RestHighLevelClient(RestClient.builder(
				new HttpHost(host, port, "http"),
				new HttpHost(host, port + 1, "http")));
		client = new OsRestClient(tClient, index);
		Map<String, String> indexSettings = new HashMap<>();
		indexSettings.put("config", Util.getResource("es-settings.json"));
		indexSettings.put("mapping", Util.getResource("es-fields.json"));
		client.create(indexSettings);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (tClient == null)
			return;
		try {
			tClient.close();
		} catch (IOException e) {
		}
	}

	static SearchClient getClient() {
		return client;
	}

}
