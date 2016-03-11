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
package com.ga2sa.actors;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import models.DashboardJob;
import models.Job;
import models.JobStatus;
import models.SalesforceAnalyticsProfile;
import models.dao.DashboardJobDAO;

import org.apache.commons.lang3.StringEscapeUtils;

import play.Logger;
import play.libs.Json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ga2sa.salesforce.DashboardDataset;
import com.ga2sa.salesforce.DashboardTemplatesManager;
import com.ga2sa.salesforce.SalesforceDataManager;

/**
 * @author SLegostaev
 *
 */
public class DashboardBGJob implements BackgroundJobInterface {
	

	
	private DashboardJob job;
	
	public DashboardBGJob(DashboardJob job) {
		this.job = job;
	}
	
	/* (non-Javadoc)
	 * @see com.ga2sa.actors.JobActorInterface#start()
	 */
	@Override
	public void start() throws Exception {
		try {
			SalesforceAnalyticsProfile profile = job.getSalesforceAnalyticsProfile();
			SalesforceDataManager.createDashboard(profile, getDashboardJson());
			job.setStatus(JobStatus.OK);
			job.setMessages("Dashbord was created");
			Logger.debug("Backgroud job has been completed, job name is " + job.getName());
		} catch (Exception e) {
			job.setStatus(JobStatus.FAIL);
			job.setMessages(StringEscapeUtils.escapeHtml4(e.getMessage()));
			Logger.debug(e.getMessage(), e);
		} finally {
			DashboardJobDAO.update(job);
		}
	}
	
	
	private JsonNode getDashboardJson() throws JsonProcessingException, IOException {
		ObjectNode dashboardJson = DashboardTemplatesManager.getTemplate(job.dashboardType);
		dashboardJson.put("label", job.getName());
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> dashboardDatasets = mapper.readValue(job.datasets, Map.class);
		dashboardDatasets.forEach((key, value) -> {
			ObjectNode step = (ObjectNode) dashboardJson.findPath(key);
			step.put("datasets", Json.toJson(value).get("datasets"));
		});
		return dashboardJson;
	}
	
	/* (non-Javadoc)
	 * @see com.ga2sa.actors.BackgroundJobInterface#getJob()
	 */
	@Override
	public Job getJob() {
		return job;
	}
}
