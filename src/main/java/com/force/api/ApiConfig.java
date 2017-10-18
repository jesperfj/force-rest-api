package com.force.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLDecoder;

public class ApiConfig {

	ApiVersion apiVersion = ApiVersion.DEFAULT_VERSION;
	String apiVersionString;
	String username;
	String password;
	String loginEndpoint = "https://login.salesforce.com";
	String clientId;
	String clientSecret;
	String redirectURI;
	SessionRefreshListener sessionRefreshListener;
	ObjectMapper objectMapper;
	int requestTimeout = 0; // in milliseconds, defaults to 0 which is no timeout (infinity)

	public ApiConfig() {
		objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public ApiConfig clone() {
		return new ApiConfig()
			.setApiVersion(apiVersion)
			.setUsername(username)
			.setPassword(password)
			.setLoginEndpoint(loginEndpoint)
			.setClientId(clientId)
			.setClientSecret(clientSecret)
			.setRedirectURI(redirectURI)
			.setObjectMapper(objectMapper)
			.setRequestTimeout(requestTimeout);
	}
	
	public ApiConfig setForceURL(String url) {
		try {
			URI uri = new URI(url);
			loginEndpoint = "https://"+uri.getHost()+(uri.getPort()>0 ? ":"+uri.getPort() : "");
			if (uri.getQuery() != null) {
				String[] params = uri.getQuery().split("&");
				for(String param : params) {
					String[] kv = param.split("=");
					if(kv[0].equals("user")) {
						username = URLDecoder.decode(kv[1],"UTF-8");
					} else if(kv[0].equals("password")) {
						password = URLDecoder.decode(kv[1],"UTF-8");
					} else if(kv[0].equals("oauth_key")) {
						clientId = URLDecoder.decode(kv[1],"UTF-8");
					} else if(kv[0].equals("oauth_secret")) {
						clientSecret = URLDecoder.decode(kv[1],"UTF-8");
					}
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Couldn't parse URL: "+url,e);
		}
		return this;
	}
	
	public ApiConfig setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
		return this;
	}

	public ApiConfig setApiVersion(ApiVersion value) {
		apiVersion = value;
		return this;
	}

	public ApiConfig setApiVersionString(String value) {
		apiVersionString = value;
		return this;
	}

	public ApiConfig setUsername(String value) {
		username = value;
		return this;
	}

	public ApiConfig setPassword(String value) {
		password = value;
		return this;
	}

	public ApiConfig setLoginEndpoint(String value) {
		loginEndpoint = value;
		return this;
	}
	
	public ApiConfig setClientId(String value) {
		clientId = value;
		return this;
	}
	
	public ApiConfig setClientSecret(String value) {
		clientSecret = value;
		return this;
	}
	public ApiConfig setSessionRefreshListener(SessionRefreshListener value) {
		sessionRefreshListener = value;
		return this;
	}

	public ApiConfig setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	/**
	 * sets both connect timeout and read timeout on HttpUrlConnection to the specified value. A value of 0 (zero)
	 * means no timeout. The default is no timeout.
	 * @param requestTimeout timeout in milliseconds. A value of 0 means no timeout.
	 * @return this ApiConfig instance
	 */
	public ApiConfig setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getLoginEndpoint() {
		return loginEndpoint;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}
	
	public String getRedirectURI() {
		return redirectURI;
	}

	public SessionRefreshListener getSessionRefreshListener() { return sessionRefreshListener; }

	public ObjectMapper getObjectMapper() { return objectMapper; }

	public int getRequestTimeout() { return requestTimeout; }

	/**
	 * @deprecated use #getApiVersionString instead
	 * @return enum representing api version
	 */
	public ApiVersion getApiVersion() {
		return apiVersion;
	}

	public String getApiVersionString() {
		return apiVersionString != null ? apiVersionString : apiVersion.toString();
	}


}
