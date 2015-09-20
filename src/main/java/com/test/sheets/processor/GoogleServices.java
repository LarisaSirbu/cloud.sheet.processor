package com.test.sheets.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

public final class GoogleServices {

	private GoogleServices(){}
	
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/read-and-proccess-sheet");
	private static final List<String> SCOPES = Arrays.asList("https://spreadsheets.google.com/feeds","https://docs.google.com/feeds");
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	private static FileDataStoreFactory DATA_STORE_FACTORY;
	
	static {
	    try {
	        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	        GoogleServices.DATA_STORE_FACTORY = new FileDataStoreFactory(GoogleServices.DATA_STORE_DIR);
	    } catch (Throwable t) {
	        t.printStackTrace();
	        System.exit(1);
	    }
	}
	
	
	public static Drive obtainDriveService(Credential credential, String appTitle){
		return new Drive.Builder(GoogleServices.HTTP_TRANSPORT,GoogleServices.JSON_FACTORY,credential).setApplicationName(appTitle).build();
	}

	public static SpreadsheetService obtainSpreadsheetService(Credential credential, String appTitle){
		SpreadsheetService service = new SpreadsheetService(appTitle);
		service.setOAuth2Credentials(credential);
		return service;
	}

	public static Credential authorize() {
	    InputStream in = SheetsProcessor.class.getResourceAsStream("/client_secret.json");
	    GoogleClientSecrets clientSecrets;
	    Credential credential = null;
	    
		try {
			clientSecrets = GoogleClientSecrets.load(GoogleServices.JSON_FACTORY, new InputStreamReader(in));
			GoogleAuthorizationCodeFlow flow =
		            new GoogleAuthorizationCodeFlow.Builder(
		                    GoogleServices.HTTP_TRANSPORT, GoogleServices.JSON_FACTORY, clientSecrets, GoogleServices.SCOPES)
		            .setDataStoreFactory(GoogleServices.DATA_STORE_FACTORY)
		            .setAccessType("offline")
		            .build();
		    credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	    return credential;
	}

	

}
