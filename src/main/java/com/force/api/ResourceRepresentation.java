package com.force.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.api.http.HttpResponse;

/**
 * A representation of a resource in the Force.com REST API. The format of the representation
 * depends on what you passed to Resource when you requested it.
 *  
 * @author jjoergensen
 *
 */
public class ResourceRepresentation {
	
	public static final ObjectMapper jsonMapper = new ObjectMapper();

	HttpResponse response;

	public ResourceRepresentation(HttpResponse value) {
		response = value;
	}

	public <T> T as(Class<T> clazz) {
		try {
			return (T) jsonMapper.readValue(response.getStream(), clazz);
		} catch (JsonParseException e) {
			throw new ResourceException(e);
		} catch (JsonMappingException e) {
			throw new ResourceException(e);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}
	
	public Map<?,?> asMap() {
		try {
			return jsonMapper.readValue(response.getStream(), Map.class);
		} catch (JsonParseException e) {
			throw new ResourceException(e);
		} catch (JsonMappingException e) {
			throw new ResourceException(e);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	public List<?> asList() {
		try {
			return jsonMapper.readValue(response.getStream(), List.class);
		} catch (JsonParseException e) {
			throw new ResourceException(e);
		} catch (JsonMappingException e) {
			throw new ResourceException(e);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	/**
	 *
	 * @return the HTTP response code of the underlying request if it was between 200 and 299. Any code outside of that
	 * range will result in an ApiException being thrown before a ResourceRepresentation is instantiated.
	 */
	public int getResponseCode() {
		return response.getResponseCode();
	}
}
