package com.vsp.api.benefittranslate;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.vsp.api.soldrates.RestApiBase;

public class BenefitTranslateApiUtil extends RestApiBase
{
	private static final String RESOURCE_URL_FORMAT = "https://%s/benefit-type-translator-web/benefittypes";
	private static final String SERVER_NAME_TAG = "product-server";

	private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static Calendar calobj;
	public static String currentDate;
	
	protected String getResourceUrlFormat() {
		return RESOURCE_URL_FORMAT;
	}

	protected String getServerNameTag() {
		return SERVER_NAME_TAG;
	}

	public BenefitTranslateApiUtil() throws ClientProtocolException, IOException, ParseException 
	{
		super();

		calobj = Calendar.getInstance();
		currentDate = df.format(calobj.getTime());
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

	public void getTranslatedBenefitTypeWithDate(String benefitType, String clientId, String divisionId, String classId, String asOfDate)
	{
		MultivaluedMap<String, String> params = getParameters(benefitType, clientId, divisionId, classId, asOfDate);
		translateBenefit(params);	
	}	

	private void translateBenefit(MultivaluedMap<String, String> params) {
		try {
			String token = getToken();
//			overwrite token from postman or chrome manually 
//			token = "c4aaf366-2222-4a5a-a5f1-526c1cf602d5";
			
			ClientResponse response = getRestClientUtil().process(getUrl(), token, params, false);

			JSONParser parser = new JSONParser();
			if (response != null && response.getMessage().equals("OK")) {
				JSONArray resultArray = (JSONArray) parser.parse(response.getEntity(String.class));
				JSONObject benefit = (JSONObject) resultArray.get(0);
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
