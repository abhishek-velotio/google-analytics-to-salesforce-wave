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
package com.ga2sa.scheduler;

import models.JobStatus;
import play.Logger;
import akka.actor.UntypedActor;

import com.ga2sa.actors.BackgroundJobInterface;
//import org.apache.commons.io.IOUtils;

/**
 * Class for background job
 * 
 * @author Igor Uvarov
 * @editor	Sergey Legostaev
 * 
 */

public class BackgroundJob extends UntypedActor{
	
	/**
	 * Method for handle message for actor. Message is instance of BackroundJob class. 
	 */
	@Override
	public void onReceive(Object obj) throws Exception {
		if (obj instanceof BackgroundJobInterface) {
			BackgroundJobInterface bgJob = (BackgroundJobInterface)obj;
			if (bgJob.getJob().getStatus().equals(JobStatus.CANCELED) == false) {
				Logger.debug("background job has been started, job name is " + bgJob.getJob().getName());
				bgJob.start();
			}
		} else {
			unhandled(obj);
		}
	}
	
}
