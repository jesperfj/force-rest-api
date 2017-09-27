package com.force.api;

public class CreateResponse {
	String id;
	ApiError[] errors;
	boolean success;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ApiError[] getErrors() {
		return errors;
	}
	public void setErrors(ApiError[] errors) {
		this.errors = errors;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

	
}
