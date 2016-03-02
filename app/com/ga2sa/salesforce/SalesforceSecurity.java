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

import java.net.MalformedURLException;

import models.SFAccountType;
import models.SalesforceAnalyticsProfile;
import play.Play;

import com.sforce.dataset.util.DatasetUtils;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

/**
 * @author SLegostaev
 *
 */
public class SalesforceSecurity {
	
	public static PartnerConnection login(SalesforceAnalyticsProfile profile) throws MalformedURLException, ConnectionException {
	    final String endpoint = profile.accountType == null || profile.accountType.equals(SFAccountType.PRODUCTION) 
	    		? null : Play.application().configuration().getString("salesforce_endpoint");
	    
	    return DatasetUtils.login(0, profile.getUsername(), profile.getPassword(), null, endpoint, null, true);
	}
}
