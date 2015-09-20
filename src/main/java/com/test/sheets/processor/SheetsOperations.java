package com.test.sheets.processor;

import static java.lang.String.format;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public final class SheetsOperations {

	private SheetsOperations(){}
	
	public static final String NOT_AVAILABLE = "N/A";
	public static final int EXCEL_DOC = 1;
	
	public static ListFeed getListFeedForDefaultWorksheet(SpreadsheetService service, SpreadsheetEntry spreadsheet) throws IOException, ServiceException{
		WorksheetEntry worksheet = spreadsheet.getDefaultWorksheet();
		URL listFeedUrl = worksheet.getListFeedUrl();
		ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
		return listFeed;
	}
	
	public static URL getUrlFeedForDefaultWorksheet(SpreadsheetEntry spreadsheet) throws IOException, ServiceException{
		return spreadsheet.getDefaultWorksheet().getListFeedUrl();
	}

	public static List<ListEntry> readRowsFromSpreadsheet(SpreadsheetService service, URL spreadsheetURL) {
		SpreadsheetEntry originalEntry;
		try {
			originalEntry = service.getEntry(spreadsheetURL, SpreadsheetEntry.class);
			ListFeed originalListFeed = getListFeedForDefaultWorksheet(service, originalEntry);
			List<ListEntry> allRowsEntries = new ArrayList<ListEntry>();
			allRowsEntries.addAll(originalListFeed.getEntries());
			return allRowsEntries;
		} catch (IOException e) {
			System.out.println(format("An exception occurred while reading the reference spreadsheet [%s]",e.getMessage()));
			e.printStackTrace();
		} catch (ServiceException e) {
			System.out.println(format("An exception occurred while querying the service [%s]",e.getMessage()));
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<String> getHeaderRow(SpreadsheetService service ,URL spreadsheetURL){
		List<String> header = null;
		SpreadsheetEntry spreadsheetEntry;
		try {
			spreadsheetEntry = service.getEntry(spreadsheetURL, SpreadsheetEntry.class);
			URL cellFeedUrl = new URL(spreadsheetEntry.getDefaultWorksheet().getCellFeedUrl().toString()+"?min-row=1&max-row=1");
			CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
			header =  new ArrayList<String>();
			for (CellEntry cell : cellFeed.getEntries()) {
				header.add(cell.getCell().getValue());
			}
		} catch (IOException e) {
			System.out.println(format("An exception occurred while reading the reference spreadsheet [%s]",e.getMessage()));
			e.printStackTrace();
		} catch (ServiceException e) {
			System.out.println(format("An exception occurred while querying the service [%s]",e.getMessage()));
			e.printStackTrace();
		}
		
		return header;
		
	}
	
	public static List<ListEntry> copyRowsToNewSheet(SpreadsheetService service, URL spreadsheetURL, List<ListEntry> entries, List<String> header) throws IOException, ServiceException {
		SpreadsheetEntry newSpreadsheetEntry = service.getEntry(spreadsheetURL, SpreadsheetEntry.class);
        URL newListFeedUrl = newSpreadsheetEntry.getDefaultWorksheet().getListFeedUrl();
        
        URL cellFeedUrl= newSpreadsheetEntry.getDefaultWorksheet().getCellFeedUrl();
        CellFeed cellFeed= service.getFeed (cellFeedUrl, CellFeed.class);
        
        for(int i=0;i<header.size();i++){
        	 CellEntry cellEntry= new CellEntry (1, i+1, header.get(i));
             cellFeed.insert (cellEntry);
        }
        
        for(ListEntry row: entries){
        	service.insert(newListFeedUrl, row);
        }
        
        SpreadsheetEntry newFileEntry;
        newFileEntry = service.getEntry(spreadsheetURL, SpreadsheetEntry.class);
		ListFeed newFileListFeed = getListFeedForDefaultWorksheet(service, newFileEntry);
		List<ListEntry> allRowsEntries = new ArrayList<ListEntry>();
		allRowsEntries.addAll(newFileListFeed.getEntries());
        
        return allRowsEntries;
	}
	
	
	public static void updateRowsInSheet(SpreadsheetService service, URL spreadsheetURL, List<ListEntry> entries){
		for(ListEntry row: entries){
			System.out.println("Updating row : "+row.getPlainTextContent());
			try {
				row.update();
			} catch (IOException e) {
				System.out.println("Could not update row : "+row.getPlainTextContent());
				e.printStackTrace();
			} catch (ServiceException e) {
				System.out.println("Could not update row : "+row.getPlainTextContent());
				e.printStackTrace();
			}
        }
	}
	
	public static void updateRowInSheet(SpreadsheetService service, URL spreadsheetURL, ListEntry row){
			System.out.println("Updating row : "+row.getPlainTextContent());
			try {
				row.update();
			} catch (IOException e) {
				System.out.println("Could not update row : "+row.getPlainTextContent());
				e.printStackTrace();
			} catch (ServiceException e) {
				System.out.println("Could not update row : "+row.getPlainTextContent());
				e.printStackTrace();
			}
	}

	public static String createDocumentInDrive(Drive driveService,String documentTitle, int docType){
		String fileId=NOT_AVAILABLE;
		switch(docType){
			case EXCEL_DOC:{
				File body = new File();
			    body.setTitle(documentTitle);
			    body.setMimeType("application/vnd.google-apps.spreadsheet");  
			    try {
			    	File file = driveService.files().insert(body).execute();
			        fileId=file.getId();
			   } catch (IOException e) {
			     System.out.println("An error occured: " + e);
			   }
			   break;
			}
			default:{
				System.out.println("Not a valid doctype");
			}
		}
		
		return fileId;
	}
	
	
}
