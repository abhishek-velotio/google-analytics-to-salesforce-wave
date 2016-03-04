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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * The persistent class for the google_analytics_profiles database table.
 * @author Igor Ivarov
 * @editor Sergey Legostaev 
 */
//@JsonFilter("myFilter")
@Entity
@Table(name="google_analytics_profiles")
public class GoogleAnalyticsProfile extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@NotNull
	@NotEmpty
	@URL
	@Column(name="auth_provider_x509_cert_url")
	private String authProviderX509CertUrl;
	
	@NotNull
	@NotEmpty
	@URL
	@Column(name="auth_uri")
	private String authUri;
	
	@NotNull
	@NotEmpty
	@Email
	@Column(name="client_email")
	private String clientEmail;
	
	@NotNull
	@NotEmpty
	@Column(name="client_id")
	private String clientId;
	
	@NotNull
	@NotEmpty
	@Column(name="client_secret")
	private String clientSecret;
	
	@NotNull
	@NotEmpty
	@URL
	@Column(name="client_x509_cert_url")
	private String clientX509CertUrl;
	
	@NotNull
	@NotEmpty
	private String name;
	
	@NotNull
	@NotEmpty
	@URL
	@Column(name="token_uri")
	private String tokenUri;
	
	private Boolean connected = false;

	@Column(name="google_user_id")
	private String googleUserId;
	
	@Column(name="access_token")
	private String accessToken;
	
	@Column(name="refresh_token")
	private String refreshToken;
	
	@JsonIgnore
	@OneToMany(mappedBy="googleAnalyticsProfile", fetch = FetchType.LAZY)
	public List<DatasetJob> datasetJobs;

	public GoogleAnalyticsProfile() {
	}

	public String getAuthProviderX509CertUrl() {
		return this.authProviderX509CertUrl;
	}

	public void setAuthProviderX509CertUrl(String authProviderX509CertUrl) {
		this.authProviderX509CertUrl = authProviderX509CertUrl;
	}

	public String getAuthUri() {
		return this.authUri;
	}

	public void setAuthUri(String authUri) {
		this.authUri = authUri;
	}

	public String getClientEmail() {
		return this.clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return this.clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientX509CertUrl() {
		return this.clientX509CertUrl;
	}

	public void setClientX509CertUrl(String clientX509CertUrl) {
		this.clientX509CertUrl = clientX509CertUrl;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTokenUri() {
		return this.tokenUri;
	}

	public void setTokenUri(String tokenUri) {
		this.tokenUri = tokenUri;
	}

	public Boolean getConnected() {
		return connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public String getGoogleUserId() {
		return googleUserId;
	}

	public void setGoogleUserId(String googleUserId) {
		this.googleUserId = googleUserId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
//	public List<DatasetJob> getJobs() {
//		return this.jobs;
//	}
//
//	public void setJobs(List<DatasetJob> jobs) {
//		this.jobs = jobs;
//	}
//
//	public void addJob(DatasetJob job) {
//		getJobs().add(job);
//		job.setGoogleAnalyticsProfile(this);
//	}
//
//	public void removeJob(DatasetJob job) {
//		getJobs().remove(job);
//		job.setGoogleAnalyticsProfile(null);
//
//	}

}