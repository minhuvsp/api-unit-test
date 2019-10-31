package com.vsp.api.audit.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsp.api.product.model.audit.AuditMessages;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

@Service("AuditServiceWinkClient")
public class AuditServiceWinkClient {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceWinkClient.class);

    private static final String AUTHORIZATION_TYPE_BEARER = "bearer";
    private static final String CLIENT_CREDENTIALS = "client_credentials";

    private static final String OAUTH_CLIENT_SCOPES = "oauth.client_scopes";
    private static final String OAUTH_CLIENT_ID = "oauth.client_id";
    private static final String OAUTH_CLIENT_SECRET = "oauth.client_secret";
    private static final String OAUTH_URI = "oauth.resource_uri";

    private static final String AUDIT_RESOURCE_URI = "audit.resource_uri";

    @Autowired
    private Environment environment;

    private String resourceURL;

    private RestClient client;


    public AuditServiceWinkClient() {
        client = new RestClient();
    }

    private String getResourceURL() {
        if (resourceURL == null) {
            try {
                resourceURL = environment.getProperty(AUDIT_RESOURCE_URI);
            } catch (Exception e) {
                logger.error("AuditServiceWinkClient:: exception in getResourceURL()", e);
            }
        }
        return resourceURL;
    }

    public String getToken() {
        String accessToken = "";

        String client_id = environment.getProperty(OAUTH_CLIENT_ID);
        String client_secret = environment.getProperty(OAUTH_CLIENT_SECRET);
        String client_scope = environment.getProperty(OAUTH_CLIENT_SCOPES);
        Resource resource = client.resource(environment.getProperty(OAUTH_URI));

        ClientResponse response = null;
        try {
            response = resource.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON)
                    .post(getOAuthParameters(client_id, client_secret, client_scope));

            if (response != null && response.getMessage().equals("OK")) {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(response.getEntity(String.class));
                accessToken = (String) json.get("access_token");
            }
        } catch (Exception ex) {
            logger.error("Exception in getToken=", ex);
        }

        return accessToken;
    }

    private MultivaluedMap<String, String> getOAuthParameters(String client_id, String client_secret, String scope) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl<String, String>();

        params.add("grant_type", CLIENT_CREDENTIALS);
        params.add("scope", scope);
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);

        return params;
    }

    private MultivaluedMap<String, String> getParameters(String clientId, String classId, String effBegin) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl<String, String>();

        params.add("clientId", clientId);
        params.add("classId", classId);
        params.add("effBegin", effBegin);

        return params;
    }

    public ClientResponse retrieveAudit(String clientId, String classId, String effBegin) {
        Resource resource = client.resource(getResourceURL());
        resource.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TYPE_BEARER + " " + getToken());
        resource.queryParams(getParameters(clientId, classId, effBegin));

        ClientResponse response = null;
        try {
            response = resource.accept(MediaType.APPLICATION_JSON).get();
        } catch (org.apache.wink.client.ClientWebException ex) {
            logger.error("Exception in retrieveAudit=" + ex.getResponse().getStatusCode(), ex);
        }

        return response;
    }

    public ClientResponse approveAudit(String clientId, String classId, String effBegin) {
        Resource resource = client.resource(getResourceURL() + "/approve");
        resource.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TYPE_BEARER + " " + getToken());
        resource.queryParams(getParameters(clientId, classId, effBegin));

        ClientResponse response = null;
        try {
            response = resource.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post("");
        } catch (org.apache.wink.client.ClientWebException ex) {
            logger.error("Exception in approveAudit=" + ex.getResponse().getStatusCode(), ex);
        }

        return response;
    }

    public ClientResponse saveAudit(AuditMessages auditMessages) {
        Resource resource = client.resource(getResourceURL());
        resource.header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TYPE_BEARER + " " + getToken());

        ClientResponse response = null;
        try {
            ObjectMapper mapperObj = new ObjectMapper();
            String t1 = mapperObj.writeValueAsString(auditMessages);

            // handle mismatches between actual audit api and the model class AuditMessage in api-gb
            String t2 = t1.replace("edits", "actions");
            String inputEntity = t2.replace("decription", "description");

            response = resource.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(inputEntity);
        } catch (Exception ex) {
            logger.error("Exception in saveAudit=", ex);
        }

        return response;
    }

}
