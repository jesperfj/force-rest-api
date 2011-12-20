package com.force.api.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpRequest {

	public enum ResponseFormat { STREAM, BYTE, STRING };
	
	protected HttpURLConnection conn;
	protected ResponseFormat responseFormat = ResponseFormat.STREAM;
	
	StringBuffer stringRep = new StringBuffer();
	byte[] contentBytes;
	InputStream contentStream;
	
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
	
	public HttpRequest responseFormat(ResponseFormat value) {
		responseFormat = value;
		return this;
	}
	public void sendContent() {
		try {
			if (contentBytes != null) {
				BufferedOutputStream out = new BufferedOutputStream(
						conn.getOutputStream());
				out.write(contentBytes);
				out.flush();
			} else if (contentStream != null) {
				final byte[] buf = new byte[2000];
				BufferedOutputStream out = new BufferedOutputStream(
						conn.getOutputStream());
				int n;
				while((n=contentStream.read(buf)) >= 0) {
					out.write(buf,0,n);
				}
				out.flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
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
	
	public HttpRequest content(byte[] value) {
		contentBytes = value;
		conn.setDoOutput(true);
		return this;
	}
	
	public HttpRequest content(InputStream value) {
		contentStream = value;
		conn.setDoOutput(true);
		return this;
	}
	
	public String toString() {
		if(conn==null) {
			return "HttpRequest with uninitialized URL";
		} else {
			return stringRep.toString()+(contentBytes!=null ? "\nBODY\n\n"+new String(contentBytes) : "");
		}
	}

}