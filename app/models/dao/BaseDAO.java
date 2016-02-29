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

import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import models.Job;
import models.dao.filters.BaseFilter;
import models.dao.filters.BaseFilter.OrderType;

import org.postgresql.util.PSQLException;

import play.Logger;
import play.db.jpa.JPA;

/**
 * Common DAO class for manage entity object.
 * 
 * @author Sergey Legostaev
 *
 * @param <T> Type of object
 */

public class BaseDAO<T> {
	
	public static <T> List<T> findAll(Class<T> clazz) {
		return getAllByFilter(new BaseFilter<T>(clazz));
	}
	
	public static <T> List<T> getAllByFilter(final BaseFilter<T> filter) { 
		if (filter != null && !filter.objClass.isPresent()) throw new IllegalArgumentException("Object is null");
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<T>>() {
				@SuppressWarnings({ "unchecked" })
				public List<T> apply () {

						CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
						CriteriaQuery<T> cq = cb.createQuery(filter.objClass.get());
						Root<T> c = cq.from(filter.objClass.get());
						cq.select(c);
						Path<Job> orderBy = c.get(filter.orderBy.orElse(BaseFilter.DEFAULT_ORDER_BY));
						OrderType orderType = filter.orderType.orElse(OrderType.desc);
						cq.orderBy(orderType.equals(OrderType.asc) ? cb.asc(orderBy) : cb.desc(orderBy));
						return JPA.em().createQuery(cq)
								.setFirstResult(filter.offset.orElse(BaseFilter.DEFAULT_OFFSET))
								.setMaxResults(filter.count.orElse(BaseFilter.DEFAULT_COUNT)).getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	public static <T> Long getCount(Class<T> clazz) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<Long>() {
			    public Long apply() {
			    	CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
			    	CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			    	cq.select(cb.count(cq.from(clazz)));
			    	return JPA.em().createQuery(cq).getSingleResult();
			    }
			});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0l;
	}
	
	public static <T> Boolean isExist(Class<T> object, String name) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<Boolean>() {
				public Boolean apply () {
					CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
			    	CriteriaQuery<T> cq = cb.createQuery(object);
			    	Root<T> c = cq.from(object);
			    	ParameterExpression<T> p = cb.parameter(object);
			    	cq.select(c).where(cb.equal(c.get("name"), p));
			    	Logger.debug(name);
					return JPA.em().createQuery(cq).setParameter("name", name).getResultList().size() > 0;
				}
			});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static <T> void save(T object) throws Exception {
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().persist(object);
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
			
			Throwable t = e.getCause();
			
			while ((t != null) && !(t instanceof PSQLException)) t = t.getCause();
				    
			if (t instanceof PSQLException) throw new Exception((PSQLException) t);
		}
	}
	
	public static <T> void delete(T object) {
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().remove(JPA.em().merge(object));
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static <T> void update(T object) throws Exception {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().merge(object);
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
