package com.force.api;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class GetDeletedTest {

    private final static String TEST_NAME = "Unit Test GetDeleted";

    private ForceApi api;

    @Before
    public void init() {
        api = new ForceApi(new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password")));
    }

    @Test
    public void testGetDeletedLocalTimezone() {
        // sanity check
        if (api.query("SELECT name FROM Account WHERE name LIKE '" + TEST_NAME + "%'", Account.class)
                .getTotalSize() > 0) {
            fail("Looks like org is not clean. Manually delete account record with name '" + TEST_NAME +
                    "' before running this test");
        }

        try {
            // use local timezone for testing the UTC conversion
            Calendar oneMinInThePast = Calendar.getInstance();
            oneMinInThePast.roll(Calendar.MINUTE, -1);
            Calendar fiveMinsInTheFuture = Calendar.getInstance();
            fiveMinsInTheFuture.roll(Calendar.MINUTE, 5);

            // first establish a baseline for the number of deleted objects in that timeframe
            GetDeletedSObject deleted = this.api.getDeleted("Account", oneMinInThePast.getTime(),
                    fiveMinsInTheFuture.getTime());
            int previousDeletedCount = deleted.getDeletedRecords().size();

            // create a test account
            Account a = new Account();
            a.setName(TEST_NAME);
            String id = this.api.createSObject("account", a);
            assertNotNull(id);

            // delete the test account
            this.api.deleteSObject("account", id);

            // check that we see the deletion
            deleted = this.api.getDeleted("Account", oneMinInThePast.getTime(), fiveMinsInTheFuture.getTime());
            int currentDeletedCount = deleted.getDeletedRecords().size();
            // TODO do a greater than? for now just fail if it's not exactly +1
            assertEquals("we should see (at least) one deleted SObject more", (previousDeletedCount + 1),
                    currentDeletedCount);

        } finally {
            QueryResult<Account> res = api.query("SELECT id FROM Account WHERE name LIKE '" + TEST_NAME + "%'",
                    Account.class);
            for (Account a : res.getRecords()) {
                this.api.deleteSObject("account", a.getId());
            }
        }
    }

}
