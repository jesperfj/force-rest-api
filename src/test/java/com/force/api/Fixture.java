package com.force.api;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Fixture {

	static Properties props;
	static {
		props = new Properties();
		try {
			props.load(Fixture.class.getResourceAsStream("/test.properties"));
		} catch (IOException e) {
			System.out.println("Couldn't load test.properties. Using environment.");
			//throw new RuntimeException(e);
			// New behavior: If file not present, set from environment and don't complain here.
			setFromEnv(props);
		}
	}
	
	public static String get(String key) {
		return props.getProperty(key);
	}

	private static void setFromEnv(Properties props) {
		for(Map.Entry<String,String> entry : System.getenv().entrySet()) {
			if(entry.getKey().startsWith("TEST_")) {
				props.setProperty(entry.getKey().substring(5), entry.getValue());
			}
		}
	}

}
