package com.force.api;

public class AuthorizationResponse {

	ApiConfig apiConfig;
	String code;
	String state;
	
	public AuthorizationResponse apiConfig(ApiConfig value) {
		apiConfig = value;
		return this;
	}
	public AuthorizationResponse code(String value) {
		code = value;
		return this;
	}
	
	public AuthorizationResponse state(String value) {
		state = value;
		return this;
	}
}
