package com.force.api;

public class OperationFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OperationFailedException(ApiError[] errors) {
		
	}
}
