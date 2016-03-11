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
package com.ga2sa.salesforce;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author SLegostaev
 *
 */
public class DashboardStep {
	
	@JsonIgnore
	public List<DashboardDataset> datasets;
	
	public String name;
	
	public DashboardStep(String name) {
		this.name = name;
	}
}
