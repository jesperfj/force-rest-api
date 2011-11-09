package com.force.api;

public class ApiSession {
	
	ApiConfig apiConfig;
	String accessToken;
	String apiEndpoint;

	public ApiSession() {}
	
	public ApiSession(ApiConfig apiConfig, String accessToken, String apiEndpoint) {
		this.apiConfig = apiConfig;
		this.accessToken = accessToken;
		this.apiEndpoint = apiEndpoint;
	}

	public ApiConfig getApiConfig() {
		return apiConfig;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public String getApiEndpoint() {
		return apiEndpoint;
	}
	public ApiSession setApiConfig(ApiConfig apiConfig) {
		this.apiConfig = apiConfig;
		return this;
	}
	public ApiSession setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}
	public ApiSession setApiEndpoint(String apiEndpoint) {
		this.apiEndpoint = apiEndpoint;
		return this;
	}
	

}
