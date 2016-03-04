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

import java.util.List;

import models.DatasetJob;
import models.Job;
import models.JobStatus;
import play.db.jpa.JPA;
/**
 * 
 * DAO class for manage of Background Jobs.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 *
 */

public class JobDAO extends BaseDAO<DatasetJob> {
	
	public static DatasetJob findById(Long id) {
		return findById(DatasetJob.class, id);
	}
	
	public static DatasetJob getLastJob() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<DatasetJob>() {
				public DatasetJob apply () {
					return (DatasetJob) JPA.em().createQuery("select j from DatasetJob j ORDER BY j.created DESC", Job.class).setMaxResults(1).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<DatasetJob> getJobsForScheduler() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<DatasetJob>>() {
				@SuppressWarnings("unchecked")
				public List<DatasetJob> apply () {
					return (List<DatasetJob>) JPA.em().createQuery("select j from DatasetJob j where j.status = :status or j.repeatPeriod <> null")
							.setParameter("status", JobStatus.PENDING).getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
