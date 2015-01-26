package com.force.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

public class QueryTest {

	ForceApi api;
	
	@Before
	public void init() {
		api = new ForceApi(new ApiConfig()
		.setUsername(Fixture.get("username"))
		.setPassword(Fixture.get("password"))
		.setLoginEndpoint(Fixture.get("loginEndpoint")));
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
		List<Account> result = api.query("SELECT name FROM Account",Account.class).getRecords();
		// Note, attribute names are capitalized by the Force.com REST API
		assertNotNull(result.get(0).getName());
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
		List<Contact> existingContacts = api.query(String.format("SELECT Name FROM Contact WHERE AccountId='%s'",a.id),Contact.class).getRecords();
        Contact ct = new Contact("force@test.com","FirstName","LastName");
		ct.setAccountId(a.id);
        ct.setId(api.createSObject("Contact", ct));
        assertNotNull(ct.getId());
		List<Account> childResult = api.query(String.format("SELECT Name, (SELECT Id, AccountId, Email, FirstName, LastName FROM Contacts) FROM Account WHERE Id='%s'",a.id),
										 Account.class).getRecords();
		// Note, attribute names are capitalized by the Force.com REST API
        assertEquals(existingContacts.size()+1, childResult.get(0).contacts.size());

        Contact result = null;
        for(Contact c : childResult.get(0).contacts){
        	if(c.getId().equals(ct.getId())){
        		result = c;
        	}
        }
        assertNotNull(result);
        assertEquals("force@test.com", result.getEmail());
        assertEquals("FirstName", result.getFirstName());
        assertEquals("LastName", result.getLastName());
        assertEquals(a.id, result.getAccountId());

        List<Contact> parentResult = api.query(String.format("SELECT AccountId, Account.Id, Account.Name FROM Contact WHERE Id='%s'",ct.getId()), Contact.class).getRecords();
        assertEquals(1, parentResult.size());
        assertEquals(a.getId(), parentResult.get(0).getAccountId());
        assertEquals(a.getId(), parentResult.get(0).getAccount().getId());
        assertEquals(a.getName(), parentResult.get(0).getAccount().getName());
	}
}
