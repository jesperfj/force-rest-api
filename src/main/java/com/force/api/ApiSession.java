package com.force.api;

import com.force.api.http.Http;
import com.force.api.http.HttpRequest;
import com.force.api.http.HttpResponse;

public class ApiSession implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	String accessToken;
	String apiEndpoint;
	String refreshToken;

	transient ApiConfig apiConfig;

	public ApiSession() {}
	
	public ApiSession(String accessToken, String apiEndpoint) {
		this.accessToken = accessToken;
		this.apiEndpoint = apiEndpoint;
	}

	public ApiSession(ApiConfig apiConfig) {
		this.apiConfig=apiConfig;
	}

	public ApiSession(ApiConfig apiConfig, String accessToken, String apiEndpoint) {
		this(accessToken,apiEndpoint);
		this.apiConfig=apiConfig;
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

	public ApiConfig getApiConfig() {
		return apiConfig;
	}

	public ApiSession setApiConfig(ApiConfig apiConfig) {
		this.apiConfig = apiConfig;
		return this;
	}

	public HttpResponse apiRequest(HttpRequest req) {
		req.setAuthorization("Bearer "+getAccessToken());
		HttpResponse res = Http.send(req);
		if(res.getResponseCode()==401) {
			if(refreshToken!=null) {
				System.out.println("Session expired. Refreshing session using refreshToken...");
				accessToken = Auth.refreshOauthTokenFlow(apiConfig, refreshToken).getAccessToken();
				req.setAuthorization("Bearer "+accessToken);
				res = Http.send(req);
			} else if(apiConfig.getUsername()!=null && apiConfig.getPassword()!=null) {
				System.out.println("Session expired. Refreshing session using username/password...");
				accessToken = Auth.authenticate(apiConfig).getAccessToken();
				req.setAuthorization("Bearer "+accessToken);
				res = Http.send(req);
			}
		}
		if(res.getResponseCode()>299) {
			if(res.getResponseCode()==401) {
				throw new ApiTokenException(res.getString());
			} else {
				throw new ApiException(res.getResponseCode(), res.getString());
			}
		} else if(req.getExpectedCode()!=-1 && res.getResponseCode()!=req.getExpectedCode()) {
			throw new RuntimeException("Unexpected response from Force API. Got response code "+res.getResponseCode()+
					". Was expecting "+req.getExpectedCode());
		} else {
			return res;
		}
	}


}
