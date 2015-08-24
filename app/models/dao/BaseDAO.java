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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.postgresql.util.PSQLException;

import play.db.jpa.JPA;

/**
 * Common DAO class for manage entity object.
 * 
 * @author Sergey Legostaev
 *
 * @param <T> Type of object
 */

public class BaseDAO<T> {
	
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
