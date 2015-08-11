package controllers.settings;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import models.User;
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
import com.ga2sa.security.UserGroup;
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
	
	private static final String USER_EXISTS = "User already exists";
	
	@Transactional
	public static Result add() {
		User user = Json.fromJson(request().body().asJson(), User.class);
		user.setPassword(PasswordManager.encryptPassword(user.getPassword()));
		user.setRecordCreatedBy(ApplicationSecurity.getCurrentUser().getId());
		user.setRecordCreatedDateTime(new Timestamp(new Date().getTime()));
		return commonAction(user, new Callback0() {
			@Override
			public void invoke() throws Throwable {
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
		return commonAction(user, new Callback0() {
			@Override
			public void invoke() throws Throwable {
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
				Logger.debug(USER_EXISTS);
				validateResult.put("username", USER_EXISTS);
			}
		}
		return badRequest(Json.toJson(validateResult)).as(MimeTypes.JAVASCRIPT());
	}
	
}
