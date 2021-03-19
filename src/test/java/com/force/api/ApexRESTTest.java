package com.force.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Smoke test covering the basics.
 * @author jjoergensen
 *
 */
public class ApexRESTTest {

	static final String TEST_NAME = "force-rest-api Apex rest test";

	@Test
	public void apexRestGetTest() {

		ForceApi api = new ForceApi(new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"))
			.setClientId(Fixture.get("clientId"))
			.setClientSecret(Fixture.get("clientSecret")));
		
		// TEST GET
		
		Account a = api.rootPath().get("/services/apexrest/ApexTest").as(Account.class);
		System.out.println(a.getName());
		assertNotNull(a.getName());
	}
}
