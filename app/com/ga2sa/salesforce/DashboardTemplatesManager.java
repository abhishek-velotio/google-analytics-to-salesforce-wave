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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import models.DashboardType;
import play.Logger;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author SLegostaev
 *
 */
public class DashboardTemplatesManager {
	
	private static ObjectMapper mapper = new ObjectMapper();
	private static Map<DashboardType, ObjectNode> templates;
	
	public static ObjectNode getTemplate(DashboardType dashboardType) {
		if (templates == null) loadTemplates();
		return templates.get(dashboardType);
	}
	
	public static void loadTemplates() {
		templates = new HashMap<DashboardType, ObjectNode>();
		Arrays.stream(DashboardType.values()).forEach(dashboartType -> templates.put(dashboartType, loadJson(dashboartType)));

	}
	
	private static ObjectNode loadJson(DashboardType type) {
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
			
			
			return objNode;
		} catch (IOException e) {
			Logger.debug(e.getMessage(), e);
		}
		return null;
	}
}
