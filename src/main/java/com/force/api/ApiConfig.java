package com.force.api;

import java.net.URI;
import java.net.URLDecoder;

public class ApiConfig {

	ApiVersion apiVersion = ApiVersion.DEFAULT_VERSION;
	String username;
	String password;
	String loginEndpoint = "https://login.salesforce.com";
	String clientId;
	String clientSecret;
	String redirectURI;

	public ApiConfig clone() {
		return new ApiConfig()
			.setApiVersion(apiVersion)
			.setUsername(username)
			.setPassword(password)
			.setLoginEndpoint(loginEndpoint)
			.setClientId(clientId)
			.setClientSecret(clientSecret)
			.setRedirectURI(redirectURI);
	}
	
	public ApiConfig setForceURL(String url) {
		try {
			URI uri = new URI(url);
			loginEndpoint = "https://"+uri.getHost()+(uri.getPort()>0 ? ":"+uri.getPort() : "");
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

	public ApiVersion getApiVersion() {
		return apiVersion;
	}


	
}
