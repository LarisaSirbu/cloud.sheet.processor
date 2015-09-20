package com.test.sheets.processor.geocoding;

public class GeocodingResponse {

	private String latitude;
	private String longitude;
	private String formattedAddress;
	
	public GeocodingResponse(String lat,String longit, String formattedAddress){
		this.latitude=lat;
		this.longitude=longit;
		this.formattedAddress=formattedAddress;
	}
	
	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public static class Builder{
		private String builderLatitude;
		private String builderLongitude;
		private String builderFormattedAddress;
		
		
		
		public Builder setLatitude(String builderLatitude) {
			this.builderLatitude = builderLatitude;
			return this;
		}

		public Builder setLongitude(String builderLongitude) {
			this.builderLongitude = builderLongitude;
			return this;
		}

		public Builder setFormattedAddress(String builderFormattedAddress) {
			this.builderFormattedAddress = builderFormattedAddress;
			return this;
		}

		public GeocodingResponse build(){
			return new GeocodingResponse(builderLatitude,builderLongitude,builderFormattedAddress);
		}
	}
}
