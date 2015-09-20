package com.test.sheets.processor.geocoding;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public final class Geocoding {

	private static final String LATITUDE_XPATH = "/GeocodeResponse/result/geometry/location/lat/text()";
	private static final String LONGITUDE_XPATH = "/GeocodeResponse/result/geometry/location/lng/text()";
	private static final String FORMATTED_ADDRESS_XPATH = "/GeocodeResponse/result/formatted_address/text()";
	private static final String RESPONSE_STATUS_XPATH = "/GeocodeResponse/status";
	
	private static final String RESPONSE_OK="OK";
	
	public static GeocodingResponse sendRequest(GeocodingRequest request) {
		URL requestUrl;
		try {
			requestUrl = new URL(request.buildRequestUrl());
		} catch (MalformedURLException e) {
			System.out.println("Could not create the geocoding request connection URL");
			e.printStackTrace();
			return null;
		}
		
		URLConnection connection;
		try {
			connection = requestUrl.openConnection();
			InputStream response = connection.getInputStream();
			return evaluateHttpResponse(response);	
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		    
	}
	
	public static GeocodingResponse evaluateHttpResponse(InputStream xmlReponse) throws IOException{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(xmlReponse);
			XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    Node statusNode = (Node) xpath.evaluate(RESPONSE_STATUS_XPATH, document, XPathConstants.NODE);
		    
		    if(statusNode.getTextContent().equals(RESPONSE_OK)){
		    	Node formattedAddressNode = (Node) xpath.evaluate(FORMATTED_ADDRESS_XPATH, document, XPathConstants.NODE);
		    	Node latitudeNode = (Node) xpath.evaluate(LATITUDE_XPATH, document, XPathConstants.NODE);
		    	Node longitudeNode = (Node) xpath.evaluate(LONGITUDE_XPATH, document, XPathConstants.NODE);
		    	
		    	GeocodingResponse reponse = new GeocodingResponse.Builder()
	    														.setFormattedAddress(formattedAddressNode.getTextContent())
	    														.setLatitude(latitudeNode.getTextContent())
	    														.setLongitude(longitudeNode.getTextContent())
	    														.build();
		    	return reponse;
		    } else {
		    	System.out.println(String.format("The request was not successfull. Response status : %s",statusNode.getTextContent()));
		    	return null;
		    }
		} catch (ParserConfigurationException e) {
			System.out.println("Could not parse the reponse from geocoding request");
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			System.out.println("Could not parse the reponse from geocoding request");
			e.printStackTrace();
			return null;
		} catch (XPathExpressionException e) {
			System.out.println("Could not parse the reponse from geocoding request");
			e.printStackTrace();
			return null;
		}
		
		
		
	}

}
