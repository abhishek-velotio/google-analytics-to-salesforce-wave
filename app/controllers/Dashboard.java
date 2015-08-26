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

package controllers;

import java.util.HashMap;
import java.util.Map;

import models.dao.GoogleAnalyticsProfileDAO;
import models.dao.SalesforceAnalyticsProfileDAO;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.security.Access;
import com.ga2sa.utils.JsonUtil;
/**
 * 
 * Controller class for work with dashboard page
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access
public class Dashboard extends Controller {
	
	private static Map<String, JsonNode> params = new HashMap<String, JsonNode>();
	
	/**
	 * get all data for creation jobs, insert these data to json and return into page
	 * 
	 * @return page
	 */
	
	public static Result index() {
		params.clear();
		
		JsonNode googleProfiles = Json.toJson(GoogleAnalyticsProfileDAO.getConnectedProfiles());
		JsonNode salesforceProfiles = Json.toJson(SalesforceAnalyticsProfileDAO.getProfiles());
		//JsonNode jobs = Json.toJson(JobDAO.getJobs());

		params.put("googleProfiles", JsonUtil.excludeFields(googleProfiles, GoogleAnalyticsProfileDAO.privateFields));
		params.put("salesforceProfiles", JsonUtil.excludeFields(salesforceProfiles, SalesforceAnalyticsProfileDAO.privateFields));
		//params.put("jobs", jobs);

		return ok(views.html.pages.dashboard.index.render(params));
	}

}
