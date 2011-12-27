package com.force.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class SessionRefreshTest {
	
	@Test
	public void testSessionRefresh() {
		
		ForceApi api = new ForceApi(new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"))
			.setClientId(Fixture.get("clientId"))
			.setClientSecret(Fixture.get("clientSecret")));

		assertNotNull(api.getIdentity());
		// This call is not available in public api
		Auth.revokeToken(api.config, api.session.accessToken);
		
		assertNotNull(api.getIdentity());
		
	}

}
