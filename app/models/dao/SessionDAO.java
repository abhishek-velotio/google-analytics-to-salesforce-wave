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

import models.Session;
import play.Logger;
import play.db.jpa.JPA;
/**
 * 
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class SessionDAO extends BaseDAO<Session> {
	
	public static Session getSession(String sessionId) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<Session>() {
				public Session apply () {
					try {
						return JPA.em().createQuery("select s from Session s where s.id = :session_id", Session.class)
								.setParameter("session_id", sessionId).getSingleResult();
					} catch (NoResultException e) {
						Logger.info("Session not found.");
					}
					return null;
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void deleteById(String sessionId) {
		delete(getSession(sessionId));
	}

}
