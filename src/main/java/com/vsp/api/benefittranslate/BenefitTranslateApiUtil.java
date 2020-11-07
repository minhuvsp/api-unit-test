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

import com.vsp.restclient.wink.RestApiBase;

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

			if (response != null) {
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = null;
				Object obj = parser.parse(response.getEntity(String.class));
				// need test to see if the result is array of objects or just one object
				if (obj instanceof JSONArray) {
					JSONArray resultArray = (JSONArray)obj;
					jsonObject = (JSONObject)resultArray.get(0);
				} else if (obj instanceof JSONObject) {
					jsonObject = (JSONObject)obj;
				}

				if (!response.getMessage().equals("OK")) {
//				errorMessage = (String) jsonObject.get("message");
					System.err.println((String) jsonObject.get("description"));
				} else {
					System.out.println((String) jsonObject.get("productPackageName"));
				}
			}

//			if (response != null && response.getMessage().equals("OK")) {
//				JSONArray resultArray = (JSONArray) parser.parse(response.getEntity(String.class));
//				JSONObject benefit = (JSONObject) resultArray.get(0);
//				System.out.println((String) benefit.get("productPackageName"));
//			} else {
//				System.err.println(response==null? null : response.getMessage());
//				JSONObject info = (JSONObject) parser.parse(response.getEntity(String.class));
//				System.err.println((String) info.get("description"));
//			}

		} catch (Exception e1) {
			logger.error("Exception in translateBenefit()", e1);
		}

	}

}
