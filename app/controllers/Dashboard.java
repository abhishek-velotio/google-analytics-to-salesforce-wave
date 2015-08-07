package controllers;

import java.util.HashMap;
import java.util.Map;

import models.dao.GoogleAnalyticsProfileDAO;
import models.dao.JobDAO;
import models.dao.SalesforceAnalyticsProfileDAO;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.security.Access;
import com.ga2sa.utils.JsonUtil;
/**
 * 
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access
public class Dashboard extends Controller {
	
	private static Map<String, JsonNode> params = new HashMap<String, JsonNode>();
	
	public static Result index() {
		params.clear();
		
		
		JsonNode googleProfiles = Json.toJson(GoogleAnalyticsProfileDAO.getConnectedProfiles());
		JsonNode salesforceProfiles = Json.toJson(SalesforceAnalyticsProfileDAO.getProfiles());
		JsonNode jobs = Json.toJson(JobDAO.getJobs());

		params.put("googleProfiles", JsonUtil.excludeFields(googleProfiles, GoogleAnalyticsProfileDAO.privateFields));
		params.put("salesforceProfiles", JsonUtil.excludeFields(salesforceProfiles, SalesforceAnalyticsProfileDAO.privateFields));
		params.put("jobs", jobs);

		return ok(views.html.pages.dashboard.index.render(params));
	}

}
