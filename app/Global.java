import java.util.TimeZone;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import akka.actor.ActorRef;

import com.ga2sa.scheduler.Scheduler;

/**
 * Load global settings and initialization of scheduled jobs 
 * @Author Igor Uvarov
 * @Editor Sergey Legostaev
 **/

public class Global extends GlobalSettings {
	
	@Override
	public void onStart(Application app) {
		Logger.info("Application has been started");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Scheduler.getInstance().tell("start", ActorRef.noSender());
	}
	
}
