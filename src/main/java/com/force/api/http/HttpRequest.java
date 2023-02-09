package com.force.api.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

	static public HttpRequest formPost() { 
			return new HttpRequest()
				.method("POST")
				.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	}
	
	public enum ResponseFormat { STREAM, BYTE, STRING };
	
	ResponseFormat responseFormat = ResponseFormat.STREAM;
	
	byte[] contentBytes;
	InputStream contentStream;

	List<Header> headers = new ArrayList<Header>();
	String method;
	String url;
	int expectedCode = -1; // -1 means no expected code specified.

	StringBuilder postParams = new StringBuilder();

	private String authorization;
	
	private int requestTimeout = 0; // in milliseconds, defaults to 0 which is no timeout (infinity)

	public HttpRequest() {
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public String getMethod() {
		return method;
	}

	public byte[] getContentBytes() {
		if(postParams.length()>0) {
			try {
				return postParams.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		} else {
			return contentBytes;
		}
	}

	public InputStream getContentStream() {
		return contentStream;
	}

	public String getUrl() {
		return url;
	}
	
	public HttpRequest expectsCode(int value) {
		expectedCode = value;
		return this;
	}

	public int getExpectedCode() {
		return expectedCode;
	}
	
	public HttpRequest header(String key, String value) {
		headers.add(new Header(key,value));
		
		return this;
	}

	public ResponseFormat getResponseFormat() {
		return responseFormat;
	}
	
	public HttpRequest responseFormat(ResponseFormat value) {
		responseFormat = value;
		return this;
	}

	public int getRequestTimeout() { return requestTimeout; }

	public void setRequestTimeout(int value){
		requestTimeout = value;
	}
	
	public HttpRequest url(String value) {
		url = value;
		return this;
	}

	public HttpRequest method(String value) {
		method = value;
		return this;
	}
	
	public HttpRequest content(byte[] value) {
		if(postParams.length()>0) {
			throw new IllegalStateException("Cannot add request content as byte[] after post parameters have been set with param() or preEncodedParam()");
		}
		contentBytes = value;
		return this;
	}

	public HttpRequest param(String key, String value) {
		if(contentBytes!=null) {
			throw new IllegalStateException("Cannot add params to HttpRequest after content(byte[]) has been called with non-null value");
		}
		try {
			if(postParams.length()>0) {
				postParams.append("&"+key+"="+URLEncoder.encode(value, "UTF-8"));
			} else {
				postParams.append(key+"="+URLEncoder.encode(value, "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public HttpRequest preEncodedParam(String key, String value) {
		if(contentBytes!=null) {
			throw new IllegalStateException("Cannot add params to HttpRequest after content(byte[]) has been called with non-null value");
		}
		if(postParams.length()>0) {
			postParams.append("&"+key+"="+value);
		} else {
			postParams.append(key+"="+value);
		}
		return this;
	}
	
//  possible future method
//	
//	public HttpRequest content(InputStream value) {
//		contentStream = value;
//		conn.setDoOutput(true);
//		return this;
//	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(method+" "+url+"\n");
		for(Header h : headers) {
			b.append(h.key+": "+h.value+"\n");
		}
		if(authorization!=null) {
			b.append("Authorization: "+authorization);
		}
		if(getContentBytes()!=null) {
			try {
				b.append("\n"+new String(getContentBytes(),"UTF-8")+"\n");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		} else if(contentStream!=null) {
			b.append("\n[streamed content. Cannot print]\n");
		}
		return b.toString();
	}
	
	public class Header {
		String key;
		String value;
		public Header() {}
		public Header(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}

	public void setAuthorization(String value) {
		authorization = value;
	}
	
	public String getAuthorization() {
		return authorization;
	}
}
