package com.greendelta.search.glad.rest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.greendelta.search.wrapper.SearchClient;
import com.greendelta.search.wrapper.es.EsClient;

public class SearchInitializer implements ServletContextListener {

	private static SearchClient client;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		createClient();
	}

	private static void createClient() {
		String cluster = Util.getProperty("search.cluster");
		String host = Util.getProperty("search.host");
		String index = Util.getProperty("search.index");
		Builder settingsBuilder = Settings.builder()
				.put("cluster.name", cluster);
		Settings settings = settingsBuilder.build();
		TransportClient tClient = new PreBuiltTransportClient(settings);
		try {
			tClient.addTransportAddress(new TransportAddress(InetAddress.getByName(host), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		client = new EsClient(tClient, index, "PROCESS");
		Map<String, String> indexSettings = new HashMap<>();
		indexSettings.put("config", Util.getResource("es-settings.json"));
		indexSettings.put("mapping", Util.getResource("es-fields.json"));
		client.create(indexSettings);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

	static SearchClient getClient() {
		return client;
	}

}
