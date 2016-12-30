package com.vsp.api.product;

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

public class ProductApiUtil extends RestApiBase
{
//	private static final String RESOURCE_URL_FORMAT = "https://%s/product-web/clientproducts";
	private static final String RESOURCE_URL_FORMAT = "http://%s/product-web/clientproducts";
		
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

	public ProductApiUtil() throws ClientProtocolException, IOException, ParseException 
	{
		super();

		calobj = Calendar.getInstance();
		currentDate = df.format(calobj.getTime());
	}
	
	private MultivaluedMap<String, String> getParameters(String clientId, String divisionId, String classId, String asOfDate) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl<String, String>();
		
		params.add("clientId", clientId);
		params.add("divisionId", divisionId);
		params.add("classId", classId);
		
		if (asOfDate != null)
			params.add("asOfDate", asOfDate);

		return params;
	}

	private MultivaluedMap<String, String> getParametersForRetrieve(String asOfDate) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl<String, String>();
		
		if (asOfDate != null) {
//			params.add("asOfDate", asOfDate);
			params.add("effectiveBegin", asOfDate);
		}
		
		
		
		return params;
	}

	public void search(String clientId, String divisionId, String classId, String asOfDate)
	{
		MultivaluedMap<String, String> params = getParameters(clientId, divisionId, classId, asOfDate);
		search(params);	
	}	

	private void search(MultivaluedMap<String, String> params) {
		try {
			String token = getToken();
//			overwrite token from postman or chrome manually 
//			token = "c4aaf366-2222-4a5a-a5f1-526c1cf602d5";
			
			ClientResponse response = getRestClientUtil().process(getUrl(), token, params, false);

			JSONParser parser = new JSONParser();
			if (response != null && response.getMessage().equals("OK")) {
				JSONArray resultArray = (JSONArray) parser.parse(response.getEntity(String.class));
				
				JSONObject result = (JSONObject) resultArray.get(0);
				System.out.println((JSONObject) result.get("clientProductKey"));
				System.out.println("Active=" + (Boolean) result.get("active"));
				System.out.println(result.toString());
			} else {
				System.err.println(response==null? null : response.getMessage());
				JSONObject info = (JSONObject) parser.parse(response.getEntity(String.class));
				System.err.println((String) info.get("description"));
			}

		} catch (Exception e1) {
			logger.error("Exception in product api search()", e1);
		}

	}

	public JSONObject retrieve(String key, String asOfDate)
	{
		MultivaluedMap<String, String> params = getParametersForRetrieve(asOfDate);
		return retrieve(key, params);	
	}	

	private JSONObject retrieve(String key, MultivaluedMap<String, String> params) {
		JSONObject result = null;
		try {
			String token = getToken();
			
			ClientResponse response = getRestClientUtil().process(getUrl() + "/" + key, token, params, false);

			JSONParser parser = new JSONParser();
			if (response != null && response.getMessage().equals("OK")) {
				result = (JSONObject) parser.parse(response.getEntity(String.class));
//				System.out.println((String) result.toString());
			} else {
				System.err.println(response==null? null : response.getMessage());
//				JSONObject info = (JSONObject) parser.parse(response.getEntity(String.class));
//				System.err.println((String) info.get("description"));
			}

		} catch (Exception e1) {
			logger.error("Exception in product api retrieve()", e1);
		}
		
		return result;
	}

}
