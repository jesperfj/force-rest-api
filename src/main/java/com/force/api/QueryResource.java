package com.force.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class QueryResource extends Resource {

	private String query;
	
	public QueryResource setQuery(String value) {
		query = value;
		return this;
	}
	
	@Override
	public String getPath() {
		try {
			return "/query/?q="+URLEncoder.encode(query,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(e);
		}
	}

}
