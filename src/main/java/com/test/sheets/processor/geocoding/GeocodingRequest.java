package com.test.sheets.processor.geocoding;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gdata.data.spreadsheet.ListEntry;

public class GeocodingRequest {

	public static final String MAPS_KEY="AIzaSyBEIO0apfqYTbIU_yk8xqOXpHN2vSUlOEQ";
	public static final String GEOCODING_SERVICE_XML_URL = "https://maps.googleapis.com/maps/api/geocode/xml";
	
	private String address;
	private String postalCode;
	private String city;
	private String country;
	private String stateOrProvince;
	
	public GeocodingRequest(String address,String postalCode, String city, String country, String stateOrProvince){
		this.address=address;
		this.postalCode=postalCode;
		this.city=city;
		this.country=country;
		this.stateOrProvince=stateOrProvince;
	}
	
	public String buildRequestUrl(){
		String requestUrl = null;
		try {
			String charset = "UTF-8";
			String addressParameter = address+","+city+","+stateOrProvince+","+country+","+postalCode;
			String urlAddressParameter  = URLEncoder.encode(addressParameter, charset);
			String language= "en-GB";
			requestUrl = GEOCODING_SERVICE_XML_URL+"?"+String.format("address=%s&key=%s&language=%s",urlAddressParameter, MAPS_KEY, language);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return requestUrl;
	}
	
	public static class Builder{
		private String builderAddress;
		private String builderPostalCode;
		private String builderCity;
		private String builderCountry;
		private String builderState;
		
		
		
		public Builder setAddress(String builderAddress) {
			this.builderAddress = builderAddress;
			return this;
		}

		public Builder setPostalCode(String builderPostalCode) {
			this.builderPostalCode = builderPostalCode;
			return this;
		}

		public Builder setCity(String builderCity) {
			this.builderCity = builderCity;
			return this;
		}

		public Builder setCountry(String builderCountry) {
			this.builderCountry = builderCountry;
			return this;
		}

		public Builder setState(String builderState) {
			this.builderState = builderState;
			return this;
		}

		public GeocodingRequest build(){
			return new GeocodingRequest(builderAddress,builderPostalCode,builderCity,builderCountry,builderState);
		}
		
		public GeocodingRequest build(ListEntry row){
			
			this.builderAddress=row.getCustomElements().getValue("address");
			this.builderPostalCode=row.getCustomElements().getValue("postalcode");
			this.builderCity=row.getCustomElements().getValue("city");
			this.builderCountry=row.getCustomElements().getValue("country");
			this.builderState=row.getCustomElements().getValue("stateprovince");
			
			return build();
		}
	}
	
}
