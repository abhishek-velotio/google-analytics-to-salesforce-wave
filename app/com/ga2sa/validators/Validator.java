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

package com.ga2sa.validators;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import com.ga2sa.security.PasswordManager;

import models.Job;
import models.User;
import models.dao.BaseDAO;
import models.dao.JobDAO;

/**
 * 
 * Class for validate object.
 * 
 * @author Sergey Legostaev
 *
 */
public class Validator {
	
	private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*)";
	
	/**
	 * Common method for validate DB object
	 * 
	 * @param DB object
	 * @return map with fields and errors
	 */
	
	public static <T> Map<String,String> validate(T object) {
		Map<String,String> result = new HashMap<String,String>();
		if (object == null) {
			result.put("object", "Not Found");
		} else {
			Set<ConstraintViolation<T>> errors = Validation.buildDefaultValidatorFactory().getValidator().validate( object );
			errors.forEach(error -> result.put(error.getPropertyPath().toString(), error.getMessage())); 
			if (object instanceof User) {
				User u = (User) object;
				if (u.id == null || (u.id != null && u.password.equals(PasswordManager.PASSWORD_TMP) == false)) {
					if (u.password != null && u.password.length() < 6) {
						result.put("password", "Password must contain at least 6 characters");
					} else if (u.password != null && u.password.matches(PASSWORD_PATTERN) == false) {
						 result.put("password", "Password must include characters in UPPER/lowercase and numbers");
					}
				}
			} else if (object instanceof Job) {
				String name = ((Job) object).getName();
				Class<T> clazz = (Class<T>) ((T) object).getClass();
				if (BaseDAO.isExist(clazz, name)) result.put("name", "Job with same name already exists");
			}
		}
		return result;
	}
}
