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

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import models.GoogleAnalyticsReport;
import models.Job;
import models.JobStatus;
import models.dao.GoogleAnalyticsReportDAO;
import models.dao.JobDAO;
import play.Logger;
import play.libs.Json;
import akka.actor.UntypedActor;

import com.ga2sa.google.Report;
import com.ga2sa.salesforce.SalesforceDataManager;
import com.google.common.io.Files;
//import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

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
		if (obj instanceof Job) {
			
			Job job = JobDAO.findById(((Job) obj).id);
			
			if (job.getStatus().equals(JobStatus.CANCELED)) return;
			
			File csvReport = null;
			
			Logger.debug("Job started: " + job.getName());
			
			GoogleAnalyticsReport previousReport = GoogleAnalyticsReportDAO.getReportByJobId(job.getId());
			
			if (job.isRepeated()) {
				
				Integer duration = (job.getRepeatPeriod().equals("week")) ? 7 : 1;
				Integer timeUnit = (job.getRepeatPeriod().equals("week") || job.getRepeatPeriod().equals("day") ) ? Calendar.DATE : Calendar.MONTH;
					
				if (job.needIncludePreviousData() && previousReport != null) {
					
					JsonNode changedProperties = Json.parse(job.getGoogleAnalyticsProperties());
					
					Calendar startDateForReport = Calendar.getInstance();
					Calendar endDateForReport = Calendar.getInstance();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
					
					try {
						endDateForReport.setTime(sdf.parse(changedProperties.get("endDate").asText()));
						startDateForReport.setTime(endDateForReport.getTime());
						startDateForReport.add(Calendar.DATE, 1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					endDateForReport.add(timeUnit, duration);
					
					ObjectNode node = (ObjectNode) changedProperties;
					node.set("startDate", new TextNode(sdf.format(startDateForReport.getTime())));
					node.set("endDate", new TextNode(sdf.format(endDateForReport.getTime())));
					
					job.setGoogleAnalyticsProperties(node.toString());
					
					csvReport = Report.getReport(job).addToCSV(previousReport.data);
				
				} else {
					csvReport = Report.getReport(job).toCSV();
				}
			} else {
				csvReport = Report.getReport(job).toCSV();
			}
			
			Logger.debug("QUERY   " + job.getGoogleAnalyticsProperties());
			
			try {
				if (job.isRepeated() && job.needIncludePreviousData() && previousReport != null) {
//					previousReport.setData(IOUtils.toByteArray(Files.asByteSource(csvReport).openStream()));
					previousReport.data = Files.toByteArray(csvReport);
					GoogleAnalyticsReportDAO.update(previousReport);
				} else {
//					GoogleAnalyticsReportDAO.save(new GoogleAnalyticsReport(job.getId(), IOUtils.toByteArray(Files.asByteSource(csvReport).openStream())));
					GoogleAnalyticsReportDAO.save(new GoogleAnalyticsReport(job.getId(), Files.toByteArray(csvReport)));
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				job.setStatus(JobStatus.FAIL);
				job.setErrors(e1.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				SalesforceDataManager.uploadData(job.getSalesforceAnalyticsProfile(), csvReport);
				job.setStatus(JobStatus.OK);			
			} catch (Exception e) {
				e.printStackTrace();
				job.setStatus(JobStatus.FAIL);
				job.setErrors(e.getMessage());
			}
			
			job.setEndTime(new Timestamp(new Date().getTime()));
			
			try {
				JobDAO.update(job);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			csvReport.delete();
		} else {
			unhandled(obj);
		}
	}
	
}
