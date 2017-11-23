package com.force.api.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Http {

	// This magic piece of code is from http://stackoverflow.com/a/39641592/82236
	// It addresses the unfathomable reality that JDK doesn't support PATCH.
	// But it messes with the standard library at runtime, so I am not feeling great about
	// doing it in a library that will coexist with other code in the same runtime. Leaving it
	// in comments for now.
	//
	//	static {
	//		try {
	//			Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
	//			methodsField.setAccessible(true);
	//			// get the methods field modifiers
	//			Field modifiersField = Field.class.getDeclaredField("modifiers");
	//			// bypass the "private" modifier
	//			modifiersField.setAccessible(true);
	//
	//			// remove the "final" modifier
	//			modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);
	//
	//         	/* valid HTTP methods */
	//			String[] methods = {
	//					"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE", "PATCH"
	//			};
	//			// set the new methods - including patch
	//			methodsField.set(null, methods);
	//
	//		} catch (Throwable e) {
	//			e.printStackTrace();
	//		}
	//	}

	static final Logger logger = LoggerFactory.getLogger(Http.class);

	static final byte[] readResponse(InputStream stream) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(stream);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buf = new byte[10000];
		int read = 0;
		while((read=bin.read(buf))!=-1) {
			bout.write(buf,0,read);
		}
		return bout.toByteArray();
	}

	public static final HttpResponse send(HttpRequest req) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(req.getUrl()).openConnection();
			if(req.getRequestTimeout()>0){
				conn.setConnectTimeout(req.getRequestTimeout());
				conn.setReadTimeout(req.getRequestTimeout());
			}
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod(req.getMethod());
			for (HttpRequest.Header h : req.getHeaders()) {
				conn.addRequestProperty(h.getKey(), h.getValue());
			}
			if(req.getAuthorization()!=null) {
				conn.addRequestProperty("Authorization", req.getAuthorization());
			}
			if (req.getContentBytes() != null) {
				conn.setDoOutput(true);
				BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
				out.write(req.getContentBytes());
				out.flush();
			} else if (req.getContentStream() != null) {
				conn.setDoOutput(true);
				final byte[] buf = new byte[2000];
				BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
				int n;
				while ((n = req.getContentStream().read(buf)) >= 0) {
					out.write(buf, 0, n);
				}
				out.flush();
			}
			int code = conn.getResponseCode();
			if (200 <= code && code < 300) {
				switch (req.getResponseFormat()) {
					case BYTE:
						return new HttpResponse().setByte(readResponse(conn.getInputStream()))
								.setResponseCode(code);
					case STRING:
						return new HttpResponse().setString(
								new String(readResponse(conn.getInputStream()), "UTF-8")).setResponseCode(
								code);
					default:
						return new HttpResponse().setStream(conn.getInputStream()).setResponseCode(code);
				}
			} else if(code == 304) {
				// 304 is a special case when the "If-Modified-Since" header is used, it is not an error,
				// it indicates that SF objects were not changed since the time specified in the "If-Modified-Since" header
				return new HttpResponse().setResponseCode(code);
			} else {
				logger.info("Bad response code: {} on request: {}", code, req);
				return new HttpResponse().setString(
						new String(readResponse(conn.getErrorStream()), "UTF-8")).setResponseCode(code);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}


}
