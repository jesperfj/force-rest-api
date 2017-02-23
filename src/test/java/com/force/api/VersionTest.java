package com.force.api;

import org.junit.Test;

import javax.swing.text.TableView;

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
    public void testListOfVersions() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"))
                .setApiVersionString("v38.0");

        ForceApi api = new ForceApi(c);

        SupportedVersions versions = api.getSupportedVersions();
        assertNotNull(versions);

        for (ExtendedApiVersion version : versions) System.out.println(version.getVersionString()+" "+version.getLabel());
    }

    @Test
    public void testLatestAndOldestVersions() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"))
                .setApiVersionString("v38.0");

        ForceApi api = new ForceApi(c);

        ExtendedApiVersion oldest = api.getSupportedVersions().oldest();
        assertNotNull(oldest);
        System.out.println(oldest.getVersion());

        ExtendedApiVersion latest = api.getSupportedVersions().latest();
        assertNotNull(latest);
        System.out.println(latest.getVersion());
    }

    @Test
    public void testDefaultVersionIsSupported() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"));

        ForceApi api = new ForceApi(c);

        assertTrue(api.getSupportedVersions().contains(c.getApiVersionString()));
    }

    @Test
    public void testFakeVersionIsUnsupported() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"));

        ForceApi api = new ForceApi(c);

        assertFalse(api.getSupportedVersions().contains("v1324.0"));
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
}
