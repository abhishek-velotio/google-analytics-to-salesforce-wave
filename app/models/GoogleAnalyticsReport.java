package models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the google_analytics_reports database table.
 * @author Igor Ivarov
 * @editor Sergey Legostaev 
 */
@Entity
@Table(name="google_analytics_reports")
@NamedQuery(name="GoogleAnalyticsReport.findAll", query="SELECT g FROM GoogleAnalyticsReport g")
public class GoogleAnalyticsReport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="GOOGLE_ANALYTICS_REPORTS_ID_GENERATOR", sequenceName="GOOGLE_ANALYTICS_REPORT_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GOOGLE_ANALYTICS_REPORTS_ID_GENERATOR")
	private Integer id;

	private byte[] data;

	@Column(name="job_id")
	private Long jobId;
	
	public GoogleAnalyticsReport() {
		
	}

	public GoogleAnalyticsReport(Long jobId, byte[] data) {
		this.jobId = jobId;
		this.data = data;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public Long getJobId(){
		return this.jobId;
	}
	
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

}