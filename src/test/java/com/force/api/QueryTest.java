package com.force.api;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class QueryTest {

    static final String TEST_NAME = "force-rest-api query test";

    ForceApi api;
	
	@Before
	public void init() {
		api = new ForceApi(new ApiConfig()
		.setUsername(Fixture.get("username"))
		.setPassword(Fixture.get("password")));
	}

	@Test
    public void testQueryTimeout() {
        try {
            new ForceApi(new ApiConfig()
                    .setUsername(Fixture.get("username"))
                    .setPassword(Fixture.get("password"))
                    .setRequestTimeout(1) // 1ms timeout
            ).query("SELECT name FROM Account").getRecords();
            fail("SocketTimeoutException was not thrown but was expected to be.");
        } catch (Exception e) {
            assertEquals(RuntimeException.class, e.getClass()); // RuntimeException wraps timeout exception
            assertTrue(e.getMessage().contains("java.net.SocketTimeoutException"));
        }
    }
	
	@Test
	public void testUntypedQuery() {
		@SuppressWarnings("rawtypes")
		List<Map> result = api.query("SELECT name FROM Account").getRecords();
		// Note, attribute names are capitalized by the Force.com REST API
		assertNotNull(result.get(0).get("Name"));
	}

	@Test
	public void testTypedQuery() {
		List<Account> result = api.query("SELECT name, createddate FROM Account",Account.class).getRecords();
		// Note, attribute names are capitalized by the Force.com REST API
		assertNotNull(result.get(0).getName());
		assertNotNull(result.get(0).getCreatedDate());
	}

    @Test
    public void testQueryMore() throws Exception {
        final int queryBatchSize = 2000;
        final int exceedQueryBatchSize = 2001;

        // make sure we have enough accounts before testing queries.
        // this does not tear down because this is an expensive operations tests should be run against test org.
        final int numAccts = api.query("SELECT count() FROM Account", Account.class).getTotalSize();
        if (numAccts < exceedQueryBatchSize) {
            int accountsNeeded = exceedQueryBatchSize - numAccts;
            System.out.println("Adding "+accountsNeeded+" test accounts to org. This will take a bit of time but is a one time operation");
            for (int i = 0; i < accountsNeeded; i++) {
                api.createSObject("Account", Collections.singletonMap("Name", "TEST-ACCOUNT-" + i));
            }
        }

        final QueryResult<Account> iniResult = api.query("SELECT name FROM Account LIMIT " + exceedQueryBatchSize, Account.class);
        assertEquals(queryBatchSize, iniResult.getRecords().size());
        assertEquals(exceedQueryBatchSize, iniResult.getTotalSize());
        assertFalse(iniResult.isDone());
        assertNotNull(iniResult.getNextRecordsUrl());

        final QueryResult<Map> moreResult = api.queryMore(iniResult.getNextRecordsUrl());
        assertEquals(exceedQueryBatchSize - queryBatchSize, moreResult.getRecords().size());
        assertEquals(exceedQueryBatchSize, moreResult.getTotalSize());
        assertTrue(moreResult.isDone());
        assertNull(moreResult.getNextRecordsUrl());
    }

    @Test
	public void testRelationshipQuery() throws JsonGenerationException, JsonMappingException, IOException {
		Account a = new Account();
		a.setName("force-rest-api-test-account");
		String id = api.createSObject("account", a);
		a.setId(id);
		Contact ct = new Contact("force@test.com","FirstName","LastName");
		ct.setAccountId(a.id);
        ct.setId(api.createSObject("Contact", ct));
		List<Account> childResult = api.query(String.format("SELECT Name, (SELECT AccountId, Email, FirstName, LastName FROM Contacts) FROM Account WHERE Id='%s'",a.id),
										 Account.class).getRecords();
		// Note, attribute names are capitalized by the Force.com REST API
        assertEquals(1, childResult.get(0).contacts.size());
        assertEquals("force@test.com", childResult.get(0).contacts.get(0).getEmail());
        assertEquals("FirstName", childResult.get(0).contacts.get(0).getFirstName());
        assertEquals("LastName", childResult.get(0).contacts.get(0).getLastName());
        assertEquals(a.id, childResult.get(0).contacts.get(0).getAccountId());

        List<Contact> parentResult = api.query(String.format("SELECT AccountId, Account.Id, Account.Name FROM Contact WHERE Id='%s'",ct.getId()), Contact.class).getRecords();
        assertEquals(1, parentResult.size());
        assertEquals(a.getId(), parentResult.get(0).getAccountId());
        assertEquals(a.getId(), parentResult.get(0).getAccount().getId());
        assertEquals(a.getName(), parentResult.get(0).getAccount().getName());
	}

    @Test
    public void testQueryAll() {
        Account a = new Account();
        a.setName(TEST_NAME);
        a.setExternalId("1234");
        String id = api.createSObject("account", a);
        assertNotNull(id);

        api.deleteSObject("Account",id);

        List<Account> result = api.queryAll("SELECT name FROM Account WHERE id = '"+id+"'",Account.class).getRecords();
        // Note, attribute names are capitalized by the Force.com REST API
        assertNotNull(result.get(0).getName());
    }
}
