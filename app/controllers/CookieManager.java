package controllers;

import play.Play;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Cookie;
/**
 * 
 * Class for manage cookies
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */

public class CookieManager extends Controller {
	
	/**
	 * get cookie from aplication context
	 * 
	 * @param cookie name
	 * @return cookie object
	 */
	public static Cookie get(String id) {
		return Http.Context.current().request().cookie(id);
	}
	
	/**
	 * set cookie for application, for production cookie is http only
	 * 
	 * @param cookie name
	 * @param value
	 * @param is Secure
	 */
	
	public static void set(String id, String value, boolean isSecure) {
		Http.Context.current().response().setCookie(id, value, null, null, null, Play.isProd(), isSecure);
	}
	
	/** 
	 * remove cookie from context
	 * 
	 * @param cookie name
	 * @param isSecure
	 */
	
	public static void remove(String id, boolean isSecure) {
		Http.Context.current().response().discardCookie(id, null, null, isSecure);
	}
}
