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
	
	public enum Scope { 
		ID ("id"),
		API("api"), 
		VISUALFORCE("visualforce"),
		WEB("web"),
		FULL("full"),
		REFRESH_TOKEN("refresh_token"),
		CHATTER_API("chatter_api");
		String value;
		
		Scope(String s) { value = s; }
		public Scope and(Scope addlScope){ 
			if(this.value!=null && this.value.trim().length()>0){
				this.value +=" "+addlScope.toString(); 
			}else{
				this.value=addlScope.toString();
			}
			return this;
		}
		public String toString() { return value; }
	}
	
	ApiConfig apiConfig;
	Scope scope;
	String state;
	boolean immediate = false;
	Display display;

	public AuthorizationRequest apiConfig(ApiConfig value) {
		apiConfig = value;
		return this;
	}
	
	public AuthorizationRequest scope(String scope) {
		this.scope = Scope.valueOf(scope);
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
