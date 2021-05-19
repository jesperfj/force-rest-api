package com.force.api;

public class ApiSession implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	String accessToken;
	String apiEndpoint;
	String refreshToken;
	String instanceUrl;
	String id;

	public ApiSession() {}
	
	public ApiSession(String accessToken, String apiEndpoint) {
		this.accessToken = accessToken;
		this.apiEndpoint = apiEndpoint;
	}

	public String getAccessToken() {
		return accessToken;
	}
	public String getApiEndpoint() {
		return apiEndpoint;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public String getInstanceUrl() {
		return instanceUrl;
	}
	public String getId() {
		return id;
	}
	public ApiSession setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}
	public ApiSession setApiEndpoint(String apiEndpoint) {
		this.apiEndpoint = apiEndpoint;
		return this;
	}
	public ApiSession setRefreshToken(String value) {
		refreshToken = value;
		return this;
	}
	public ApiSession setInstanceUrl(String value) {
		instanceUrl = value;
		return this;
	}
	public ApiSession setId(String value) {
		id = value;
		return this;
	}

}
