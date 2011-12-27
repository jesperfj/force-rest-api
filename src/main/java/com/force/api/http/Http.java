package com.force.api.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Http {
	
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
			if (code < 300 && code >= 200) {
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
			} else {
				System.out.println("Bad response code: " + code + " on request:\n" + req);
				HttpResponse r = new HttpResponse().setString(
						new String(readResponse(conn.getErrorStream()), "UTF-8")).setResponseCode(code);
				return r;
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}


}
