package models.dao;

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
