package com.force.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpRequest {

	public enum ResponseFormat { STREAM, BYTE, STRING };
	
	protected HttpURLConnection conn;
	protected ResponseFormat responseFormat = ResponseFormat.STREAM;
	
	StringBuffer stringRep = new StringBuffer();
	
	public HttpRequest() {
		super();
	}

	protected void baseSetUrl(String url) {
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			stringRep.append("URL: "+url+"\n");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		conn.setInstanceFollowRedirects(true);
	}

	protected void baseAddHeader(String key, String value) {
		conn.addRequestProperty(key, value);
		stringRep.append("Header: "+key+": "+value+"\n");
	}
	
	public HttpRequest header(String key, String value) {
		baseAddHeader(key, value);
		return this;
	}

	public HttpURLConnection getConnection() {
		return conn;
	}
	
	public ResponseFormat getResponseFormat() {
		return responseFormat;
	}
	
	public void sendContent() {
		
	}

	public HttpRequest url(String value) {
		baseSetUrl(value);
		return this;
	}

	public HttpRequest method(String value) {
		try {
			conn.setRequestMethod(value);
			stringRep.append("Method: "+value+"\n");
		} catch (ProtocolException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public String toString() {
		if(conn==null) {
			return "HttpRequest with uninitialized URL";
		} else {
			return stringRep.toString();
		}
	}

}