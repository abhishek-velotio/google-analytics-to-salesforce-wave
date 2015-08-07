package controllers;

import java.util.HashMap;
import java.util.Map;

import models.User;
import models.dao.GoogleAnalyticsProfileDAO;
import models.dao.SalesforceAnalyticsProfileDAO;
import models.dao.UserDAO;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ga2sa.security.Access;
import com.ga2sa.security.ApplicationSecurity;
import com.ga2sa.security.UserGroup;
/**
 * 
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class Settings extends Controller {
	
	private static Map<String, JsonNode> params = new HashMap<String, JsonNode>();
	
	@Access
	public static Result index() {
		return redirect(routes.Settings.profile());
	}
	
	@Access
	public static Result profile() {
		params.clear();
		
		User currentUser = ApplicationSecurity.getCurrentUser();
		JsonNode user = Json.toJson(currentUser);
		
		((ObjectNode)user).remove("isActive");
		
		params.put("user", user);
		
		return ok(views.html.pages.settings.index.render("profile", params));
	}
	
	@Access(allowFor = UserGroup.ADMIN)
	public static Result users() {
		params.clear();
		
		JsonNode users = Json.toJson(UserDAO.getUserWithoutCurrent());
		
		params.put("users", users);
		
		return ok(views.html.pages.settings.index.render("users", params));
	}
	
	@Access(allowFor = UserGroup.ADMIN)
	public static Result ga() {
		params.clear();
		
		JsonNode profiles = Json.toJson(GoogleAnalyticsProfileDAO.getProfiles());
		
		params.put("profiles", profiles);
		
		return ok(views.html.pages.settings.index.render("ga", params));
	}
	
	@Access(allowFor = UserGroup.ADMIN)
	public static Result sa() {
		params.clear();
		
		JsonNode profiles = Json.toJson(SalesforceAnalyticsProfileDAO.getProfiles());
		
		params.put("profiles", profiles);
		return ok(views.html.pages.settings.index.render("sa", params));
	}

}