package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the google_analytics_reports database table.
 * @author Igor Ivarov
 * @editor Sergey Legostaev 
 */
@Entity
@Table(name="google_analytics_reports")
@NamedQuery(name="GoogleAnalyticsReport.findAll", query="SELECT g FROM GoogleAnalyticsReport g")
public class GoogleAnalyticsReport extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Id
//	@SequenceGenerator(name="GOOGLE_ANALYTICS_REPORTS_ID_GENERATOR", sequenceName="GOOGLE_ANALYTICS_REPORT_SEQ")
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GOOGLE_ANALYTICS_REPORTS_ID_GENERATOR")
//	private Integer id;
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	public Long id;
	
	public byte[] data;

	@Column(name="job_id")
	public Long jobId;
	
	public GoogleAnalyticsReport() {
		
	}

	public GoogleAnalyticsReport(Long jobId, byte[] data) {
		this.jobId = jobId;
		this.data = data;
	}

}