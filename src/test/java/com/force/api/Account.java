package com.force.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Account {

	String id;
	String name;
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	@JsonSetter(value="Id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonSetter(value="Name")
	public void setName(String name) {
		this.name = name;
	}
	
	
}
