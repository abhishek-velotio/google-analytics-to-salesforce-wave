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

import javax.persistence.NoResultException;

import models.GoogleAnalyticsReport;
import play.Logger;
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
			if (e instanceof NoResultException) {
				Logger.debug("Report not found for job id : " + id);
			} else {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
