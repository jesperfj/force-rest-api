package com.force.api;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class DescribeTest {

	static ForceApi api = new ForceApi(new ApiConfig()
		.setUsername(Fixture.get("username"))
		.setPassword(Fixture.get("password"))
		.setClientId(Fixture.get("clientId"))
		.setClientSecret(Fixture.get("clientSecret")));

	
	@Test
	public void testDescribeGlobal() {
		DescribeGlobal dg = api.describeGlobal();
		assertNotNull(dg.getSObjects().get(0));
	}


	@Test
	public void testDescribeSObject() {
		DescribeSObject ds = api.describeSObject("Contact");
		assertNotNull(ds.getName());
		assertNotNull(ds.getAllFields());
	}

	@Test
	public void testGenerateJava() {
		DescribeSObject ds = api.describeSObject("Contact");
		PojoCodeGenerator gen = new PojoCodeGenerator();
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		try {
			gen.generateCode(s, ds, new ApiConfig().getApiVersion(),null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
