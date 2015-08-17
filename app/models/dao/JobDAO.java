package models.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import models.Job;
import models.JobStatus;
import models.dao.filters.BaseFilter;
import models.dao.filters.BaseFilter.OrderType;
import play.db.jpa.JPA;
/**
 * 
 * DAO class for manage of Background Jobs.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 *
 */

public class JobDAO extends BaseDAO<Job> {
	
	
	public static List<Job> getJobs() {
		return getJobs(null);
	}
	
	public static List<Job> getJobs(final BaseFilter filter) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<Job>>() {
				@SuppressWarnings({ "unchecked" })
				public List<Job> apply () {
					if (filter == null) {
						return JPA.em().createNamedQuery("Job.findAll").getResultList();
					} else {
						CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
						CriteriaQuery<Job> cq = cb.createQuery(Job.class);
						Root<Job> c = cq.from(Job.class);
						cq.select(c);
						Path<Job> orderBy = c.get(filter.orderBy.orElse(BaseFilter.DEFAULT_ORDER_BY));
						OrderType orderType = filter.orderType.orElse(OrderType.desc);
						cq.orderBy(orderType.equals(OrderType.asc) ? cb.asc(orderBy) : cb.desc(orderBy));
						return JPA.em().createQuery(cq)
								.setFirstResult(filter.offset.orElse(BaseFilter.DEFAULT_OFFSET))
								.setMaxResults(filter.count.orElse(BaseFilter.DEFAULT_COUNT)).getResultList();
					}
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isExist(Job job) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<Boolean>() {
				public Boolean apply () {
					return JPA.em().createQuery("select j from Job j where j.name = :name", Job.class)
							.setParameter("name", job.getName()).getResultList().size() != 0;
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Job getLastJob() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<Job>() {
				public Job apply () {
					return (Job) JPA.em().createQuery("select j from Job j ORDER BY j.created DESC", Job.class).setMaxResults(1).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Job> getJobsForScheduler() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<Job>>() {
				@SuppressWarnings("unchecked")
				public List<Job> apply () {
					return (List<Job>) JPA.em().createQuery("select j from Job j where j.status = :status or j.repeatPeriod <> null")
							.setParameter("status", JobStatus.PENDING).getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
