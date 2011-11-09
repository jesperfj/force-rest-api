package com.force.api;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class DataApiOperationsTest {

	private static DataApi api;
	private static Properties fixture;
	
	@BeforeClass
	public static void beforeClass() {
		fixture = new Properties();
			try {
				fixture.load(DataApiOperationsTest.class
				        .getResourceAsStream("/test.properties"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		System.out.println(fixture.getProperty("username"));
		System.out.println(fixture.getProperty("password"));
		System.out.println(fixture.getProperty("clientId"));
		System.out.println(fixture.getProperty("clientSecret"));
		api = new DataApi(new ApiConfig()
			.setUsername(fixture.getProperty("username"))
			.setPassword(fixture.getProperty("password"))
			.setClientId(fixture.getProperty("clientId"))
			.setClientSecret(fixture.getProperty("clientSecret")));
	}

	@Test
	public void testGetSObjectTyped() {

		Account a = api.get(new SObjectResource()
						.setId(fixture.getProperty("accountId"))
						.setType("Account")).as(Account.class);

		assertEquals(a.getId(),fixture.getProperty("accountId"));
		
	}

	@Test
	public void testGetSObjectUntyped() {
		
		Map<?, ?> sobj = api.get(new SObjectResource()
										.setId(fixture.getProperty("accountId"))
										.setType("Account")).asMap();
		for(Object key : sobj.keySet()) {
			System.out.println(key+": "+sobj.get(key));
		}
		assertEquals(fixture.getProperty("accountId"),sobj.get("Id"));

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
