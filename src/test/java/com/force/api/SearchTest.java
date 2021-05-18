package com.force.api;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SearchTest {

    static final String TEST_NAME = "force-rest-api search test";

    ForceApi api;

    @Before
    public void init() {
        api = new ForceApi(new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password")));
    }

    @Test
    public void testSearchTimeout() {
        try {
            new ForceApi(new ApiConfig()
                    .setUsername(Fixture.get("username"))
                    .setPassword(Fixture.get("password"))
                    .setRequestTimeout(1) // 1ms timeout
            ).searchBySOSL("FIND {name} IN ALL TYPE RETURNING Account").getSearchRecords();
            fail("SocketTimeoutException was not thrown but was expected to be.");
        } catch (Exception e) {
            assertEquals(RuntimeException.class, e.getClass()); // RuntimeException wraps timeout exception
            assertTrue(e.getMessage().contains("java.net.SocketTimeoutException"));
        }
    }

    @Test
    public void testUntypedSearch() {
        @SuppressWarnings("rawtypes")
        List<Map> result = api.searchBySOSL("FIND {name} IN ALL TYPE RETURNING Account(name)").getSearchRecords();
        // Note, attribute names are capitalized by the Force.com REST API
        assertNotNull(result.get(0).get("Name"));
    }

    @Test
    public void testTypedSearch() {
        List<Account> result = api.searchBySOSL("FIND {name} IN ALL TYPE RETURNING Account(Name, CreatedDate)", Account.class).getSearchRecords();
        // Note, attribute names are capitalized by the Force.com REST API
        assertNotNull(result.get(0).getName());
        assertNotNull(result.get(0).getCreatedDate());
    }
}
