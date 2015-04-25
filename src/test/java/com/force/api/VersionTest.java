package com.force.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jjoergensen on 4/25/15.
 */
public class VersionTest {

    @Test
    public void testSoapLoginCustomVersion() {
        ForceApi api = new ForceApi(new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"))
                .setApiVersionString("v32.0"));

        assertNotNull(api.session);
        assertNotNull(api.session.accessToken);
        assertNotNull(api.session.apiEndpoint);

    }

    @Test
    public void testOAuthLoginCustomVersion() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"))
                .setClientId(Fixture.get("clientId"))
                .setClientSecret(Fixture.get("clientSecret"))
                .setApiVersionString("v32.0");
        ApiSession s = Auth.oauthLoginPasswordFlow(c);
        ForceApi api = new ForceApi(c,s);
        assertEquals(Fixture.get("username"), api.getIdentity().getUsername());
    }

}
