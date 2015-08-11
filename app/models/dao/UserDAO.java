package models.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import models.User;
import play.Logger;
import play.db.jpa.JPA;

import com.ga2sa.security.ApplicationSecurity;
import com.ga2sa.security.PasswordManager;
/**
 * 
 * DAO class for work with user entity.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class UserDAO extends BaseDAO<User> {
	
	@SuppressWarnings("unchecked")
	public static List<User> getUsers() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<User>>() {
				public List<User> apply () {
					return (List<User>) JPA.em().createNamedQuery("User.findAll").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	public static List<User> getUserWithoutCurrent() {
		User currentUser = ApplicationSecurity.getCurrentUser();
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<User>>() {
				public List<User> apply () {
					return JPA.em().createQuery("select u from User u where u.id <> :id ORDER BY u.id ASC", User.class)
							.setParameter("id", currentUser.getId()).getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static User getUserByUsername(String username) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<User>() {
				public User apply () {
					try {
						return JPA.em().createQuery("select u from User u where u.username = :username", User.class)
								.setParameter("username", username).getSingleResult();
					} catch (NoResultException e) {
						Logger.info(String.format("User not found by username %s", username));
					}
					return null;
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static User getUserById(Long userId) {
				
		try {
			return JPA.withTransaction(new play.libs.F.Function0<User>() {
				public User apply () {
					try {
						return JPA.em().createQuery("select u from User u where u.id = :user_id", User.class)
								.setParameter("user_id", userId).getSingleResult();
					} catch (NoResultException e) {
						Logger.info(String.format("User not found by id %s", userId));
					}
					return null;
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void update(User changedUser) throws Exception {
		
		User user = getUserById(changedUser.getId());
		
		user.setUsername(changedUser.getUsername());
		user.setEmailAddress(changedUser.getEmailAddress());
		user.setFirstName(changedUser.getFirstName());
		user.setLastName(changedUser.getLastName());
		user.setRole(changedUser.getRole());
		user.setRecordModifiedBy(ApplicationSecurity.getCurrentUser().getId());
		user.setRecordModifiedDateTime(new Timestamp(new Date().getTime()));
		
		if (changedUser.getIsActive() != null) user.setIsActive(changedUser.getIsActive());
		if (!changedUser.getPassword().equals(PasswordManager.PASSWORD_TMP)) user.setPassword(PasswordManager.encryptPassword(changedUser.getPassword()));
		
		BaseDAO.update(user);
	}

}
