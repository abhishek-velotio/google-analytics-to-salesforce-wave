package com.ga2sa.security;

import models.User;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ObjectNode;
/**
 * 
 * Action class for handle requests to page. If user has access to page then request will be allowed, if user has not access then will be returned error code.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 *
 */

public class AccessAction extends Action<Access> {

	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		User user = ApplicationSecurity.getCurrentUser();
		if (user == null) return F.Promise.pure((Result) redirect(controllers.routes.Authorization.ga2saSignIn()));
		if (user.getRole().equalsIgnoreCase(UserGroup.ADMIN.name())) return delegate.call(ctx);
		
		UserGroup userGroup = configuration.allowFor();
		
		if (userGroup.equals(UserGroup.ADMIN)) {
			ObjectNode result = Json.newObject();
			result.put("error", 401);
			result.put("message", "You don't have permissions");
			return F.Promise.pure((Result) unauthorized(result));
		}
		
		return delegate.call(ctx);
	}

}
