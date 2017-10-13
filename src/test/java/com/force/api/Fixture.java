package com.force.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class Fixture {

	static Properties props;
	static {
		InputStream in = Fixture.class.getResourceAsStream("/test.properties");
		if(in!=null) {
			System.out.println("Setting test config from test.properties resource");
			props = new Properties();
			try {
				props.load(Fixture.class.getResourceAsStream("/test.properties"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			System.out.println("Setting test config from environment");
		}
	}
	
	public static String get(String key) {
		if(props!=null) {
			return props.getProperty(key);
		} else {
			return System.getenv(key);
		}
	}

	private static void setFromEnv(Properties props) {
		for(Map.Entry<String,String> entry : System.getenv().entrySet()) {
			if(entry.getKey().startsWith("TEST_")) {
				props.setProperty(entry.getKey().substring(5), entry.getValue());
			}
		}
	}

}
