package com.vsp.api.soldrates;

import java.io.IOException;
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

import com.vsp.restclient.wink.RestApiBase;
import com.vsp.restclient.wink.RestClientUtil;

public class SoldRatesApiUtil extends RestApiBase
{
	private static final String RESOURCE_URL_FORMAT = "https://%s/sold-rates-fees-api-web/soldratesset";
	private static final String SERVER_NAME_TAG = "rating-server";

	protected String getResourceUrlFormat() {
		return RESOURCE_URL_FORMAT;
	}

	protected String getServerNameTag() {
		return SERVER_NAME_TAG;
	}

	public SoldRatesApiUtil() throws ClientProtocolException, IOException, ParseException 
	{
		super();
	}

	private MultivaluedMap<String, String> getParameters(String benefitType, String clientId, String divisionId, String classId, String asOfDate) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl<String, String>();
		
		params.add("clientId", clientId);
		
		if (asOfDate != null)
			params.add("asOfDate", asOfDate);

		return params;
	}

	public void getSoldRates(String benefitType, String clientId, String divisionId, String classId, String asOfDate)
	{
		MultivaluedMap<String, String> params = getParameters(benefitType, clientId, divisionId, classId, asOfDate);
		getSoldRates(params);	
	}	

	private void getSoldRates(MultivaluedMap<String, String> params) {
		try {
			String token = getToken();
//			overwrite token from postman or chrome manually 
//			token = "c4aaf366-2222-4a5a-a5f1-526c1cf602d5";
			
			ClientResponse response = getRestClientUtil().process(getUrl(), token, params, false);
			
			JSONParser parser = new JSONParser();
			if (response != null && response.getMessage().equals("OK")) {
//				JSONArray resultArray = (JSONArray) parser.parse(response.getEntity(String.class));
				JSONObject result = (JSONObject) parser.parse(response.getEntity(String.class));
				System.out.println((String) result.toString());

//				JSONObject benefit = (JSONObject) benefitArray.get(0);
//				System.out.println((String) benefit.get("productPackageName"));
			} else {
				JSONObject info = (JSONObject) parser.parse(response.getEntity(String.class));
				System.err.println((String) info.get("description"));
			}
		} catch (Exception e1) {
			logger.error("Exception in getSoldRates()", e1);
		}

	}

}
