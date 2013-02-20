package com.force.api;

import com.google.gson.annotations.SerializedName;

public class ApiSession implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@SerializedName("access_token") String accessToken;
	@SerializedName("instance_url") String apiEndpoint;
    @SerializedName("refresh_token") String refreshToken;

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
	

}
