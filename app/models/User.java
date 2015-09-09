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

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ga2sa.security.PasswordManager;


/**
 * The persistent class for the users database table.
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev 
 */
@Entity
@Table(name="users")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u ORDER BY u.id ASC")
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	@NotNull
	@Email
	@Column(name="email_address", unique = true)
	public String emailAddress;
	
	@NotEmpty
	@NotNull
	@Column(name="first_name")
	public String firstName;
	
	@Column(name="is_active")
	@NotNull
	public Boolean isActive;
	
	@Column(name="last_login_date_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date lastLoginDateTime;
	
	@NotEmpty
	@NotNull
	@Column(name="last_name")
	public String lastName;
	
	@NotEmpty
	@NotNull
	public String password;
	
	@Enumerated(EnumType.STRING)
	public UserGroup role;
	
	@NotEmpty
	@NotNull
	@Column(unique = true)
	public String username;
	
	//bi-directional many-to-one association to Job
	@JsonIgnore
	@OneToMany(mappedBy="user", fetch = FetchType.LAZY)
	private List<Job> jobs;

	public User() {
	}

	@JsonProperty("password")
	private String defaultPassword () {
		return PasswordManager.PASSWORD_TMP;
	}
	
	public List<Job> getJobs() {
		return this.jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public void addJob(Job job) {
		getJobs().add(job);
	}

	public void removeJob(Job job) {
		getJobs().remove(job);
	}
	
}