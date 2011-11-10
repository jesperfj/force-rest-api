package com.force.api;

public class ApiConfig {

	ApiVersion apiVersion = ApiVersion.DEFAULT_VERSION;
	String username;
	String password;
	String refreshToken;
	String loginEndpoint = "https://login.salesforce.com";
	String clientId;
	String clientSecret;
	String redirectURI;

	public ApiConfig clone() {
		return new ApiConfig()
			.setApiVersion(apiVersion)
			.setUsername(username)
			.setPassword(password)
			.setRefreshToken(refreshToken)
			.setLoginEndpoint(loginEndpoint)
			.setClientId(clientId)
			.setClientSecret(clientSecret)
			.setRedirectURI(redirectURI);
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

	public ApiConfig setRefreshToken(String value) {
		refreshToken = value;
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

	public String getRefreshToken() {
		return refreshToken;
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
