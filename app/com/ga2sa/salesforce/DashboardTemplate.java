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

import models.DashboardType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author SLegostaev
 *
 */
public class DashboardTemplate {
	
	@JsonProperty("id")
	public DashboardType dashboardType;
	
	public String name;
	
	@JsonIgnore
	public ObjectNode objectNode;
	
	
	public List<DashboardStep> steps;	
	
	public DashboardTemplate(DashboardType dashboardType) {
		this.dashboardType = dashboardType;
		this.name = dashboardType.name().replaceAll("_", " ");
	}
	
}
