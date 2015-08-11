package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import com.ga2sa.security.Access;
/**
 * Controller class for first page
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access
public class Application extends Controller {
	
	/**
	 * redirect to dashbord page
	 * 
	 * @return
	 */
	public static Result index() {
		return redirect(routes.Dashboard.index());
	}
}
