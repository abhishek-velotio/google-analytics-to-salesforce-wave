package com.ga2sa.google;

import java.io.IOException;
import java.util.Collections;

import models.GoogleAnalyticsProfile;
import models.dao.GoogleAnalyticsProfileDAO;
import play.Play;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.AnalyticsScopes;
/**
 * 
 * 
 * 
 * @author Igor Ivarov
 * @editor Sergey Legostaev
 */
public class GoogleConnector {
	
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	public static final String CACHE_CREDENTIAL_PREFIX = "cache_credential_";
	
	private static String redirectURL;
	
	private static GoogleAuthorizationCodeFlow getFlow(GoogleAnalyticsProfile profile)  {
//		redirectURL = Play.isProd() ? profile.getRedirectUris()[1] : profile.getRedirectUris()[0];
		String[] uris = profile.getRedirectUris().split(",");
		redirectURL = uris.length > 0 ? (Play.isProd() ? uris[1] : uris[0]) : null;
		return new GoogleAuthorizationCodeFlow
			.Builder(HTTP_TRANSPORT, JSON_FACTORY, profile.getClientId(), profile.getClientSecret(), Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY))
			.setAccessType("offline")
			.setApprovalPrompt("force")
			.build();
	}
	
	public static String getAuthURL(GoogleAnalyticsProfile profile) {
		return getFlow(profile).newAuthorizationUrl().setRedirectUri(redirectURL).toURI().toString();
	}
	
	public static void exchangeCode(GoogleAnalyticsProfile profile, String authorizationCode)  {
		
		try {
			GoogleAuthorizationCodeFlow flow = getFlow(profile);
			GoogleTokenResponse response = flow.newTokenRequest(authorizationCode).setRedirectUri(redirectURL).execute();
			storeCredentials(profile, flow.createAndStoreCredential(response, null));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void storeCredentials(GoogleAnalyticsProfile profile, Credential credential) {
		
		profile.setAccessToken(credential.getAccessToken());
		profile.setRefreshToken(credential.getRefreshToken());
		profile.setConnected(true);
		
		try {
			GoogleAnalyticsProfileDAO.update(profile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static GoogleCredential getCredentials(GoogleAnalyticsProfile profile) {
		
		GoogleCredential credential = new GoogleCredential.Builder()
	    	.setTransport(HTTP_TRANSPORT)
	    	.setJsonFactory(JSON_FACTORY)
	    	.setClientSecrets(profile.getClientId(), profile.getClientSecret())
	    	.build()
			.setAccessToken(profile.getAccessToken())
			.setRefreshToken(profile.getRefreshToken());
		
		return credential;
	}
}
