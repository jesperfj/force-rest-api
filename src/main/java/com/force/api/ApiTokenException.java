package com.force.api;

public class ApiTokenException extends ApiException {

	private static final long serialVersionUID = 1L;
	public static final int CODE = 401;
	
	public ApiTokenException(String message) {
		super(CODE,message);
	}
}
