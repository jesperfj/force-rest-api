package com.force.api;

import java.io.IOException;
import java.util.Properties;

public class Fixture {

	static Properties props;
	static {
		props = new Properties();
		try {
			props.load(Fixture.class.getResourceAsStream("/test.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String get(String key) {
		return props.getProperty(key);
	}

}
