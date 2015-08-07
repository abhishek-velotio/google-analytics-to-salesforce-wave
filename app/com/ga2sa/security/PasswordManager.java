package com.ga2sa.security;

import org.mindrot.jbcrypt.BCrypt;

import play.Logger;
/**
 * 
 * Util class for work with password, the one uses for encrypt and check user password 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 * 
 */
public class PasswordManager {
	
	public static final String PASSWORD_TMP = "password";

	public static boolean checkPassword(String plainPassword, String encryptedPassword) {
		try {
			return BCrypt.checkpw(plainPassword, encryptedPassword);
		} catch (IllegalArgumentException e) {
			Logger.error("Check password error: ", e) ;
		}
		return false;
	}
	
	public static String encryptPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

}
