package com.vsp.restclient.wink.test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

import com.vsp.api.benefittranslate.BenefitTranslateApiUtil;

public class BenefitTranslatorTest {

	public BenefitTranslatorTest() throws ClientProtocolException, IOException, ParseException {
		super();
	}
	
	public void test() throws ClientProtocolException, IOException, ParseException {
    	System.out.println("process ...");

		// success example
		String clientId = "12120530";
		String divisionId = "0021";
		String classId = "0021";
		String benefitId = "A";
		String effDate = "20160301";

		String asOfDate = effDate.substring(0,4) + "-" + effDate.substring(4,6) + "-" + effDate.substring(6,8);
		System.out.println("asOfDate=" + asOfDate);			

		BenefitTranslateApiUtil util = new BenefitTranslateApiUtil();
		util.getTranslatedBenefitTypeWithDate(benefitId, clientId, divisionId, classId, asOfDate);

//		SimpleRestResponse result = getTranslatedBenefitTypeWithDate(benefitId, clientId, divisionId, classId, asOfDate);
//     	System.out.println("Done process. Rest Return=" + result);	
	}

	   public static void main( String[] args )
	    {
	    	try {
				(new BenefitTranslatorTest()).test();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }

	
}
