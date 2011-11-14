package com.force.api;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class DataApiOperationsTest {

	private static DataApi api;
	
	@BeforeClass
	public static void beforeClass() {
		api = new DataApi(new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"))
			.setClientId(Fixture.get("clientId"))
			.setClientSecret(Fixture.get("clientSecret")));
	}

	@Test
	public void testGetSObjectTyped() {

		Account a = api.get(new SObjectResource()
						.setId(Fixture.get("accountId"))
						.setType("Account")).as(Account.class);

		assertEquals(a.getId(),Fixture.get("accountId"));
		
	}

	@Test
	public void testGetSObjectUntyped() {
		
		Map<?, ?> sobj = api.get(new SObjectResource()
										.setId(Fixture.get("accountId"))
										.setType("Account")).asMap();
		for(Object key : sobj.keySet()) {
			System.out.println(key+": "+sobj.get(key));
		}
		assertEquals(Fixture.get("accountId"),sobj.get("Id"));

	}

	@Test
	@Ignore
	public void testCreateSObjectTyped() {
		
	}
	
	@Test
	@Ignore
	public void testCreateSObjectUNtyped() {
		
	}
	
	@Test
	@Ignore
	public void testUpdateSObjectTyped() {
		
	}
	
	@Test
	@Ignore
	public void testUpdateSObjectUNntyped() {
		
	}
	
	@Test
	@Ignore
	public void testUpsertSObjectTyped() {
		
	}
	
	@Test
	@Ignore
	public void testUpsertSObjectUNtyped() {
		
	}
	
	@Test
	@Ignore
	public void testDeleteSObject() {
		
	}
	
	@Test
	public void testQuery() {
		
		Map<?,?> result = api.get(new QueryResource()
						         .setQuery("SELECT id FROM Account")).asMap();
		for(Object o : result.keySet()) {
			System.out.println(o+": "+result.get(o));
		}
		
	}

	@Test
	@Ignore
	public void testSearch() {
		
	}
	
	
}
