package com.test.sheets.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.test.sheets.processor.geocoding.Geocoding;
import com.test.sheets.processor.geocoding.GeocodingResponse;

public class GeocodingTest {

	 @Test
	  public void parseGeocodingRequestResponseTest() throws IOException  {
		 
		 ClassLoader classLoader = getClass().getClassLoader();
		 File initialFile = new File(classLoader.getResource("response_sample.xml").getFile());
		 InputStream httpResponse = new FileInputStream(initialFile);
		 GeocodingResponse response = Geocoding.evaluateHttpResponse(httpResponse);
		
		 Assert.assertTrue(response.getFormattedAddress().equals("1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA"));
		 Assert.assertTrue(response.getLatitude().equals("37.4217550"));
		 Assert.assertTrue(response.getLongitude().equals("-122.0846330"));
	 
	 }
	
	
}
