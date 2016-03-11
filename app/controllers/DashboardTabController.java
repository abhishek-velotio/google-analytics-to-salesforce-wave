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

import models.dao.SalesforceAnalyticsProfileDAO;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.salesforce.DashboardTemplatesManager;
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
public class DashboardTabController extends Controller {
	
	
	/**
	 * get all data for creation jobs, insert these data to json and return into page
	 * 
	 * @return page
	 */
	
	public static Result index() {
		return ok(views.html.pages.dashboard.index.render());
	}
	
	public static Result getSalesforceProfiles() {
		JsonNode salesforceProfiles = Json.toJson(SalesforceAnalyticsProfileDAO.getProfiles());
		return ok(JsonUtil.excludeFields(salesforceProfiles, SalesforceAnalyticsProfileDAO.privateFields));
	}
	
	public static Result getTemplates() {
		return ok(Json.toJson(DashboardTemplatesManager.getAllTemplates()));
	}

}
