package com.vsp.api.soldrates;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.prefs.Preferences;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vsp.restclient.wink.RestClientUtil;

public class SoldRatesApiTest
{
	protected Logger logger = LoggerFactory.getLogger(getClass().getName());

	private static final String BENEFIT_TRANSLATOR_URL_FORMAT = "https://%s/benefit-type-translator-web/benefittypes";

	private RestClientUtil restClientUtil;
	private String url;
	private String token;
//	private JSONParser parser;
	private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static Calendar calobj;
	public static String currentDate;

	protected SoldRatesApiTest() throws ClientProtocolException, IOException, ParseException 
	{
		calobj = Calendar.getInstance();
		currentDate = df.format(calobj.getTime());

//		parser = new JSONParser();

		restClientUtil = new RestClientUtil();
		token = restClientUtil.getToken();
		
//		url = "https://api.vsp.com/benefit-type-translator-web/benefittypes";
		String envName = System.getProperty("env");
		String serverNameTag = "product-server";
		Preferences prefsEnv = Preferences.userRoot().node("/preferences/" + envName);
		String serverName = prefsEnv.get(serverNameTag, "String");
		url = String.format(BENEFIT_TRANSLATOR_URL_FORMAT, serverName);
		logger.debug("url={} and token={}", url, token);
	}
	
	private MultivaluedMap<String, String> getParameters(String benefitType, String clientId, String divisionId, String classId, String asOfDate) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl<String, String>();
		
		params.add("benefitType", benefitType);
		params.add("clientId", clientId);
		params.add("divisionId", divisionId);
		params.add("classId", classId);
		
		if (asOfDate != null)
			params.add("asOfDate", asOfDate);

		return params;
	}

	protected void getTranslatedBenefitTypeWithDate(String benefitType, String clientId, String divisionId, String classId, String asOfDate)
	{
		MultivaluedMap<String, String> params = getParameters(benefitType, clientId, divisionId, classId, asOfDate);
		translateBenefit(params);	
	}	

	private void translateBenefit(MultivaluedMap<String, String> params) {
		try {
			ClientResponse response = restClientUtil.process(url, token, params, false);
			JSONParser parser = new JSONParser();
			if (response != null && response.getMessage().equals("OK")) {
				JSONArray benefitArray = (JSONArray) parser.parse(response.getEntity(String.class));
				JSONObject benefit = (JSONObject) benefitArray.get(0);
				System.out.println((String) benefit.get("productPackageName"));
			} else {
				JSONObject info = (JSONObject) parser.parse(response.getEntity(String.class));
				System.err.println((String) info.get("description"));
			}

//			 process bases on what return and what's necessary
//			if (response != null && response.getMessage().equals("OK")) {
//				result.setSuccess(true);
//				JSONArray benefitArray = (JSONArray) parser.parse(response.getEntity(String.class));
//				JSONObject benefit = (JSONObject) benefitArray.get(0);
//				result.setBenefitName((String) benefit.get("productPackageName"));
//			} else {
//				result.setSuccess(false);
//				JSONObject info = (JSONObject) parser.parse(response.getEntity(String.class));
//				result.setError((String) info.get("description"));
//			}
		} catch (Exception e1) {
			logger.error("Exception in translateBenefit()", e1);
		}

	}

}
