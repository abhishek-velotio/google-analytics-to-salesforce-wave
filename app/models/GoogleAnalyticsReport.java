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

import javax.persistence.Column;
import javax.persistence.Entity;
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