package com.force.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogTest {

	public static void main(String[] args) {
		Logger log = Logger.getLogger("myApp");
		log.log(Level.INFO,"Testing logging");
		
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("http://www.google.com").openConnection();
			System.out.println(conn.getResponseCode());
			InputStream in = conn.getInputStream();
			byte[] buf = new byte[1000];
			int read = 0;
			while((read=in.read(buf))!=-1) {
				System.out.println(new String(buf,0,read));
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
