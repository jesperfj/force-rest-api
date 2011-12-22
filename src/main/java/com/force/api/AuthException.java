package com.force.api;

public class AuthException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	int code;
	String message;
	
	public AuthException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
