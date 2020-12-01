package com.force.api;

public class ApiResponse {

	private String id;
	private ApiError[] errors;
	private boolean success;

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
