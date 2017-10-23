package com.force.api.http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class HttpResponse {
	
	private String stringResponse;
	private byte[] byteResponse;
	private InputStream streamResponse;
	private int responseCode;
	private Map<String, List<String>> headers;

	public int getResponseCode() {
		return responseCode;
	}
	public String getString() {
		return stringResponse;
	}
	public byte[] getByte() {
		return byteResponse;
	}
	public InputStream getStream() {
		return streamResponse;
	}
	public Map<String, List<String>> getHeaders() { return headers; }
	public HttpResponse setString(String stringResponse) {
		this.stringResponse = stringResponse;
		return this;
	}
	public HttpResponse setByte(byte[] byteResponse) {
		this.byteResponse = byteResponse;
		return this;
	}
	public HttpResponse setStream(InputStream streamResponse) {
		this.streamResponse = streamResponse;
		return this;
	}
	
	public HttpResponse setResponseCode(int value) {
		responseCode = value;
		return this;
	}

	public HttpResponse setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
		return this;
	}
}
