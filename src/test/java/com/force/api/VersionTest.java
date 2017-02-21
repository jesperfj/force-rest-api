package com.force.api;

import org.junit.Test;

import java.util.List;

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
    public void testDiscoverListOfVersions() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"))
                .setApiVersionString("v38.0");

        ForceApi api = new ForceApi(c);

        List<VersionRepresentation> versions =
                api.discoverAvailableVersions();

        assertNotNull(versions);

        for (VersionRepresentation version : versions) {
            System.out.println(version.getVersion());
        }
    }

    @Test
    public void testDiscoverOfLatestAndOldestVersions() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"))
                .setApiVersionString("v38.0");

        ForceApi api = new ForceApi(c);

        VersionRepresentation oldest = api.getOldestVersion();
        assertNotNull(oldest);
        System.out.println(oldest.getVersion());

        VersionRepresentation latest = api.getLatestVersion();
        assertNotNull(latest);
        System.out.println(latest.getVersion());
    }

    @Test
    public void testDefaultVersionIsSupported() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"));

        ForceApi api = new ForceApi(c);

        assertTrue(api.versionIsSupportedInOrg(c.getApiVersionString()));
    }

    @Test
    public void testFakeVersionIsUnsupported() {
        ApiConfig c = new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"));

        ForceApi api = new ForceApi(c);

        assertFalse(api.versionIsSupportedInOrg("v1324.0"));
    }
}
