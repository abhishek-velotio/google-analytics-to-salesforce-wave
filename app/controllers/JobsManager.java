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

package controllers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import models.Job;
import models.JobStatus;
import models.dao.GoogleAnalyticsProfileDAO;
import models.dao.JobDAO;
import models.dao.SalesforceAnalyticsProfileDAO;
import models.dao.filters.BaseFilter;
import models.dao.filters.BaseFilter.OrderType;
import models.dao.filters.JobFilter;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.MimeTypes;
import akka.actor.ActorRef;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.scheduler.Scheduler;
import com.ga2sa.security.Access;
import com.ga2sa.security.ApplicationSecurity;
import com.ga2sa.validators.Validator;
/**
 * 
 * Class for manage jobs
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
@Access
public class JobsManager extends Controller {
	
	private static final String NOT_FOUND = "Object not found";
	
	/**
	 * method creates job form json that was requested from page
	 * 
	 * @return action result
	 */
	public static Result create () {
		
		JsonNode requestData = request().body().asJson();
		Job job = Json.fromJson(requestData, Job.class);
		
		job.setGoogleAnalyticsProfile(GoogleAnalyticsProfileDAO.getProfileById(Long.valueOf(requestData.get("googleProfile").textValue())));
		job.setSalesforceAnalyticsProfile(SalesforceAnalyticsProfileDAO.getProfileById(Long.valueOf(requestData.get("salesforceProfile").textValue())));
		job.setUser(ApplicationSecurity.getCurrentUser());
		job.setStatus(JobStatus.PENDING);
		job.setMessages("Job has pending status");
		if (job.getStartTime() == null) job.setStartTime(Timestamp.from(Instant.now()));
		
//		if (!requestData.get("repeatPeriod").isNull()) job.setRepeatPeriod(requestData.get("repeatPeriod").asText());
//		if (!requestData.get("includePreviousData").isNull()) job.setIncludePreviousData(requestData.get("includePreviousData").asBoolean());
		
		Map<String, String> validateResult = validate(job);
		if (validateResult.isEmpty()) {
			try {
				JobDAO.save(job);
				Scheduler.getInstance().tell(job, ActorRef.noSender());
				return ok(Json.toJson(job)).as(MimeTypes.JAVASCRIPT());
			} catch (Exception e) {
				Logger.debug(e.getMessage());
				validateResult.put("error", e.getMessage());
			}
		}
		return badRequest(Json.toJson(validateResult)).as(MimeTypes.JAVASCRIPT());
		
	}
	
	public static Result countJobs() {
		Long count  = JobDAO.getCount(Job.class);
		return ok(count.toString());
	}
	
	public static Result jobs(Integer count, Integer page, String orderBy, String orderType) {
		BaseFilter filter = count == null && page == null && orderBy == null && orderType == null 
				? null : new JobFilter(Optional.ofNullable(count), Optional.ofNullable(page), Optional.ofNullable(orderBy), 
											Optional.ofNullable(orderType == null ? null : OrderType.valueOf(orderType)));
		return ok(Json.toJson(JobDAO.getJobs(filter))).as(MimeTypes.JAVASCRIPT());
	}
	
	private static Map<String, String> validate(Job job) {
		Map<String, String> validateResult = Validator.validate(job);
		if (JobDAO.isExist(job)) validateResult.put("name", "Job already exists");
		return validateResult;
	}
	
	public static Result cancel(Long id) throws Exception {
		if (id != null)  {
			Job job = JobDAO.findById(id);
			if (job != null) {
				if (job.getStatus().equals(JobStatus.PENDING) == false) return badRequest("Job already completed.");
				job.setStatus(JobStatus.CANCELED);
				job.setMessages("Job has been canceled.");
				JobDAO.update(job);
				return ok(Json.toJson(job));
			}
		}
		return notFound(NOT_FOUND);
	}
	
	public static Result delete(Long id) {
		if (id != null)  {
			Job job = JobDAO.findById(id);
			if (job != null) {
				if (job.getStatus().equals(JobStatus.PENDING)) return badRequest("Job has pending status now and can not be deleted.");
				JobDAO.delete(job);
				return ok();
			}
		}
		return notFound(NOT_FOUND);
	}
}
