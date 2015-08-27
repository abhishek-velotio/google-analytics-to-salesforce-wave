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

import models.GoogleAnalyticsProfile;
import play.db.jpa.JPA;
/**
 * 
 * DAO class for work with Google Analytics Profile entity
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class GoogleAnalyticsProfileDAO extends BaseDAO<GoogleAnalyticsProfile> {
	
	public static List<String> privateFields = Arrays.asList(
		"authProviderX509CertUrl", 
		"authUri", 
		"clientEmail", 
		"clientId",
		"clientSecret",
		"clientX509CertUrl",
		"redirectUris",
		"tokenUri",
		"connected",
		"googleUserId",
		"accessToken",
		"refreshToken"
	);
	
	public static final String CACHE_PROFILE_PREFIX = "google_profile_";
	public static final String FLASH_PROFILE_ID 	= "profile_id";
	
	public static List<GoogleAnalyticsProfile> getProfiles() {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<GoogleAnalyticsProfile>>() {
				@SuppressWarnings("unchecked")
				public List<GoogleAnalyticsProfile> apply () {
					return (List<GoogleAnalyticsProfile>) JPA.em().createNamedQuery("GoogleAnalyticsProfile.findAll").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<GoogleAnalyticsProfile> getConnectedProfiles() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<GoogleAnalyticsProfile>>() {
				@SuppressWarnings("unchecked")
				public List<GoogleAnalyticsProfile> apply () {
					return (List<GoogleAnalyticsProfile>) JPA.em().createQuery("select gap from GoogleAnalyticsProfile gap where gap.connected = true").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static GoogleAnalyticsProfile getProfileByName(String name) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<GoogleAnalyticsProfile>() {
				public GoogleAnalyticsProfile apply () {
					return (GoogleAnalyticsProfile) JPA.em().createQuery("select gap from GoogleAnalyticsProfile gap where gap.name = :name", GoogleAnalyticsProfile.class).setParameter("name", name).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static GoogleAnalyticsProfile getProfileById(Long profileId) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<GoogleAnalyticsProfile>() {
				public GoogleAnalyticsProfile apply () {
					return (GoogleAnalyticsProfile) JPA.em().createQuery("select gap from GoogleAnalyticsProfile gap where gap.id = :id", GoogleAnalyticsProfile.class).setParameter("id", profileId).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
