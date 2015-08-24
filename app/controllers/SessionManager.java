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

package controllers;

import play.Logger;
import play.mvc.Controller;

/**
 * Class for session manage
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class SessionManager extends Controller {
	
	public static String get(String id) {
		try {
			return session(id);
		} catch (RuntimeException e) {
			Logger.debug("There is no HTTP Context available from here.");
		}
		return null;
	}
	
	public static void set(String id, String value) {
		session(id, value);
	}
	
	public static void remove(String id) {
		session().remove(id);
	}
	
	public static void clear() {
		session().clear();
	}

}
