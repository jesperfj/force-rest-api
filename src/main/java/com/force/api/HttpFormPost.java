package com.force.api;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.URLEncoder;

public class HttpFormPost extends HttpRequest {

	StringBuffer body = new StringBuffer();
	
	public HttpFormPost url(String url) {
		baseSetUrl(url);
		conn.setDoOutput(true);
		try {
			conn.setRequestMethod("POST");
		} catch (ProtocolException e) {
			throw new RuntimeException(e);
		}
		conn.addRequestProperty("Accept", "*/*");
		conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		return this;
	}

	public HttpFormPost header(String key, String value) {
		baseAddHeader(key, value);
		return this;
	}
	
	public HttpFormPost param(String key, String value) {
		try {
			if(body.length()>0) {
				body.append("&"+key+"="+URLEncoder.encode(value, "UTF-8"));
			} else {
				body.append(key+"="+URLEncoder.encode(value, "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public HttpFormPost preEncodedParam(String key, String value) {
		if(body.length()>0) {
			body.append("&"+key+"="+value);
		} else {
			body.append(key+"="+value);
		}
		return this;
	}

	public void sendContent() {
		
		try {
			BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
			out.write(body.toString().getBytes("UTF-8"));
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public String toString() {
		return super.toString()+"Request Body:\n"+body.toString();
	}
}
