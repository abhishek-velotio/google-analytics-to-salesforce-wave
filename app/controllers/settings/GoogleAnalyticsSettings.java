package controllers.settings;

import java.util.Map;

import models.GoogleAnalyticsProfile;
import models.UserGroup;
import models.dao.GoogleAnalyticsProfileDAO;
import play.Logger;
import play.cache.Cache;
import play.db.jpa.Transactional;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.MimeTypes;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ga2sa.google.GoogleConnector;
import com.ga2sa.security.Access;
import com.ga2sa.validators.Validator;
/**
 * 
 * Controller class for manage Google Analytics profile, also responsible for connection and disconnect to Google.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access(allowFor = UserGroup.ADMIN)
public class GoogleAnalyticsSettings extends Controller {
	
	private static final String PROFILE_EXISTS =  "Profile already exists";
	
	@Transactional
	public static Result get(String profileId) {
		GoogleAnalyticsProfile profile = GoogleAnalyticsProfileDAO.getProfileById(Integer.parseInt(profileId));
		return ok(Json.toJson(profile));
	}
	
	@Transactional
	public static Result add() {
		GoogleAnalyticsProfile object = Json.fromJson(request().body().asJson(), GoogleAnalyticsProfile.class);
		
		return commonAction(object, new Callback0() {
			@Override
			public void invoke() throws Throwable {
				GoogleAnalyticsProfileDAO.save(object);
			}
		});
	}
	
	@Transactional
	public static Result delete(String profileId) {
		GoogleAnalyticsProfileDAO.delete(GoogleAnalyticsProfileDAO.getProfileById(Integer.parseInt(profileId)));
		return ok();
	}
	
	@Transactional
	public static Result update(String profileId) {
		GoogleAnalyticsProfile object = Json.fromJson(request().body().asJson(), GoogleAnalyticsProfile.class);
		return commonAction(object, new Callback0() {
			@Override
			public void invoke() throws Throwable {
				GoogleAnalyticsProfileDAO.update(object);
			}
		});
	}
	
	public static Result connect(String profileId) {
		ObjectNode result = Json.newObject();
		GoogleAnalyticsProfile profile = (GoogleAnalyticsProfile) GoogleAnalyticsProfileDAO.getProfileById(Integer.parseInt(profileId));
		Cache.set(GoogleAnalyticsProfileDAO.CACHE_PROFILE_PREFIX + profileId, profile);
		result.put("authUrl", GoogleConnector.getAuthURL(profile));
		flash(GoogleAnalyticsProfileDAO.FLASH_PROFILE_ID, profileId);
		return ok(result);
	}
	
	public static Result disconnect(String profileId) {	
		
		GoogleAnalyticsProfile profile = (GoogleAnalyticsProfile) GoogleAnalyticsProfileDAO.getProfileById(Integer.parseInt(profileId));
		final String cacheId = GoogleConnector.CACHE_CREDENTIAL_PREFIX + profileId;
		
		profile.setAccessToken(null);
		profile.setRefreshToken(null);
		profile.setConnected(false);
		
		Cache.remove(cacheId);
		
		try {
			GoogleAnalyticsProfileDAO.update(profile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ok(Json.toJson(profile));
	}
	
	private static Result commonAction(GoogleAnalyticsProfile object, Callback0 callback) {
		Map<String, String> validateResult = Validator.validate(object);
		if (validateResult.isEmpty()) {
			try {
				callback.invoke();
				return ok(Json.toJson(object)).as(MimeTypes.JAVASCRIPT());
			} catch (Throwable e) {
				Logger.debug(PROFILE_EXISTS);
				validateResult.put("name", PROFILE_EXISTS);
			}
		}
		return badRequest(Json.toJson(validateResult)).as(MimeTypes.JAVASCRIPT());
	}

}
