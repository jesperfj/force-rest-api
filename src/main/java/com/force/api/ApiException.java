package com.force.api;

import com.force.api.http.HttpResponse;

public class ApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	int code;
	String message;
	private HttpResponse resp;
	public ApiException(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	public ApiException(int code, String message,HttpResponse resp) {
		super();
		this.code = code;
		this.message = message;
		this.resp = resp;
	}
	public int getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public HttpResponse getResp(){
		return resp;
	}
	
	
	
}
