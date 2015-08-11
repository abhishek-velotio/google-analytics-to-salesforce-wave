package models.dao;

import models.GoogleAnalyticsReport;
import play.db.jpa.JPA;
/**
 * DAO class for work with Google Analytics Report entity.
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class GoogleAnalyticsReportDAO extends BaseDAO<GoogleAnalyticsReport> {
	
	public static GoogleAnalyticsReport getReportByJobId(Long id) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<GoogleAnalyticsReport>() {
				public GoogleAnalyticsReport apply () {
					return (GoogleAnalyticsReport) JPA.em().createQuery("select gar from GoogleAnalyticsReport gar where gar.jobId = :id", GoogleAnalyticsReport.class).setParameter("id", id).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
