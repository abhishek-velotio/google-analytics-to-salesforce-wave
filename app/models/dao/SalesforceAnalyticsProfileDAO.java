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
import play.db.jpa.JPA;
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
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<SalesforceAnalyticsProfile>>() {
				@SuppressWarnings("unchecked")
				public List<SalesforceAnalyticsProfile> apply () {
					return (List<SalesforceAnalyticsProfile>) JPA.em().createNamedQuery("SalesforceAnalyticsProfile.findAll").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static SalesforceAnalyticsProfile getProfileByName(String name) {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<SalesforceAnalyticsProfile>() {
				public SalesforceAnalyticsProfile apply () {
					return (SalesforceAnalyticsProfile) JPA.em().createQuery("select gap from SalesforceAnalyticsProfile gap where gap.name = :name", SalesforceAnalyticsProfile.class)
							.setParameter("name", name).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static SalesforceAnalyticsProfile getProfileById(Long profileId) {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<SalesforceAnalyticsProfile>() {
				public SalesforceAnalyticsProfile apply () {
					return (SalesforceAnalyticsProfile) JPA.em().createQuery("select gap from SalesforceAnalyticsProfile gap where gap.id = :id", SalesforceAnalyticsProfile.class)
							.setParameter("id", profileId).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
