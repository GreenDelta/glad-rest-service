package com.greendelta.lca.search.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;

import com.greendelta.lca.search.SearchClient;
import com.greendelta.lca.search.elasticsearch.EsClient;
import com.greendelta.lca.search.elasticsearch.EsSettings;

public class SearchInitializer implements ServletContextListener {

	private static Node node;
	private static SearchClient client;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		createNode();
		createClient();
	}

	private static void createNode() {
		String home = Util.getProperty("search.home");
		Builder settingsBuilder = Settings.builder()
				.put("http.enabled", "false")
				.put("transport.type", "local")
				.put("path.home", home);
		Settings settings = settingsBuilder.build();
		node = new Node(settings);
		try {
			node.start();
		} catch (NodeValidationException e) {
			node = null;
			e.printStackTrace();
		}
	}

	private static void createClient() {
		client = new EsClient(node.client());
		Map<String, Object> settings = new HashMap<>();
		settings.put(EsSettings.CONFIG, Util.getResource("es-settings.json"));
		settings.put(EsSettings.MAPPINGS, Collections.singletonMap("PROCESS", Util.getResource("es-fields.json")));
		client.create(settings);
	}


	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (node == null)
			return;
		try {
			node.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static SearchClient getClient() {
		return client;
	}

}
