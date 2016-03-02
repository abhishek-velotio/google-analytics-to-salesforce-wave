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
package com.ga2sa.actors;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import models.DashboardJob;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sforce.dataset.loader.file.schema.ext.FieldType;
import com.sforce.dataset.util.DatasetUtils;
import com.sforce.dataset.util.SfdcUtils;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sun.javafx.collections.MappingChange.Map;

/**
 * @author SLegostaev
 *
 */
public class DashboardBGJob implements BackgroundJobInterface {
	
	public static void main(String[] args) throws Exception {
		
		PartnerConnection partnerConnection = DatasetUtils.login(0, "slegostaev@analyticsforce78.com", "123QWEasdfX3O7A5n7YT7ptgkPlOfFOMbDZ", null, "https://login.salesforce.com", null, true); //SalesforceSecurity.login(job.getSalesforceAnalyticsProfile());
		
		
		
		//final String url = dashboardREST.toString();
		//WSRequestHolder holder = WS.url(url).setHeader("Authorization", "OAuth " + config.getSessionId());
		//Class sfClass = getClassBySFType("dashboard", partnerConnection);
		
		
		//CtClass dashboard =  pool.get("Dashboard");
//		Object javaObj =  sfClass.newInstance();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
		//System.out.println(mapper.writeValueAsString(javaObj));
//0FK15000000CiHTGA0
		String resultStringJSON = getRequestToAPI("/wave/dashboards/0FK15000000CiHTGA0", partnerConnection.getConfig());
		System.out.println(resultStringJSON);
		
		Map<Object, Object> obj = mapper.readValue(resultStringJSON, Map.class);
		System.out.println(obj);
//		for (java.lang.reflect.Field field : obj.getClass().getFields()) {
			//System.out.println(field.getName() + " " + field.get(obj));
//		}
	}
	
	private static Class getClassBySFType(String type, PartnerConnection partnerConnection) throws ConnectionException, CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException {
		DescribeSObjectResult describeSObjectResult = partnerConnection.describeSObject(type);
		ClassPool pool = ClassPool.getDefault();
		CtClass ctClass =  pool.makeClass(type);
		for (Field sfField : describeSObjectResult.getFields()) {
			System.out.println(sfField.getName());
			Class<?> fieldJavaClass = SfdcUtils.getJavaClassFromFieldType(sfField.getType());
			CtClass fieldClass = pool.getCtClass(fieldJavaClass.getName());
			CtField field = new CtField(fieldClass, lowwerCaseFirst(sfField.getName()), ctClass);
			field.setModifiers(Modifier.PUBLIC);
			ctClass.addField(field);
		}
		
		return ctClass.toClass();
	}
	public static String lowwerCaseFirst(String value) {

		// Convert String to char array.
		char[] array = value.toCharArray();
		// Modify first element in array.
		array[0] = Character.toLowerCase(array[0]);
		// Return string.
		return new String(array);
	}
	
//	private static String getFieldName()
	
	private static String getRequestToAPI(String api, ConnectorConfig config) throws URISyntaxException, ClientProtocolException, IOException {
		URI u = new URI(config.getServiceEndpoint());
		URI restURI = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u.getPort(), "/services/data/v36.0" + api, null, null);
		System.out.println(restURI.toString());
		HttpGet get = new HttpGet(restURI);
		get.addHeader("Authorization", "OAuth " + config.getSessionId());
		HttpClient httpClient = HttpClients.createDefault();
		org.apache.http.HttpResponse response = httpClient.execute(get);
		InputStream is = response.getEntity().getContent();
		return IOUtils.toString(is);
	}
	
	private DashboardJob job;
	
	/**
	 * 
	 */
	public DashboardBGJob() {
		// TODO Auto-generated constructor stub
	}
	
	public DashboardBGJob(DashboardJob job) {
		this.job = job;
	}
	
	/* (non-Javadoc)
	 * @see com.ga2sa.actors.JobActorInterface#start()
	 */
	@Override
	public void start() {
		try {
			PartnerConnection partnerConnection = DatasetUtils.login(0, "slegostaev@analyticsforce78.com", "123QWEasdfX3O7A5n7YT7ptgkPlOfFOMbDZ", null, "https://login.salesforce.com", null, true); //SalesforceSecurity.login(job.getSalesforceAnalyticsProfile());
			ConnectorConfig config = partnerConnection.getConfig();
			URI u = new URI(config.getServiceEndpoint());
			//String dashboardUrl = partnerConnection.getConfig().getAuthEndpoint();
			URI dashboardREST = new URI(u.getScheme(), u.getUserInfo(), u.getHost(), u.getPort(), "/services/data/v36.0/wave/dashboards", null, null);
			final String url = dashboardREST.toString();
			WSRequestHolder holder = WS.url(url).setHeader("Authorization", "OAuth " + config.getSessionId());
			DescribeSObjectResult describeSObjectResult = partnerConnection.describeSObject("dashboard");
			
//			Class<?> dashboard = Class.forName("Dashboard");
			ClassPool pool = ClassPool.getDefault();
			
			
			CtClass dashboard =  pool.get("DashboardNew");
			//CtClass dashboard =  pool.makeClass("DashboardNew");
			//CtField field = new CtField(CtClass.intType, "test", dashboard);
			//dashboard.addField(field, "10");
			Class dashboardClass = dashboard.toClass();
			Object dashboardObj =  dashboardClass.newInstance();
			//dashboardClass.getField("test").set(dashboardObj, 10);
//			JsonNode json = Json.toJson(dashboardObj);
			JsonNode json = Json.toJson(new TestClass());
			//List<FieldType> lis = SfdcUtils.getFieldList("dashboard", partnerConnection, false);
			
			Promise<String> jsonPromise = holder.get().map(response -> {
				//System.out.println(response.getBody());
			    JsonNode dashboardsJson = response.asJson();
			    ObjectMapper mapper = new ObjectMapper();
			    
				return "";
			});
			
			//DatasetUtils.listFolders(partnerConnection);
		} catch (URISyntaxException | IOException |  ConnectionException | CannotCompileException | InstantiationException | IllegalAccessException | NotFoundException  | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class TestClass {
		private String test = "0";
	}
}
