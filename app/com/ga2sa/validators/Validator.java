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
	
	public static <T> Map<String,String> validate2(T object) {
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
