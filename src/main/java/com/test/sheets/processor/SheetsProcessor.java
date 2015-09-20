package com.test.sheets.processor;
import static com.test.sheets.processor.GoogleServices.authorize;
import static com.test.sheets.processor.GoogleServices.obtainSpreadsheetService;
import static com.test.sheets.processor.SheetsOperations.getHeaderRow;
import static com.test.sheets.processor.SheetsOperations.readRowsFromSpreadsheet;
import static com.test.sheets.processor.SheetsOperations.updateRowInSheet;
import static com.test.sheets.processor.SheetsOperations.copyRowsToNewSheet;
import static java.lang.String.format;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.util.ServiceException;
import com.test.sheets.processor.geocoding.Geocoding;
import com.test.sheets.processor.geocoding.GeocodingRequest;
import com.test.sheets.processor.geocoding.GeocodingResponse;


public class SheetsProcessor 
{
	
	private static final String APPLICATION_TITLE = "cloud-sheets-processor";
	private static final String SPREADSHEET_FEED_URL = "https://spreadsheets.google.com/feeds/spreadsheets/";
	private static final String SOURCE_FILE_ID ="1yZg4D6RLkRZkotKBy840QoBjt5qEmS7uJbvy-GQbDGc";
	
	private static String newFileId;
    
	public static void main(String[] args) {
		
	System.out.println("Trying to obtain google authorization");
	Credential credential = authorize();
	if(null==credential){
		System.out.println("Could not obtain the requred authorization.");
		return;
	}
	System.out.println("Copying source sheet to user account");
	SpreadsheetService spreadsheetService = obtainSpreadsheetService(credential, APPLICATION_TITLE);
	List<ListEntry>rowsList = copySourceToUserAccount(credential,spreadsheetService);
	System.out.println("Processing user sheet...");
	processAddresses(credential,spreadsheetService,rowsList);
  }
	
	private static List<ListEntry> copySourceToUserAccount(Credential credential,SpreadsheetService spreadsheetService) {
		List<ListEntry> originalsRowsList = null;
		URL sourceFileUrl = createUrl(SPREADSHEET_FEED_URL+SOURCE_FILE_ID);
		
		if(null==sourceFileUrl){
			return null;
		}
		System.out.println("Reading reference sheet...");
		originalsRowsList = readRowsFromSpreadsheet(spreadsheetService,sourceFileUrl);
		
		if(null==originalsRowsList){
			return null;
		}
			
		System.out.println("The reference has been read.");
		
		System.out.println("Creating sheet in user cloud...");
		Drive driveService = GoogleServices.obtainDriveService(credential, APPLICATION_TITLE);
        newFileId = SheetsOperations.createDocumentInDrive(driveService,"Addresses",SheetsOperations.EXCEL_DOC);
        URL newFileUrl = createUrl(SPREADSHEET_FEED_URL+newFileId);
        System.out.println("The sheet has been created.");
        
        if(null==newFileUrl)
        	return null;
        
        List<ListEntry> newFileListEntry=null;
        try { 
        	System.out.println("Retrieving header row from reference sheet...");
        	List<String> header = getHeaderRow(spreadsheetService, sourceFileUrl);
        	System.out.println(format("The header is [%s]",header));
        	
        	if(null==header)
        		return null;
        	System.out.println("Copying rows to user sheet...");
        	newFileListEntry=copyRowsToNewSheet(spreadsheetService,newFileUrl , originalsRowsList, header);
        	System.out.println("The rows have been copied.");
        } catch (MalformedURLException e) {
			System.out.println(format("An exception occured while writing rows to new spreadsheet [%s]",e.getMessage()));
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(format("An exception occured while writing rows to new spreadsheet [%s]",e.getMessage()));
			e.printStackTrace();
		} catch (ServiceException e) {
			System.out.println(format("An exception occured while writing rows to new spreadsheet [%s]",e.getMessage()));
			e.printStackTrace();
		}
	
        return newFileListEntry;
	}

	private static URL createUrl(String urlString) {
		URL sourceFileUrl;
		try {
			sourceFileUrl = new URL(urlString);
		} catch (MalformedURLException e) {
			System.out.println(String.format("Could not create the URL object [%s]",SPREADSHEET_FEED_URL+SOURCE_FILE_ID));
			return null;
		}
		return sourceFileUrl;
	}
	
	private static void processAddresses(Credential credential,SpreadsheetService spreadsheetService, List<ListEntry> rowsList) {
		URL newFileEntryUrl = createUrl(SPREADSHEET_FEED_URL+newFileId);
		
		if(null==rowsList || null==newFileEntryUrl) return;
		
		for(ListEntry row:rowsList){
			GeocodingRequest request = new GeocodingRequest.Builder().build(row);
			GeocodingResponse response;
				response = Geocoding.sendRequest(request);
				if(null!=response){
					row.getCustomElements().setValueLocal("googleverifiedaddress", response.getFormattedAddress());
					row.getCustomElements().setValueLocal("latitude", response.getLatitude());
					row.getCustomElements().setValueLocal("longitude", response.getLongitude());
					updateRowInSheet(spreadsheetService, newFileEntryUrl, row);
				}
		}
		
	}
	
}
