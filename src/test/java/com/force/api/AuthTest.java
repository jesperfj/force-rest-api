package com.force.api;

import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AuthTest {

	@Test
	public void testSoapLogin() {
		DataApi api = new DataApi(new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password")));

		assertNotNull(api.session);
		assertNotNull(api.session.accessToken);
		assertNotNull(api.session.apiEndpoint);

	}
	
	@Test
	public void testOAuthUsernamePasswordFlow() {
		DataApi api = new DataApi(new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"))
			.setClientId(Fixture.get("clientId"))
			.setClientSecret(Fixture.get("clientSecret")));

		assertNotNull(api.session);
		assertNotNull(api.session.accessToken);
		assertNotNull(api.session.apiEndpoint);

	}
	
	@Test
	public void testExistingValidAccessToken() {
		ApiConfig c = new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"));

		DataApi api = new DataApi(c);

		ApiSession session = new ApiSession()
			.setApiConfig(c)
			.setAccessToken(api.session.accessToken)
			.setApiEndpoint(api.session.apiEndpoint);
		
		DataApi api2 = new DataApi(c,session);
		
		assertEquals(Fixture.get("username"),api2.getIdentity().getUsername());
		
	}
}
