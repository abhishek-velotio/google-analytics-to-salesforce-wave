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
	
	
//	public static List<DatasetJob> getJobs() {
//		try {
//			return JPA.withTransaction(new play.libs.F.Function0<List<DatasetJob>>() {
//				@SuppressWarnings({ "unchecked" })
//				public List<DatasetJob> apply () {
//					return JPA.em().createNamedQuery("Job.findAll").getResultList();
//				}
//			});
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return Collections.emptyList();
//	}
	
//	public static List<DatasetJob> getJobs(final BaseFilter<DatasetJob> filter) {
//		try {
//			return JPA.withTransaction(new play.libs.F.Function0<List<Job>>() {
//				@SuppressWarnings({ "unchecked" })
//				public List<Job> apply () {
//					if (filter == null) {
//						return JPA.em().createNamedQuery("Job.findAll").getResultList();
//					} else {
//						CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
//						CriteriaQuery<Job> cq = cb.createQuery(Job.class);
//						Root<Job> c = cq.from(Job.class);
//						cq.select(c);
//						Path<Job> orderBy = c.get(filter.orderBy.orElse(BaseFilter.DEFAULT_ORDER_BY));
//						OrderType orderType = filter.orderType.orElse(OrderType.desc);
//						cq.orderBy(orderType.equals(OrderType.asc) ? cb.asc(orderBy) : cb.desc(orderBy));
//						return JPA.em().createQuery(cq)
//								.setFirstResult(filter.offset.orElse(BaseFilter.DEFAULT_OFFSET))
//								.setMaxResults(filter.count.orElse(BaseFilter.DEFAULT_COUNT)).getResultList();
//					}
//				}
//			});
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return Collections.emptyList();
//		return getAllByFilter(filter);
//	}
//	
//	public static boolean isExist(Job job) {
//		try {
//			return JPA.withTransaction(new play.libs.F.Function0<Boolean>() {
//				public Boolean apply () {
//					return JPA.em().createQuery("select j from Job j where j.name = :name", Job.class)
//							.setParameter("name", job.getName()).getResultList().size() != 0;
//				}
//			});
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
	
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
