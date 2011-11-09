package com.force.api;

public class SObjectResource extends Resource {

	private String type;
	private String id;
	
	public SObjectResource setId(String value) {
		id = value;
		return this;
	}

	public SObjectResource setType(String value) {
		type = value;
		return this;
	}

	public String getPath() {
		return "/sobjects/"+type+"/"+id;
	}

}
