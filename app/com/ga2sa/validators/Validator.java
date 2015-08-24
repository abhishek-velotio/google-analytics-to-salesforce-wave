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

/**
 * 
 * Class for validate object.
 * 
 * @author Sergey Legostaev
 *
 */
public class Validator {
	
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
		}
		return result;
	}
}
