package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
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
	public User createdBy;
	
	@ManyToOne(fetch = FetchType.LAZY)
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
