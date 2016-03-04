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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import models.dao.SalesforceAnalyticsProfileDAO;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.validator.constraints.NotEmpty;

import play.libs.Json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.utils.JsonUtil;


/**
 * The persistent class for the jobs database table.
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */


@MappedSuperclass
public abstract class Job extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@NotNull
	@Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Not allow special characters, including spaces")
	private String name;
	
	@Column(name = "errors")
	private String messages;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private JobStatus status;
	

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="salesforce_analytics_profile_id")
	@JsonIgnore
	private SalesforceAnalyticsProfile salesforceAnalyticsProfile;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="executed_by")
	private User user;

	public Job() {
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getMessages() {
		return StringEscapeUtils.escapeHtml4(this.messages);
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}


	public JobStatus getStatus() {
		return this.status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	

	public SalesforceAnalyticsProfile getSalesforceAnalyticsProfile() {
		return this.salesforceAnalyticsProfile;
	}

	public void setSalesforceAnalyticsProfile(SalesforceAnalyticsProfile salesforceAnalyticsProfile) {
		this.salesforceAnalyticsProfile = salesforceAnalyticsProfile;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	@JsonProperty(value="salesforceAnalyticsProfile")
	public JsonNode getSalesforceAnalyticsProfileId() {
		return JsonUtil.excludeFields(Json.toJson(this.salesforceAnalyticsProfile), SalesforceAnalyticsProfileDAO.privateFields);
	}
	
	@JsonProperty(value="user")
	public String getUserId() {
		return this.user.username;
	}

}