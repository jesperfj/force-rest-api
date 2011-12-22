package com.force.api;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

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
		
		try {
			c = new ApiConfig().setForceURL("login.salesforce.com:443?oauth_key=key123&oauth_secret=secret123");
			fail();
		} catch(Throwable t) {
			assertEquals("java.lang.IllegalArgumentException", t.getClass().getName());
		}
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
		assertNotNull(api.session.apiEndpoint);

	}
	
	@Test
	public void testExistingValidAccessToken() {
		ApiConfig c = new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"));

		ForceApi api = new ForceApi(c);

		ApiSession session = new ApiSession()
			.setApiConfig(c)
			.setAccessToken(api.session.accessToken)
			.setApiEndpoint(api.session.apiEndpoint);
		
		ForceApi api2 = new ForceApi(c,session);
		
		assertEquals(Fixture.get("username"),api2.getIdentity().getUsername());
		
	}
	
	@Test
	public void testAuthorizationURL() {
		String url = Auth.startOAuthWebServerFlow(new AuthorizationRequest()
			.apiConfig(new ApiConfig()
				.setClientId(Fixture.get("clientId"))
				.setRedirectURI(Fixture.get("redirectURI"))));
		System.out.println(url);

		try {
			url = Auth.startOAuthWebServerFlow(new AuthorizationRequest()
			.apiConfig(new ApiConfig()
				.setClientId(System.getenv("UNKNOWNENDVAR"))
				.setRedirectURI(System.getenv("ANOTHERUNKNOWNENDVAR"))));
		} catch(java.lang.AssertionError e) {
			return;
		}
		fail();
		
	}

	
	
	/**
	 * This test only works manually for now. Paste the URL printed when you ran
	 * testAuthorizationURL() into your browser and authenticate yourself. When
	 * you get redirected to localhost ignore the error page and copy the value
	 * of the code URL parameter into the .code("...") method argument below.
	 * Then run this and only this test (in Eclipse, remove @Ignore annotation,
	 * highlight method name, right-click and select Run As.. JUnit Test).
	 * 
	 * TODO: Automate this.
	 */
	@Test
	@Ignore
	public void testCompleteWebServerFlow() {
		ApiSession s = Auth.completeOAuthWebServerFlow(new AuthorizationResponse()
			.apiConfig(new ApiConfig()
				.setClientId(Fixture.get("clientId"))
				.setClientSecret(Fixture.get("clientSecret"))
				.setRedirectURI(Fixture.get("redirectURI")))
			.code("aPrxZibfVBKPF9tp0UFCbrd9VpKQUr5eoNNqUf.ZQS1cIp9NvWQABQLGbFRbQ_75x8m3qKa9_A%3D%3D"));
		
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
		
		Auth.revokeToken(c, s.getAccessToken());
		
		try {
			api.getIdentity();
			fail("Expected an error when making an API call with a revoked token, but it succeeded");
		} catch(Throwable t) {
			
		}
		
		// test that it throws an exception on the second revoke
		
		try {
			Auth.revokeToken(c, s.getAccessToken());
			fail("Expected AuthException when revoking already revoked token");
		} catch(AuthException e) {
			System.out.println(e.getCode()+" "+e.getMessage());
		}
	}

}
