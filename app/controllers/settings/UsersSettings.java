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

import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import models.User;
import models.UserGroup;
import models.dao.UserDAO;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.MimeTypes;

import com.ga2sa.security.Access;
import com.ga2sa.security.ApplicationSecurity;
import com.ga2sa.security.PasswordManager;
import com.ga2sa.validators.Validator;
/**
 * 
 * Controller class for manage application users.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access(allowFor = UserGroup.ADMIN)
public class UsersSettings extends Controller {
	
	private static final String USER_EXISTS = "User with same username already exists";
	private static final String EMAIL_EXISTS = "User with same email address already exists";
	
	@Transactional
	public static Result add() {
		User user = Json.fromJson(request().body().asJson(), User.class);
		return commonAction(user, new Callback0() {
			@Override
			public void invoke() throws Throwable {
				user.password = PasswordManager.encryptPassword(user.password);
				UserDAO.save(user);
			}
		});
	}
	
	@Transactional
	public static Result delete(String profileId) {
		Long id = Long.parseLong(profileId);
		User user = (User) UserDAO.getUserById(id);
		UserDAO.delete(user);
		return ok();
	}
	
	@Transactional
	public static Result update(String profileId) {
		User user = Json.fromJson(request().body().asJson(), User.class);
		User currentUser  = ApplicationSecurity.getCurrentUser();
		if (user.id.equals(currentUser.id)) user.isActive = true;
		
		return commonAction(user, new Callback0() {
			@Override
			public void invoke() throws Throwable {
				if (user.id != null) {
					if (user.password.equals(PasswordManager.PASSWORD_TMP)) {
						User sourceUser = UserDAO.getUserById(user.id);
						user.password = sourceUser.password;
					}
				}
				UserDAO.update(user);
			}
		});
	}
	
	private static Result commonAction(User object, Callback0 callback) {
		Map<String, String> validateResult = Validator.validate(object);
		if (validateResult.isEmpty()) {
			try {
				callback.invoke();
				return ok(Json.toJson(object)).as(MimeTypes.JAVASCRIPT());
			} catch (Throwable e) { 
				
				if (e.getCause() instanceof PSQLException) {
					PSQLException e2 = (PSQLException)e.getCause(); 
					ServerErrorMessage errorMessage = e2.getServerErrorMessage();
					if (errorMessage.getConstraint().equals("users_email_address_key")) {
						validateResult.put("emailAddress", EMAIL_EXISTS);
					} else {
						validateResult.put("username", USER_EXISTS);
					}
					Logger.debug(errorMessage.getDetail());
					
				} else {
					e.printStackTrace();
					validateResult.put("username", "Application error, see to log file");
				}
			}
		}
		return badRequest(Json.toJson(validateResult)).as(MimeTypes.JAVASCRIPT());
	}
	
}
