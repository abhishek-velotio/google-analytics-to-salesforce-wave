package com.ga2sa.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import models.UserGroup;
import play.mvc.With;

/**
 * Annotation interface for manage access to controllers
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 *
 */

@With(AccessAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Access {
	
	/**
	 * Parameter for set which user role has access to method
	 * 
	 * @return
	 */
	UserGroup allowFor() default UserGroup.USER;
}
