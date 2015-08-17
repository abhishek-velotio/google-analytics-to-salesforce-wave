package controllers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import models.GoogleAnalyticsProfile;
import models.Job;
import models.JobStatus;
import models.SalesforceAnalyticsProfile;
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
	
	/**
	 * method creates job form json that was requested from page
	 * 
	 * @return action result
	 */
	public static Result create () {
		
		JsonNode requestData = request().body().asJson();
		
		GoogleAnalyticsProfile gaProfile = GoogleAnalyticsProfileDAO.getProfileById(requestData.get("googleAnalyticsProfile").intValue());
		SalesforceAnalyticsProfile saProfile = SalesforceAnalyticsProfileDAO.getProfileById(requestData.get("salesforceAnalyticsProfile").intValue());
		
		Job job = new Job();
		job.setName(requestData.get("name").textValue());
		job.setGoogleAnalyticsProfile(gaProfile);
		job.setSalesforceAnalyticsProfile(saProfile);
		job.setGoogleAnalyticsProperties(requestData.get("googleAnalyticsProperties").toString());
		job.setUser(ApplicationSecurity.getCurrentUser());
		job.setStatus(JobStatus.PENDING);
		
		if (!requestData.get("repeatPeriod").isNull()) job.setRepeatPeriod(requestData.get("repeatPeriod").asText());
		
		
		if (!requestData.get("startTime").isNull()) job.setStartTime(new Timestamp(requestData.get("startTime").asLong()));
		else job.setStartTime(new Timestamp(new Date().getTime()));
		
		if (!requestData.get("includePreviousData").isNull()) job.setIncludePreviousData(requestData.get("includePreviousData").asBoolean());
		
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
}
