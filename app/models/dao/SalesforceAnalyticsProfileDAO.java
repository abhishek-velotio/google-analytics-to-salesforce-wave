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

package models.dao;

import java.util.Arrays;
import java.util.List;

import models.SalesforceAnalyticsProfile;
/**
 * 
 * DAO class for work with Salesforce entity.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class SalesforceAnalyticsProfileDAO extends BaseDAO<SalesforceAnalyticsProfile> {
	
	public static List<String> privateFields = Arrays.asList(
			"password", 
			"username"		
	);
	
	public static List<SalesforceAnalyticsProfile> getProfiles() {
		return findAll(SalesforceAnalyticsProfile.class);
	}
	
	public static SalesforceAnalyticsProfile getProfileById(Long profileId) {
		return findById(SalesforceAnalyticsProfile.class, profileId);
	}
	
}
