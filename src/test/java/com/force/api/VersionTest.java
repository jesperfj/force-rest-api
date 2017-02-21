package com.force.api;

import junit.framework.Assert;
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

    @Test
    public void testVersionCalculation() {
        String version = ApiVersion.resolveVersionString(ApiVersionSeason.WINTER, 2017);

        assertEquals("v38.0", version);
    }

    @Test
    public void testVersionCalculationFailsOnInvalid() {
        try {
            ApiVersion.resolveVersionString(ApiVersionSeason.WINTER, 17);
            Assert.fail("Error was not thrown for invalid Year");
        }
        catch (Exception e) {
        }
    }

    @Test
    public void testVersionCalculationVersionOne() {
        String version = ApiVersion.resolveVersionString(ApiVersionSeason.SUMMER, 2004);

        assertEquals("v1.0", version);

        try {
            ApiVersion.resolveVersionString(ApiVersionSeason.WINTER, 2004);
            Assert.fail("Error was not thrown for input that would result in a version < v1.0");
        }
        catch (Exception e) {
        }
    }
}
