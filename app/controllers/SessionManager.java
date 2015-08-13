package controllers;

import play.Logger;
import play.mvc.Controller;

/**
 * Class for session manage
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class SessionManager extends Controller {
	
	public static String get(String id) {
		try {
			return session(id);
		} catch (RuntimeException e) {
			Logger.debug("There is no HTTP Context available from here.");
		}
		return null;
	}
	
	public static void set(String id, String value) {
		session(id, value);
	}
	
	public static void remove(String id) {
		session().remove(id);
	}
	
	public static void clear() {
		session().clear();
	}

}
