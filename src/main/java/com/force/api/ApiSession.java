package com.force.api;

public class ApiSession {
	
	ApiConfig apiConfig;
	String accessToken;
	String apiEndpoint;

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
	public void setApiConfig(ApiConfig apiConfig) {
		this.apiConfig = apiConfig;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public void setApiEndpoint(String apiEndpoint) {
		this.apiEndpoint = apiEndpoint;
	}
	

}
