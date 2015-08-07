package controllers.settings;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import com.ga2sa.security.Access;
/**
 * Controller class for manage profile for current user.
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access
public class ProfileSettings extends Controller {
		
	@Transactional
	public static Result update(String userId) {
		return UsersSettings.update(userId);
	}
}
