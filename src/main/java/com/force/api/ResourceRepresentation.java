package com.force.api;

import java.util.List;
import java.util.Map;

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
		return (T) ForceApi.gson.fromJson(response.getString(), clazz);
	}
	
	public Map<?,?> asMap() {
		return ForceApi.gson.fromJson(response.getString(), Map.class);
	}

	public List<?> asList() {
	    return ForceApi.gson.fromJson(response.getString(), List.class);
	}

}
