package com.force.api;

import org.junit.Test;

import static org.junit.Assert.*;

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
    public void testSupportedVersions() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"));

        ForceApi api = new ForceApi(c);

        SupportedVersions versions = api.getSupportedVersions();
        System.out.println("Default version: "+ApiVersion.DEFAULT_VERSION.toString());
        assertNotNull(versions);
        assertTrue(versions.contains(ApiVersion.DEFAULT_VERSION.toString()));
        assertNotNull(versions.oldest());
        System.out.println("Oldest supported version: " + versions.oldest());
        assertNotNull(versions.latest());
        System.out.println("Latest supported version: " + versions.latest());

        // Test that it correctly returns false on unknown version
        assertFalse(versions.contains("v132423.0"));
    }

    @Test
    public void testSeasonLogic() {
        ExtendedApiVersion v = new ExtendedApiVersion(ExtendedApiVersion.Season.SPRING, 2017);
        System.out.println(ExtendedApiVersion.Season.SPRING.ordinal());
        System.out.println(v.getVersionString());
        assertEquals(v.getVersionString(), "v39.0");

        v = new ExtendedApiVersion(ExtendedApiVersion.Season.WINTER, 2011);
        assertEquals(v.getVersionString(), "v20.0");


    }

    @Test
    public void testTooOldSeason() {
        try {
            ExtendedApiVersion v = new ExtendedApiVersion(ExtendedApiVersion.Season.SPRING, 2010);
            fail("Exception expected when using invalid API version");
        } catch(Throwable t) {
            System.out.println("Correctly failed: "+t);
        }
    }

    @Test
    public void testCompareLogic() {
        ExtendedApiVersion version = new ExtendedApiVersion("v25.0");
        ExtendedApiVersion later = new ExtendedApiVersion("v27.0");
        ExtendedApiVersion earlier = new ExtendedApiVersion("v22.0");
        ExtendedApiVersion same = new ExtendedApiVersion("v25.0");

        assertTrue(version.compareTo(later) == -1);
        assertTrue(version.compareTo(earlier) == 1);
        assertTrue(version.compareTo(same) == 0);
    }
}
