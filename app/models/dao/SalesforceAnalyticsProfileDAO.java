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
			"username", 
			"applicationName"
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
					return (SalesforceAnalyticsProfile) JPA.em().createQuery("select gap from SalesforceAnalyticsProfile gap where gap.name = :name", SalesforceAnalyticsProfile.class).setParameter("name", name).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static SalesforceAnalyticsProfile getProfileById(Integer profileId) {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<SalesforceAnalyticsProfile>() {
				public SalesforceAnalyticsProfile apply () {
					return (SalesforceAnalyticsProfile) JPA.em().createQuery("select gap from SalesforceAnalyticsProfile gap where gap.id = :id", SalesforceAnalyticsProfile.class).setParameter("id", profileId).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
