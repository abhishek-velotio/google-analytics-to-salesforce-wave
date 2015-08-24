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

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.MimeTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.google.GoogleAnalyticsDataManager;
import com.ga2sa.security.Access;
import com.ga2sa.security.ApplicationSecurity;
/**
 * Controller class for work with Google Analytics Profile, this class uses in Job creation page. 
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access
public class GoogleAnalyticsAPI extends Controller {
		
	
	
	/**
	 * Get all accounts for selected Google Analytics Profile.
	 * @param Google Analytics Profile Id from application database
	 * @return json with result
	 */
	public static Result getAccounts(String profileId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getAccounts(ApplicationSecurity.getGoogleCredential(profileId))).get("items");
		if (result == null) result = Json.newObject();
		return ok(result).as(MimeTypes.JAVASCRIPT());
	}
	
	/**
	 * Get all properties from Google Analytics Profile
	 * 
	 * @param Google Analytics Profile Id from application database
	 * @param  Google Analytics Account Id from Google Analytics Profile
	 * @return json with result
	 */
	public static Result getProperties(String profileId, String accountId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getProperties(ApplicationSecurity.getGoogleCredential(profileId), accountId)).get("items");
		if (result == null) result = Json.newObject();
		return ok(result).as(MimeTypes.JAVASCRIPT());
	}
	
	/**
	 * Get all profiles for selected account and property from Google Analytics Profile
	 * 
	 * @param Google Analytics Profile Id from application database
	 * @param Google Analytics Account Id from Google Analytics Profile
	 * @param Google Analytics Property Id from Google Analytics Profile
	 * @return json with result
	 */
	public static Result getProfiles(String profileId, String accountId, String propertyId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getProfiles(ApplicationSecurity.getGoogleCredential(profileId), accountId, propertyId)).get("items");
		if (result == null) result = Json.newObject();
		return ok(result).as(MimeTypes.JAVASCRIPT());
	}
	
	/**
	 * Get all dimensions for selected Google Analytics profile.
	 * @param Google Analytics Profile Id from Google Analytics Profile
	 * @return json with result
	 */
	public static Result getDimensions(String profileId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getDimensions(ApplicationSecurity.getGoogleCredential(profileId)));
		if (result == null) result = Json.newObject();
		return ok(result).as(MimeTypes.JAVASCRIPT());
	}
	/**
	 * Get all metrics for selected Google Analytics profile.
	 * @param Google Analytics Profile Id from Google Analytics Profile
	 * @return json with result
	 */
	public static Result getMetrics(String profileId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getMetrics(ApplicationSecurity.getGoogleCredential(profileId)));
		if (result == null) result = Json.newObject();
		return ok(result).as(MimeTypes.JAVASCRIPT());
	}
	
}
