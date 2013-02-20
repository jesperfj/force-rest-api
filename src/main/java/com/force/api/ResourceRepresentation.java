package com.force.api;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import com.force.api.http.HttpResponse;

/**
 * A representation of a resource in the Force.com REST API. The format of the representation
 * depends on what you passed to Resource when you requested it.
 *  
 * @author jjoergensen
 *
 */
public class ResourceRepresentation {
	

	HttpResponse response;

	public ResourceRepresentation(HttpResponse value) {
		response = value;
	}

	public <T> T as(Class<T> clazz) {
		return (T) new Gson().fromJson(response.getString(), clazz);
	}
	
	public Map<?,?> asMap() {
		return new Gson().fromJson(response.getString(), Map.class);
	}

	public List<?> asList() {
	    return new Gson().fromJson(response.getString(), List.class);
	}

}
