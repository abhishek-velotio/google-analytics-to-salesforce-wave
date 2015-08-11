/**
 * 
 */
package com.ga2sa.security;

import java.util.UUID;

import models.Session;
import models.User;
import models.dao.SessionDAO;
import models.dao.UserDAO;
import play.Logger;
import play.mvc.Http.Cookie;

import com.ga2sa.helpers.forms.LoginForm;

import controllers.CookieManager;
import controllers.SessionManager;
/**
 * 
 * Class for manage application security. Responsible for authentication and authorization users. 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 *
 */
public class ApplicationSecurity {
	
	public static final String SESSION_ID_KEY = "session_id";
	
	public static Boolean authenticate(LoginForm loginForm) {
		User user = UserDAO.getUserByUsername(loginForm.getUsername());
		if (user == null) {
			Logger.error("User not found ");
		} else {
			if (user.getIsActive()) {
				if (PasswordManager.checkPassword(loginForm.getPassword(), user.getPassword())) {				
					final String sessionId = UUID.randomUUID().toString();
					SessionManager.set(SESSION_ID_KEY, sessionId);
//					CookieManager.set(SESSION_ID_KEY, sessionId, true);
					try {
						SessionDAO.save(new Session(sessionId, user.getId()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				} else {
					Logger.debug("Password is not correct.");
				}
			}
		}
		return false;
	}
	
	public static String getSessionId() {
//		Cookie cookie = CookieManager.get(SESSION_ID_KEY);
//		return cookie == null ? null : cookie.value();
		return SessionManager.get(SESSION_ID_KEY);
	}
	
	public static Session getCurrentSession() {
		return SessionDAO.getSession(getSessionId());
	}
	
	public static User getCurrentUser() {
	
		final String sessionId = getSessionId();
		Session session = getCurrentSession();
		
		if (session == null && sessionId == null) {
			Logger.debug("User is not loggined.");
			return null;
		}
		
		return session == null ? null : UserDAO.getUserById(session.getUserId());
	}
	
	public static boolean isAdmin() {
		User user = getCurrentUser();
		return user == null ? false : user.getRole().equals("ADMIN");
	}
	
	public static void logout() {
//		Cookie cookie = CookieManager.get(SESSION_ID_KEY);
//		if (cookie != null && cookie.value() != null) {
//			SessionDAO.deleteById(cookie.value());
//			CookieManager.remove(SESSION_ID_KEY, true);
//		}
		final String sessionId = getSessionId();
		if (sessionId != null) {
			SessionDAO.deleteById(sessionId);
			SessionManager.clear();
		}
		
	}
}
