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
