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
package com.ga2sa.actors;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import models.DatasetJob;
import models.GoogleAnalyticsReport;
import models.JobStatus;
import models.dao.GoogleAnalyticsReportDAO;
import models.dao.JobDAO;

import org.apache.commons.lang3.StringEscapeUtils;

import play.Logger;

import com.ga2sa.google.Report;
import com.ga2sa.salesforce.SalesforceDataManager;
import com.google.common.io.Files;

/**
 * @author SLegostaev
 *
 */
public class DatasetBGJob implements BackgroundJobInterface {
	
	private DatasetJob job;

	public DatasetBGJob(DatasetJob job) {
		this.job = job;
	}
	
	/* (non-Javadoc)
	 * @see com.ga2sa.actors.BackgroundJobInterface#start()
	 */
	@Override
	public void start() {
		
		if (job.getStatus().equals(JobStatus.CANCELED)) return;
		
		Report report = null;
		File csvReport = null;
		Logger.debug("Job started: " + job.getName());

		GoogleAnalyticsReport previousReport = GoogleAnalyticsReportDAO.getReportByJobId(job.id);
		try {
			if (job.isRepeated()) {
				
				Integer duration = (job.getRepeatPeriod().equals("week")) ? 7 : 1;
				Integer timeUnit = (job.getRepeatPeriod().equals("week") || job.getRepeatPeriod().equals("day") ) ? Calendar.DATE : Calendar.MONTH;
					
				if (job.needIncludePreviousData() && previousReport != null) {
					
					Calendar startDateForReport = Calendar.getInstance();
					Calendar endDateForReport = Calendar.getInstance();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
					
					endDateForReport.setTime(sdf.parse(job.gaEndDate));
					startDateForReport.setTime(endDateForReport.getTime());
					startDateForReport.add(Calendar.DATE, 1);
					
					endDateForReport.add(timeUnit, duration);
					job.gaStartDate = sdf.format(startDateForReport.getTime());
					job.gaEndDate = sdf.format(endDateForReport.getTime());
					
					report = Report.getReport(job);
					csvReport = report.addToCSV(previousReport.data);
				
				} 
			}
			
			if (csvReport == null)  {
				report = Report.getReport(job);
				csvReport = report.toCSV();
			}
		
			if (job.isRepeated() && job.needIncludePreviousData() && previousReport != null) {
				previousReport.data = Files.toByteArray(csvReport);
				GoogleAnalyticsReportDAO.update(previousReport);
			} else {
				GoogleAnalyticsReportDAO.save(new GoogleAnalyticsReport(job.id, Files.toByteArray(csvReport)));
			}
			
			SalesforceDataManager.uploadData(job.getSalesforceAnalyticsProfile(), csvReport);
			job.setStatus(JobStatus.OK);
			job.setMessages(report.getData().size() + " rows have been loaded.");
			
		} catch (Exception e) {
			e.printStackTrace();
			job.setStatus(JobStatus.FAIL);
			job.setMessages( StringEscapeUtils.escapeHtml4(e.getMessage()));
		}
		
		job.setEndTime(new Timestamp(new Date().getTime()));
		
		try {
			JobDAO.update(job);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (csvReport != null) csvReport.delete();
	}
}
