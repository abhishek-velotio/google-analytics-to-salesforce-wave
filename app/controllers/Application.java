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

import play.mvc.Controller;
import play.mvc.Result;

import com.ga2sa.security.Access;
/**
 * Controller class for first page
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access
public class Application extends Controller {
	
	/**
	 * redirect to dashbord page
	 * 
	 * @return
	 */
	public static Result index() {
		return redirect(routes.Dashboard.index());
	}
}
