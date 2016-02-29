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
package models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import models.dao.GoogleAnalyticsProfileDAO;
import play.libs.Json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ga2sa.utils.ArrayToStringDeserializer;
import com.ga2sa.utils.JsonUtil;
import com.ga2sa.utils.StringToArraySerializer;
import com.ga2sa.utils.StringToBooleanDeserializer;

/**

 * The persistent class for the jobs database table.

 * @author SLegostaev
 *
 */

@Entity
@Table(name="dataset_jobs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetJob extends Job {

	@Column(name="end_time")
	private Timestamp endTime;
	
	@Column(name="ga_profile")
	@JsonProperty(value = "googleAnalyticsProperties_profile")
	public String gaProfile;
	
	@Column(name="ga_dimensions")
	@JsonProperty(value = "googleAnalyticsProperties_dimensions")
	@JsonDeserialize(using = ArrayToStringDeserializer.class)
	@JsonSerialize(using = StringToArraySerializer.class)
	public String gaDimensions;
	
	@Column(name="ga_metrics")
	@JsonProperty(value = "googleAnalyticsProperties_metrics")
	@JsonDeserialize(using = ArrayToStringDeserializer.class)
	@JsonSerialize(using = StringToArraySerializer.class)
	public String gaMetrics;
	
	@Column(name="ga_start_date")
	@JsonProperty(value = "googleAnalyticsProperties_startDate")
	public String gaStartDate;
	
	@Column(name="ga_end_date")
	@JsonProperty(value = "googleAnalyticsProperties_endDate")
	public String gaEndDate;
	
	@Column(name="ga_sorting")
	@JsonProperty(value = "googleAnalyticsProperties_sorting")
	@JsonDeserialize(using = ArrayToStringDeserializer.class)
	@JsonSerialize(using = StringToArraySerializer.class)
	public String gaSorting;
	
	
	@Column(name="start_time")
	private Timestamp startTime;
	
	@Column(name="next_start_time")
	private Timestamp nextStartTime;
	
	@Column(name="repeat_period")
	private String repeatPeriod;
	
	@Column(name="include_previous_data")
	@JsonDeserialize(using = StringToBooleanDeserializer.class)
	private Boolean includePreviousData;

	@NotNull
	@ManyToOne
	@JoinColumn(name="google_analytics_profile_id")
	@JsonIgnore
	private GoogleAnalyticsProfile googleAnalyticsProfile;
	
	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getNextStartTime() {
		return nextStartTime;
	}

	public void setNextStartTime(Timestamp nextStartTime) {
		this.nextStartTime = nextStartTime;
	}
	
	public String getRepeatPeriod() {
		return repeatPeriod;
	}

	public void setRepeatPeriod(String repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}
	
	public Boolean getIncludePreviousData() {
		return includePreviousData;
	}

	public void setIncludePreviousData(Boolean includePreviousData) {
		this.includePreviousData = includePreviousData;
	}

	public GoogleAnalyticsProfile getGoogleAnalyticsProfile() {
		return this.googleAnalyticsProfile;
	}

	public void setGoogleAnalyticsProfile(GoogleAnalyticsProfile googleAnalyticsProfile) {
		this.googleAnalyticsProfile = googleAnalyticsProfile;
	}
	
	public Boolean isRepeated() {
		return this.getRepeatPeriod() != null;
	}
	
	public Boolean needIncludePreviousData() {
		return this.getIncludePreviousData();
	}
	

	
	@JsonProperty(value="googleAnalyticsProfile")
	public JsonNode getGoogleProfileId() {
		return JsonUtil.excludeFields(Json.toJson(this.googleAnalyticsProfile), GoogleAnalyticsProfileDAO.privateFields);
	}
}
