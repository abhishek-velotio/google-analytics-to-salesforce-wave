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
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

import models.SalesforceAnalyticsProfile;

import com.google.common.io.Files;
import com.sforce.dataset.loader.DatasetLoader;
import com.sforce.dataset.loader.DatasetLoaderException;
import com.sforce.dataset.util.DatasetUtils;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
/**
 * 
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class SalesforceDataManager {
	
	public static void uploadData(SalesforceAnalyticsProfile profile, File report) throws Exception {
				
		String dataset = Files.getNameWithoutExtension(report.getName());
	    String datasetLabel = null;
	    String app = profile.getApplicationName();
	    String username = profile.getUsername();
	    String password = profile.getPassword();
	    String token = null;
	    String sessionId = null;
	    String endpoint = null;
	    //String action = null;
	    String inputFile = report.getAbsolutePath();
	    String uploadFormat = "csv";
	    CodingErrorAction codingErrorAction = CodingErrorAction.REPORT;   
	    Charset fileCharset = Charset.forName("UTF-8");
	    String Operation = "Overwrite";
	    boolean useBulkAPI = false;
	    
	    try {
	    	PartnerConnection partnerConnection = DatasetUtils.login(0, username, password, token, endpoint, sessionId, true);
	    	DatasetLoader.uploadDataset(inputFile, uploadFormat, codingErrorAction, fileCharset, dataset, app, datasetLabel, Operation, useBulkAPI, partnerConnection, System.out);
	    } catch (ConnectionException | MalformedURLException | DatasetLoaderException e) {
	    	throw new Exception(e.getMessage());
	    }
		
	}
}
