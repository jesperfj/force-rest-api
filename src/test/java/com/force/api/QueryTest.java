package com.force.api;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QueryTest {

	ForceApi api;
	
	@Before
	public void init() {
		api = new ForceApi(new ApiConfig()
		.setUsername(Fixture.get("username"))
		.setPassword(Fixture.get("password")));
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
	public void testRelationshipQuery() throws JsonGenerationException, JsonMappingException, IOException {
		Account a = new Account();
		a.setName("force-rest-api-test-account");
		String id = api.createSObject("account", a);
		a.setId(id);
		Contact ct = new Contact("force@test.com","FirstName","LastName");
		ct.setAccountId(a.id);
		api.createSObject("Contact", ct);
		List<Account> result = api.query(String.format("SELECT Name, (SELECT AccountId, Email, FirstName, LastName FROM Contacts) FROM Account WHERE Id='%s'",a.id),
										 Account.class).getRecords();
		// Note, attribute names are capitalized by the Force.com REST API
		assertEquals(result.get(0).contacts.size(),1);
		assertEquals(result.get(0).contacts.get(0).getEmail(),"force@test.com");
		assertEquals(result.get(0).contacts.get(0).getFirstName(),"FirstName");
		assertEquals(result.get(0).contacts.get(0).getLastName(),"LastName");
		assertEquals(result.get(0).contacts.get(0).getAccountId(),a.id);
	}
	

}
