package controllers.settings;

import java.util.Map;

import models.SalesforceAnalyticsProfile;
import models.dao.SalesforceAnalyticsProfileDAO;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.MimeTypes;

import com.ga2sa.helpers.Callback;
import com.ga2sa.security.Access;
import com.ga2sa.security.UserGroup;
import com.ga2sa.validators.Validator;
/**
 * 
 * Controller class for manage Salesforce profiles.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access(allowFor = UserGroup.ADMIN)
public class SalesforceAnalyticsSettings extends Controller {
	
	private static final String PROFILE_EXISTS = "Profile already exists";
	@Transactional
	public static Result add() {
		SalesforceAnalyticsProfile object = new SalesforceAnalyticsProfile();
		return commonAction(object, new Callback<SalesforceAnalyticsProfile>() {
			@Override
			public void action()
					throws Exception {
				SalesforceAnalyticsProfileDAO.save(object);
			}
		});
	}
	
	@Transactional
	public static Result delete(String profileId) {
		Integer id = Integer.parseInt(profileId);
		SalesforceAnalyticsProfileDAO.delete(SalesforceAnalyticsProfileDAO.getProfileById(id));
		return ok();
	}
	
	@Transactional
	public static Result update(String profileId) {
		SalesforceAnalyticsProfile object = SalesforceAnalyticsProfileDAO.getProfileById(Integer.parseInt(profileId));
		return commonAction(object, new Callback<SalesforceAnalyticsProfile>() {
			@Override
			public void action()
					throws Exception {
				SalesforceAnalyticsProfileDAO.update(object);
			}
		});
	}
	
	private static Result commonAction(SalesforceAnalyticsProfile object, Callback<SalesforceAnalyticsProfile> callback) {
		object.setName(request().body().asJson().get("name").textValue());
		object.setUsername(request().body().asJson().get("username").textValue());
		object.setPassword(request().body().asJson().get("password").textValue());
		object.setApplicationName(request().body().asJson().get("applicationName").textValue());
		Map<String, String> validateResult = Validator.validate2(object);
		if (validateResult.isEmpty()) {
			try {
				callback.action();
				return ok(Json.toJson(object)).as(MimeTypes.JAVASCRIPT());
			} catch (Exception e) {
				Logger.debug(PROFILE_EXISTS);
				validateResult.put("name", PROFILE_EXISTS);
			}
		}
		return badRequest(Json.toJson(validateResult)).as(MimeTypes.JAVASCRIPT());
	}
}
