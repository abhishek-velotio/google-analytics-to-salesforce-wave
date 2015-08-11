package com.ga2sa.scheduler;

import play.libs.Akka;
import akka.actor.ActorRef;
import akka.actor.Props;
/**
 * 
 * Class for manage scheduler
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 * 
 */
public class Scheduler {
	
	private static ActorRef scheduler = Akka.system().actorOf(Props.create(SchedulerManager.class));
	
	/**
	 * Get current scheduler actorref
	 * @return
	 */
	public static ActorRef getInstance() {
		return scheduler;
	}
}
