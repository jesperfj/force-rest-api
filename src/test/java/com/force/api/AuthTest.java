package com.force.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.IOException;

public class AuthTest {

	@Test
	public void testSoapLogin() {
		ForceApi api = new ForceApi(new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password")));

		assertNotNull(api.session);
		assertNotNull(api.session.accessToken);
		assertNotNull(api.session.apiEndpoint);

	}

	@Test
	public void testIPRestrictionsOnSoap() {
		try {
			new ForceApi(new ApiConfig()
					.setUsername(Fixture.get("iprestrictedUsername"))
					.setPassword(Fixture.get("iprestrictedPassword")));
			fail("SoapLogin: Did not throw AuthException as expected when logging in with IP restricted user");
		} catch(AuthException e) {
			// pass
		}
	}

	@Test
	public void testForceURL() {
		
		ApiConfig c = new ApiConfig().setForceURL("force://login.salesforce.com?user=testuser@domain.com&password=pwd123");
		assertEquals("testuser@domain.com",c.username);
		assertEquals("pwd123", c.password);
		assertEquals("https://login.salesforce.com",c.loginEndpoint);

		c = new ApiConfig().setForceURL("force://login.salesforce.com:443?user=testuser@domain.com&password=pwd123");
		assertEquals("testuser@domain.com",c.username);
		assertEquals("pwd123", c.password);
		assertEquals("https://login.salesforce.com:443",c.loginEndpoint);

		c = new ApiConfig().setForceURL("force://login.salesforce.com:443?user=testuser@domain.com&password=pwd123&oauth_key=key123&oauth_secret=secret123");
		assertEquals("testuser@domain.com",c.username);
		assertEquals("pwd123", c.password);
		assertEquals("https://login.salesforce.com:443",c.loginEndpoint);
		assertEquals("key123",c.clientId);
		assertEquals("secret123", c.clientSecret);

		c = new ApiConfig().setForceURL("force://login.salesforce.com:443?oauth_key=key123&oauth_secret=secret123");
		assertEquals("https://login.salesforce.com:443",c.loginEndpoint);
		assertEquals("key123",c.clientId);
		assertEquals("secret123", c.clientSecret);
		
	}
	
	@Test
	public void testOAuthUsernamePasswordFlow() {
		ForceApi api = new ForceApi(new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"))
			.setClientId(Fixture.get("clientId"))
			.setClientSecret(Fixture.get("clientSecret")));

		assertNotNull(api.session);
		assertNotNull(api.session.accessToken);
		System.out.println(api.session.accessToken);
		assertNotNull(api.session.apiEndpoint);
		System.out.println(api.session.apiEndpoint);

	}

	@Test
	public void testIPRestrictionsOnOauth() {
		try {
			new ForceApi(new ApiConfig()
					.setUsername(Fixture.get("iprestrictedUsername"))
					.setPassword(Fixture.get("iprestrictedPassword"))
					.setClientId(Fixture.get("clientId"))
					.setClientSecret(Fixture.get("clientSecret")));
			fail("oauthUserNamePasswordFlow Login: Did not throw AuthException as expected when logging in with IP restricted user");
		} catch(AuthException e) {
			// pass
		}
	}


	@Test
	public void testExistingValidAccessToken() {
		ApiConfig c = new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"));

		ForceApi api = new ForceApi(c);

		ApiSession session = new ApiSession()
			.setAccessToken(api.session.accessToken)
			.setApiEndpoint(api.session.apiEndpoint);
		
		ForceApi api2 = new ForceApi(c,session);
		
		assertEquals(Fixture.get("username"),api2.getIdentity().getUsername());
		
	}
	
	@Test
	public void testRevokeToken() {
		ApiConfig c = new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"))
			.setClientId(Fixture.get("clientId"))
			.setClientSecret(Fixture.get("clientSecret"));
		ApiSession s = Auth.oauthLoginPasswordFlow(c);
		ForceApi api = new ForceApi(c,s);
		assertNotNull(api.getIdentity());
		
		Auth.revokeToken(new ApiConfig(), s.getAccessToken());
		
		try {
			api.getIdentity();
			fail("Expected an error when making an API call with a revoked token, but it succeeded");
		} catch(Throwable t) {
			
		}

	}

}
