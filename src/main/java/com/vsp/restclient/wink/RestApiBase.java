package com.vsp.restclient.wink;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.prefs.Preferences;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RestApiBase {

	protected Logger logger = LoggerFactory.getLogger(getClass().getName());

	private RestClientUtil restClientUtil;
	private String url;
	private String token;
	
	protected abstract String getResourceUrlFormat();
	protected abstract String getServerNameTag();

	public RestClientUtil getRestClientUtil() {
		return restClientUtil;
	}

	public void setRestClientUtil(RestClientUtil restClientUtil) {
		this.restClientUtil = restClientUtil;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public RestApiBase() throws ClientProtocolException, IOException, ParseException {
		initialize();
	}

	protected void initialize() throws ClientProtocolException, IOException, ParseException {

		restClientUtil = new RestClientUtil();
		token = restClientUtil.getToken();
		
		String envName = System.getProperty("env");
		String serverNameTag = getServerNameTag();
		Preferences prefsEnv = Preferences.userRoot().node("/preferences/" + envName);
		String serverName = prefsEnv.get(serverNameTag, "String");
		url = String.format(getResourceUrlFormat(), serverName);
		logger.debug("url={} and token={}", url, token);
	}

}