package com.force.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DescribeTest {

	@Test
	public void testit() {
		ForceApi api = new ForceApi(new ApiConfig()
		.setUsername(Fixture.get("username"))
		.setPassword(Fixture.get("password"))
		.setClientId(Fixture.get("clientId"))
		.setClientSecret(Fixture.get("clientSecret")));

		DescribeGlobal dg = api.describeGlobal();
		assertNotNull(dg.getEncoding());
		assertNotNull(dg.getMaxBatchSize());
		assertNotNull(dg.getSObjects().get(0));
	}
}
