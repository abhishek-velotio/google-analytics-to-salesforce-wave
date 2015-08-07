import java.sql.Timestamp;
import java.time.Instant;
import java.util.TimeZone;

import models.User;
import models.dao.UserDAO;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import akka.actor.ActorRef;

import com.ga2sa.scheduler.Scheduler;
import com.ga2sa.security.PasswordManager;
import com.ga2sa.security.UserGroup;

/**
 * Load global settings and initialization of scheduled jobs 
 * @Author Igor Uvarov
 * @Editor Sergey Legostaev
 **/

public class Global extends GlobalSettings {
	
	public void createDefaultAdmin() {
		Logger.info("Creation default user.");
		User user = UserDAO.getUserByUsername("admin");
		if (user == null) {
			user = new User();
			user.setUsername("admin");
			user.setFirstName("admin");
			user.setLastName("admin");
			user.setEmailAddress("ga2sa@mycervello.com");
			user.setRecordCreatedDateTime(Timestamp.from(Instant.now()));
			user.setIsActive(true);
			user.setRecordCreatedBy(0l);
			user.setPassword(PasswordManager.encryptPassword("cervello"));
			user.setRole(UserGroup.ADMIN.name());
			try {
				UserDAO.save(user);
				Logger.info("Default user has been created.");
			} catch (Exception e) {
				Logger.error("Default user has not been created.", e.getMessage());
			}
		} else {
			Logger.info("Default user already exists.");
		}
	}
	
	
	
	@Override
	public void onStart(Application app) {
		Logger.info("Application has been started");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		createDefaultAdmin();
		Scheduler.getInstance().tell("start", ActorRef.noSender());
	}
	
}
