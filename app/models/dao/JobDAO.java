package models.dao;

import java.util.List;

import models.Job;

import org.postgresql.util.PSQLException;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
/**
 * 
 * DAO class for manage of Background Jobs.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 *
 */

public class JobDAO {
	
	public static List<Job> getJobs() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<Job>>() {
				@SuppressWarnings("unchecked")
				public List<Job> apply () {
					return (List<Job>) JPA.em().createNamedQuery("Job.findAll").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Job getLastJob() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<Job>() {
				public Job apply () {
					return (Job) JPA.em().createQuery("select j from Job j ORDER BY j.id DESC", Job.class).setMaxResults(1).getSingleResult();
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
							.setParameter("status", "PENDING").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Transactional
	public static void save(Job job) throws Exception {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().persist(job);
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
			
			Throwable t = e.getCause();
			
			while ((t != null) && !(t instanceof PSQLException)) t = t.getCause();
				    
			if (t instanceof PSQLException) throw new Exception((PSQLException) t);
		}
		
	}
	
	@Transactional
	public static void delete(Job job) {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().remove(JPA.em().merge(job));
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	public static void update(Job job) throws Exception {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().merge(job);
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
			
			Throwable t = e.getCause();
			
			while ((t != null) && !(t instanceof PSQLException)) t = t.getCause();
				    
			if (t instanceof PSQLException) throw new Exception((PSQLException) t);
		}
		
	}
}
