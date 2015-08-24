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

import models.GoogleAnalyticsProfile;
import models.dao.GoogleAnalyticsProfileDAO;
import play.cache.Cache;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import com.ga2sa.google.GoogleConnector;
import com.ga2sa.helpers.forms.LoginForm;
import com.ga2sa.security.Access;
import com.ga2sa.security.ApplicationSecurity;
/**
 * Class for user authorization to the application and Google.
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class Authorization extends Controller {
	
	private static Form<LoginForm> loginForm = Form.form(LoginForm.class);
	
	/**
	 * method for user authentication
	 * 
	 * @return if user exists redirect to dashboard page else update login page
	 */
	public static Result ga2saSignIn() {
		if(request().method().equals("POST")) {
			Form<LoginForm> bindedForm = loginForm.bindFromRequest();
			if (ApplicationSecurity.authenticate(bindedForm.get())) return redirect(routes.Application.index());
			else return redirect(routes.Authorization.ga2saSignIn());
		}
		return ok(views.html.pages.auth.signin.render());
	}
	
	/**
	 * user logout
	 * 
	 * @return redirected to login page
	 */
	
	@Access
	public static Result ga2saSignOut() {
		ApplicationSecurity.logout();
		return redirect(routes.Authorization.ga2saSignIn());
	}
	
	/**
	 * method for login to Google Analytics
	 * 
	 * @return login result
	 */
	
	@Access
	public static Result googleSignIn() {
		
		final String code = request().getQueryString("code");
		final String profileId = flash(GoogleAnalyticsProfileDAO.FLASH_PROFILE_ID);

		GoogleAnalyticsProfile profile = (GoogleAnalyticsProfile) Cache.get(GoogleAnalyticsProfileDAO.CACHE_PROFILE_PREFIX + profileId);

		GoogleConnector.exchangeCode(profile, code);
		return ok(views.html.pages.auth.googleAuthComplete.render());
		
	}

}
