package com.force.api;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ResourceRepresentationTest {

    ForceApi api;
    String recordId;

    @Before
    public void init() {
        api = new ForceApi(new ApiConfig()
            .setUsername(Fixture.get("username"))
            .setPassword(Fixture.get("password")));
        recordId = api.query("SELECT id FROM Account",Account.class).getRecords().get(0).getId();
    }

    @Test
    public void testRawJson() {
        String jsonString = api.getSObject("account", recordId).asJsonString();
        // Pretty lame smoke test to see if we got some json back.
        assertEquals(jsonString.charAt(0),'{');
    }
}
