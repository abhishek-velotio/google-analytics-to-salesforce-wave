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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ga2sa.security.ApplicationSecurity;

/**
 * Base Entity class contains common fields
 * 
 * @author Sergey Legostaev
 *
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Column(name = "created_date", columnDefinition="TIMESTAMP default CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	public Date created;
	
	@Column(name = "modified_date", columnDefinition="TIMESTAMP default CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	public Date modified;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	public User createdBy;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	public User modifiedBy;
	
	@Version
	public Long version;
	
	@PrePersist void prePersist() {
		created = new Date();
		createdBy = ApplicationSecurity.getCurrentUser();
	}
	
	@PreUpdate void preUpdate() {
		modified = new Date();
		modifiedBy = ApplicationSecurity.getCurrentUser();
	}
	
}
