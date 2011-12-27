package com.force.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DescribeTest {

	@Test
	public void testDescribeGlobal() {
		ForceApi api = new ForceApi(new ApiConfig()
		.setUsername(Fixture.get("username"))
		.setPassword(Fixture.get("password"))
		.setClientId(Fixture.get("clientId"))
		.setClientSecret(Fixture.get("clientSecret")));

		DescribeGlobal dg = api.describeGlobal();
		assertNotNull(dg.getSObjects().get(0));
	}


	@Test
	public void testDescribeSObject() {
		ForceApi api = new ForceApi(new ApiConfig()
		.setUsername(Fixture.get("username"))
		.setPassword(Fixture.get("password"))
		.setClientId(Fixture.get("clientId"))
		.setClientSecret(Fixture.get("clientSecret")));

		DescribeSObject ds = api.describeSObject("Contact");
		assertNotNull(ds.getName());
		assertNotNull(ds.getFields()[0].getName());
	}
}
