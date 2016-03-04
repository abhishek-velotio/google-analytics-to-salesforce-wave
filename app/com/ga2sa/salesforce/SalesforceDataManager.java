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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Optional;

import models.SalesforceAnalyticsProfile;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import play.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Files;
import com.sforce.dataset.loader.DatasetLoader;
import com.sforce.dataset.util.DatasetUtils;
import com.sforce.dataset.util.FolderType;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
/**
 * 
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class SalesforceDataManager {
	
	public static final String SF_VERSION = "36.0";
	public static final String DASHBOARD_BASE_URL = "/wave/dashboards";
	
		
	public static void uploadData(SalesforceAnalyticsProfile profile, File report) throws Exception {
				
		final String dataset = Files.getNameWithoutExtension(report.getName());
		final String datasetLabel = dataset + "-Label";
	    final String app = profile.getApplicationName();
	    final String inputFile = report.getAbsolutePath();
	    final String uploadFormat = "csv";
	    final CodingErrorAction codingErrorAction = CodingErrorAction.REPORT;   
	    final Charset fileCharset = Charset.forName("UTF-8");
	    final String Operation = "Overwrite";
	    boolean useBulkAPI = false;
	    
    	PartnerConnection partnerConnection = SalesforceSecurity.login(profile);
    	DatasetLoader.uploadDataset(inputFile, uploadFormat, codingErrorAction, fileCharset, dataset, app, datasetLabel, Operation, useBulkAPI, partnerConnection, System.out);
	}
	
	public static void createDashboard(SalesforceAnalyticsProfile profile, JsonNode dashboardJson) throws ConnectionException, URISyntaxException, ClientProtocolException, IOException {
		PartnerConnection partnerConnection = SalesforceSecurity.login(profile);
		ConnectorConfig config = partnerConnection.getConfig();
		URI u = new URI(config.getServiceEndpoint());
		URI dashboardREST = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u.getPort(), "/services/data/v36.0" + DASHBOARD_BASE_URL, null, null);

		updateDashboardFolderName(partnerConnection, profile.getApplicationName(), (ObjectNode) dashboardJson);
		
		HttpPost post = new HttpPost(dashboardREST);
		post.setHeader("Authorization", "OAuth " + config.getSessionId());
		post.addHeader("Content-Type", "application/json");
		Logger.debug(dashboardJson.toString());
		StringEntity entity = new StringEntity(dashboardJson.toString());
		entity.setContentType("application/json");
		post.setEntity(entity);
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		org.apache.http.HttpResponse response = httpClient.execute(post);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
			InputStream content = response.getEntity().getContent();
			String contentStr = IOUtils.toString(content);
			content.close();
			throw new IOException(contentStr);
		}
		httpClient.close();
		/*
		WSRequestHolder holder = WS.url(dashboardREST.toString())
				.setHeader("Authorization", "OAuth " + config.getSessionId())
				.setContentType("application/json");
		holder.post(dashboardJson).map(response -> {
			
			System.out.println(response.asJson().toString());
			return  response.asJson();
		});*/
	}
	
	private static void updateDashboardFolderName(PartnerConnection partnerConnection, String folderName, ObjectNode dashboardJson) throws ClientProtocolException, ConnectionException, URISyntaxException, IOException {
		Optional<FolderType> folder = DatasetUtils.listFolders(partnerConnection).stream().filter(f -> f.name.equals(folderName)).findFirst();
		ObjectNode folderNode = dashboardJson.putObject("folder");
		folderNode.put("id", folder.get()._uid);
		folderNode.put("name", folder.get().name);
	}
	
}
