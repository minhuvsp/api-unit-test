package com.vsp.api.product.test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

import com.vsp.api.product.ProductApiUtil;

public class ProductTest {

	public ProductTest() throws ClientProtocolException, IOException, ParseException {
		super();
	}
	
	public void test() throws ClientProtocolException, IOException, ParseException {
    	System.out.println("process ...");

		// success example
		String clientId = "30061473";
		String divisionId = "0001";
		String classId = "0001";
		String asOfDate = "2016-01-01";

//		String asOfDate = effDate.substring(0,4) + "-" + effDate.substring(4,6) + "-" + effDate.substring(6,8);
//		System.out.println("asOfDate=" + asOfDate);			

		ProductApiUtil util = new ProductApiUtil();
		util.search(clientId, divisionId, classId, asOfDate);

//		SimpleRestResponse result = getTranslatedBenefitTypeWithDate(benefitId, clientId, divisionId, classId, asOfDate);
//     	System.out.println("Done process. Rest Return=" + result);	
	}

	   public static void main( String[] args )
	    {
	    	try {
				(new ProductTest()).test();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }

	
}
