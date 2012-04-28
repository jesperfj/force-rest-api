package com.force.api;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class QueryTest {
	static final String TEST_NAME = "force-rest-api-test-account";

	static ForceApi api;
	
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
		a.setName(TEST_NAME);
		String id = api.createSObject("account", a);
		a.setId(id);
		Contact ct = new Contact("force@test.com","FirstName","LastName");
		ct.setAccountId(a.id);
		api.createSObject("Contact", ct);
		List<Account> result = api.query(String.format("SELECT name,(select Id,AccountId,FirstName,LastName,Email from Contacts) FROM Account where Id='%s'",a.id),
										 Account.class).getRecords();
		// Note, attribute names are capitalized by the Force.com REST API
		assertEquals(result.get(0).contacts.size(),1);
		assertEquals(result.get(0).contacts.get(0).getEmail(),"force@test.com");
		assertEquals(result.get(0).contacts.get(0).getFirstName(),"FirstName");
		assertEquals(result.get(0).contacts.get(0).getLastName(),"LastName");
		assertEquals(result.get(0).contacts.get(0).getAccountId(),a.id);
		
	}
	
	
	@Test
	public void testChild2LevelParentQuery() throws JsonGenerationException, JsonMappingException, IOException {
		Account a = new Account();
		a.setName(TEST_NAME);
		String id = api.createSObject("account", a);
		a.setId(id);
		Contact ct = new Contact("force@test.com","FirstName","LastName");
		ct.setAccountId(a.id);
		
		String ctId = api.createSObject("Contact", ct);
		ct.setId(ctId);

		Address address = new Address();
		address.setContactId(ct.id);
		
		String addressId = api.createSObject("Address__c", address);
		address.setId(addressId);

		List<Address> result = api.query(String.format("select Id,Name,Contact__r.FirstName,Contact__r.Account.Id from Address__c where Id='%s'",address.id),
				Address.class).getRecords();
		assertEquals(result.get(0).getContact().getFirstName(),"FirstName");
		assertEquals(result.get(0).getContact().getAccount().getId(),a.getId());
		
		
	}
	
	@AfterClass
	public static void deleteRecords() {
		QueryResult<Account> res = api.query("SELECT id FROM Account WHERE name LIKE '"+TEST_NAME+"%'", Account.class);
		for(Account a : res.getRecords()) {
			api.deleteSObject("Account", a.getId());
		}
	}
	

}
