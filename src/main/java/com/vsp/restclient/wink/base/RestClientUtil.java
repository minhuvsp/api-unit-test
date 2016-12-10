package com.vsp.restclient.wink.base;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RestClientUtil 
{
	protected Logger logger = LoggerFactory.getLogger(getClass().getName());

	private static final String AUTH_URL_FORMAT = "http://%s/as/oauth/token";

	protected static final String STRING_TYPE = "String";

	private static final String PROPERTY_FILE_DEFAULT = "src/main/resources/properties.xml";

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String AUTHORIZATION = "Authorization";

	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String CONTENT_TYPE_ENCODED = "application/x-www-form-urlencoded";
	private static final String AUTHORIZATION_TYPE_BASIC = "Basic";
	private static final String AUTHORIZATION_TYPE_BEARER = "bearer";

	private String envName;
	private String auth_server;
	private String auth_token;

	private String scope = "claim_view client_view eligibility_view member_view product_view provider_view rating_view reference_view";

//	private RestClient client;


	public RestClientUtil() {
		readPreferences();
//        client = new RestClient();
 	}

	private void readPreferences() {
		InputStream is = null;
		String propertyFileName = null;
		envName = System.getProperty("env");

		propertyFileName = PROPERTY_FILE_DEFAULT;

		try {
			is = new BufferedInputStream(new FileInputStream(propertyFileName));
			
			System.out.println("Reading preferences from " + propertyFileName);
			Preferences.importPreferences(is);
			is.close();
			
			Preferences prefsEnv = Preferences.userRoot().node("/preferences/" + getEnvName());
			auth_server = prefsEnv.get("auth_server", STRING_TYPE);
			auth_token = prefsEnv.get("auth_token", STRING_TYPE);

		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	public String getToken() throws ClientProtocolException, IOException, ParseException 
	{
       	return getToken(auth_server, auth_token, scope);
	}
	
	public String getToken(String server, String token, String scope) throws ClientProtocolException, IOException, ParseException 
	{
		HttpClient client = HttpClientBuilder.create().build();
		
		String authUrl = String.format(AUTH_URL_FORMAT, server);
		HttpPost post = new HttpPost(authUrl);
		 
//		post.setHeader("Accept-Language", "en-US,en;q=0.5");
//		post.setHeader("Connection", "keep-alive");
		post.setHeader("Accept", CONTENT_TYPE_JSON);
		post.setHeader(AUTHORIZATION, AUTHORIZATION_TYPE_BASIC + " " + token);
		post.setHeader(CONTENT_TYPE, CONTENT_TYPE_ENCODED);
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
		urlParameters.add(new BasicNameValuePair("scope", scope));
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		 
		HttpResponse response = client.execute(post);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
	 
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	 
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}		
	   	System.out.println("result=" + result);

        String accessToken = null;
	   	if (result != null) {
			JSONParser parser = new JSONParser();
	        JSONObject json = (JSONObject) parser.parse(result.toString());	   	
	        System.out.println("json=" + json);
	        
	        accessToken = (String) json.get("access_token");
	        System.out.println("access_token=" + accessToken);
	   	}

	   	return accessToken;
	}

	public ClientResponse process(String url, String token, MultivaluedMap<String, String> params, boolean displayFullOutput) 
	{
		RestClient client = new RestClient();
		Resource resource = client.resource(url);
        resource.header(CONTENT_TYPE, CONTENT_TYPE_JSON);
        resource.header(AUTHORIZATION, AUTHORIZATION_TYPE_BEARER + " " + token);

        if (params != null)
        	resource.queryParams(params);

    	ClientResponse response = null;
        try {
//        	response = resource.accept(CONTENT_TYPE_JSON).get(ClientResponse.class);
        	response = resource.accept(CONTENT_TYPE_JSON).get(); // avoid ClientWebException
         } catch (org.apache.wink.client.ClientWebException ex) {
        	 logger.debug("Exception in process=" + ex.getResponse().getStatusCode(), ex);
         }

        if (displayFullOutput) {
            System.out.println("Response Message=" + response.getMessage());
        	System.out.println("Result=" + response.getEntity(String.class));
        }
        
        return response;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

}