package com.force.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Http {
	
	static final byte[] readResponse(HttpRequest req) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(req.getConnection().getInputStream());
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
			req.sendContent();
			int code = req.getConnection().getResponseCode();
			
			if(code<300 && code >=200) {
				switch(req.responseFormat) {
				case BYTE:
					return new HttpResponse()
						.setByte(readResponse(req))
						.setResponseCode(code);
				case STRING:
					return new HttpResponse()
					.setString(new String(readResponse(req),"UTF-8"))
					.setResponseCode(code);
				default:
					return new HttpResponse()
				    .setStream(req.getConnection().getInputStream())
					.setResponseCode(code);
				}
			} else {
				System.out.println("Bad response code: "+code+" on request:\n"+req);
				HttpResponse r = new HttpResponse()
					.setString(new String(readResponse(req),"UTF-8"))
					.setResponseCode(code);
				return r;
			}
		} catch(IOException e) {
			throw new RuntimeException(req.toString(),e);
		}
		
	}


}
