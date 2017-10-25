package com.vsp.api.soldrates.test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

import com.vsp.api.soldrates.SoldRatesApiUtil;

public class SoldRatesTest {

	public SoldRatesTest() throws ClientProtocolException, IOException, ParseException {
		super();
	}
	
	public void test() throws ClientProtocolException, IOException, ParseException {
    	System.out.println("process ...");

		// success example
//		String clientId = "12120530";
//		String divisionId = "0021";
//		String classId = "0021";
//		String benefitId = "A";
//		String effDate = "20160301";
		String clientId = "30017806";
		String divisionId = "0001";
		String classId = "0001";
		String benefitId = "A";
		String effDate = "20170301";

		String asOfDate = effDate.substring(0,4) + "-" + effDate.substring(4,6) + "-" + effDate.substring(6,8);
		System.out.println("asOfDate=" + asOfDate);			

		SoldRatesApiUtil util = new SoldRatesApiUtil();
		util.getSoldRates(benefitId, clientId, divisionId, classId, asOfDate);

//		SimpleRestResponse result = getTranslatedBenefitTypeWithDate(benefitId, clientId, divisionId, classId, asOfDate);
//     	System.out.println("Done process. Rest Return=" + result);	
	}

	   public static void main( String[] args )
	    {
	    	try {
				(new SoldRatesTest()).test();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }

	
}
