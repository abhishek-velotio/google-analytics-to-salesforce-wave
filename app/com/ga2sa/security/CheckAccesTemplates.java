package com.ga2sa.security;

import models.User;
/**
 * 
 * Check user access for tabs and pages
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 *
 */
public class CheckAccesTemplates {
	
	public static boolean hasAccess() {
		User user = ApplicationSecurity.getCurrentUser();
		return user == null ? false : user.getRole().equalsIgnoreCase(UserGroup.ADMIN.name());
	}
	
}
