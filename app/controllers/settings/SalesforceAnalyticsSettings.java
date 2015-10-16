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

package controllers.settings;

import java.util.Map;

import models.SFAccountType;
import models.SalesforceAnalyticsProfile;
import models.UserGroup;
import models.dao.SalesforceAnalyticsProfileDAO;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.MimeTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.security.Access;
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
		return commonAction(object, new Callback0() {
			@Override
			public void invoke() throws Throwable {
				SalesforceAnalyticsProfileDAO.save(object);
			}
		});
	}
	
	@Transactional
	public static Result delete(String profileId) {
		SalesforceAnalyticsProfileDAO.delete(SalesforceAnalyticsProfileDAO.getProfileById(Long.valueOf(profileId)));
		return ok();
	}
	
	@Transactional
	public static Result update(String profileId) {
		SalesforceAnalyticsProfile object = SalesforceAnalyticsProfileDAO.getProfileById(Long.valueOf(profileId));
		return commonAction(object, new Callback0() {
			@Override
			public void invoke() throws Throwable {
				SalesforceAnalyticsProfileDAO.update(object);
			}
		});
	}
	
	private static Result commonAction(SalesforceAnalyticsProfile object, Callback0 callback) {
		JsonNode body = request().body().asJson();
		object.setName(body.get("name").textValue());
		object.setUsername(body.get("username").textValue());
		object.setPassword(body.get("password").textValue());
		object.setApplicationName(body.get("applicationName").textValue());
		object.accountType = SFAccountType.valueOf(SFAccountType.class, body.get("accountType").textValue());
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
