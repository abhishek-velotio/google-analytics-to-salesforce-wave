package com.ga2sa.security;

/**
 * 
 * Check user access for tabs and pages
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 *
 */
public class CheckAccesTemplates {
	
	/**
	 * Method uses for check access to element on UI for current user.
	 * 
	 * @return true or false
	 */
	public static boolean hasAccess() {
		return ApplicationSecurity.isAdmin();
	}
	
}
