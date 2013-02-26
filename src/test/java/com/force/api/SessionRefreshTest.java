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
		// This call is not available in public service
		Auth.revokeToken(api.config, api.session.accessToken);

		Identity id = null;
		for(int i=0;i<5;i++) {
			try {
				id = api.getIdentity();
			} catch(Throwable t) {
				System.out.println("Got "+t);
				System.out.println("Attempt "+(i+1)+"/5: service failed to refresh after revoking token.");
				try {
					Thread.sleep(2000);
				} catch(InterruptedException e) {}
			}
		}
		assertNotNull(id);
		
	}

}
