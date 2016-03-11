/**
 * This document is a part of the source code and related artifacts
 * for GA2SA, an open source code for Google Analytics to 
 * Salesforce Analytics integration.
 *
 * Copyright © 2015 Cervello Inc.,
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.ga2sa.salesforce;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.DashboardType;

import org.apache.commons.lang3.StringEscapeUtils;

import play.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author SLegostaev
 *
 */
public class DashboardTemplatesManager {
	
	private static ObjectMapper mapper = new ObjectMapper();
	private static Map<DashboardType, DashboardTemplate> templates;
	
	static {
		loadTemplates();
	}
	
	public static ObjectNode getTemplate(DashboardType dashboardType) {
		return templates.get(dashboardType).objectNode;
	}
	
	public static Collection<DashboardTemplate> getAllTemplates() {
		return templates.values();
	}
	
	public static void loadTemplates() {
		templates = new TreeMap<DashboardType, DashboardTemplate>();
		Arrays.stream(DashboardType.values()).forEach(dashboartType -> templates.put(dashboartType, loadJson(dashboartType)));

	}
	
	private static DashboardTemplate loadJson(DashboardType type) {
		DashboardTemplate dashboardTemplate = new DashboardTemplate(type);
		try {
			File file = new File("app/templates/" + type.name() + ".json");
			ObjectNode objNode = (ObjectNode) mapper.readTree(file);
			objNode.remove("name");
			
			List<JsonNode> queries = objNode.findValues("query");
			queries.forEach(queryNode -> {
				ObjectNode queryObject = (ObjectNode)queryNode.get("query");
				String queryString = StringEscapeUtils.escapeHtml4(queryObject.toString());
				((ObjectNode)queryNode).put("query", queryString).put("version", 36);
			});
			dashboardTemplate.objectNode = objNode;
			dashboardTemplate.steps = getSteps(objNode);
			
		} catch (IOException e) {
			Logger.debug(e.getMessage(), e);
		}
		return dashboardTemplate;
	}
	
	private static List<DashboardStep> getSteps(ObjectNode objNode) {
		List<DashboardStep> steps = new ArrayList<DashboardStep>();
		objNode.findValue("steps").fields().forEachRemaining(field -> {
			DashboardStep step = new DashboardStep(field.getKey());
			step.datasets = new ArrayList<DashboardDataset>();
			ArrayNode datasetsNode = (ArrayNode)field.getValue().get("datasets");
			datasetsNode.forEach(datasetNode -> step.datasets.add(new DashboardDataset(datasetNode.get("id").textValue(), datasetNode.get("name").textValue())));
			steps.add(step);
		});
		
		return steps;
	}
}
