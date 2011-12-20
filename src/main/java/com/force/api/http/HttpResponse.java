package com.force.api.http;

import java.io.InputStream;

public class HttpResponse {
	
	private String stringResponse;
	private byte[] byteResponse;
	private InputStream streamResponse;
	private int responseCode;

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
	
	
	

}
