package com.force.api;

public class AuthorizationRequest {

	public enum Display { 
		PAGE("page"), 
		POPUP("popup"), 
		TOUCH("touch"), 
		MOBILE("mobile");
		
		String value;
		Display(String s) { value = s; }
		public String toString() { return value; }
	}
	
	ApiConfig apiConfig;
	String scope;
	String state;
	boolean immediate = false;
	Display display;

	public AuthorizationRequest apiConfig(ApiConfig value) {
		apiConfig = value;
		return this;
	}
	
	public AuthorizationRequest scope(String scope) {
		this.scope = scope;
		return this;
	}
	public AuthorizationRequest state(String state) {
		this.state = state;
		return this;
	}
	public AuthorizationRequest immediate() {
		immediate=true;
		return this;
	}
	public AuthorizationRequest display(Display display) {
		this.display = display;
		return this;
	}

	
}
