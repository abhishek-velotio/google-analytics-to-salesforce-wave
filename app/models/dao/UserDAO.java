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

import javax.persistence.NoResultException;

import models.User;
import play.Logger;
import play.db.jpa.JPA;

import com.ga2sa.security.ApplicationSecurity;
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
							.setParameter("id", currentUser.id).getResultList();
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

}
