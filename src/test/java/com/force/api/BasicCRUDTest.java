package com.force.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Smoke test covering the basics.
 * @author jjoergensen
 *
 */
public class BasicCRUDTest {

	

	@Test
	public void basicCRUDTest() {
		final String testName = "force-rest-api basic crud test";
		
		ForceApi api = new ForceApi(new ApiConfig()
			.setUsername(Fixture.get("username"))
			.setPassword(Fixture.get("password"))
			.setClientId(Fixture.get("clientId"))
			.setClientSecret(Fixture.get("clientSecret")));

		if(api.query("SELECT name FROM Account WHERE name LIKE '"+testName+"%'",Account.class).getTotalSize()>0) {
			fail("Looks like org is not clean. Manually delete account record with name '"+testName+"' before running this test");
		}
		
		try {
			
			// TEST CREATE
			
			Account a = new Account();
			a.setName(testName);
			a.setExternalId("1234");
			String id = api.createSObject("account", a);
			assertNotNull(id);
			
			// TEST GET
			
			a = api.getSObject("account", id).as(Account.class);
			assertEquals(testName,a.getName());
			assertEquals("1234", a.getExternalId());
			
			// TEST UPDATE
			
			a = new Account();
			a.setName(testName+", updated");
			api.updateSObject("account", id, a);
			
			a = api.getSObject("account", id).as(Account.class);
			assertEquals(testName+", updated",a.getName());
			
			// TEST UPSERT: UPDATE
			
			a = new Account();
			a.setName(testName);
			a.setAnnualRevenue(3141592.65);

			// test that we get expected result
			assertEquals(CreateOrUpdateResult.UPDATED, api.createOrUpdateSObject("account", "externalId__c", "1234", a));
			
			// check that the record was indeed updated
			assertTrue(3141592.65==api.getSObject("account", id).as(Account.class).getAnnualRevenue());
			
			// check that there's still only one record with this name
			assertTrue(1==api.query("SELECT id FROM Account WHERE name = '"+testName+"'",Account.class).getTotalSize());
			
			// TEST UPSERT: CREATE
			
			a = new Account();
			a.setName(testName+" new one");
			a.setAnnualRevenue(1414213.56);
			
			// test that we get expected result
			assertEquals(CreateOrUpdateResult.CREATED, api.createOrUpdateSObject("account", "externalId__c", "2345", a));
			
			// check that the record value was properly set
			assertTrue(1414213.56==api.query("SELECT annualRevenue FROM Account WHERE externalId__c = '2345'",Account.class).getRecords().get(0).getAnnualRevenue());
			
		}
		finally {
			QueryResult<Account> res = api.query("SELECT id FROM Account WHERE name LIKE '"+testName+"%'", Account.class);
			for(Account a : res.getRecords()) {
				api.deleteSObject("account", a.getId());
			}
		}
		
	}
	
	@Test
	public void basicCRUDTestIncludingNullValues() {
		final String testName = "force-rest-api basic crud test with nulls";
		
		ForceApi api = new ForceApi(new ApiConfig()
				.setUsername(Fixture.get("username"))
				.setPassword(Fixture.get("password"))
				.setClientId(Fixture.get("clientId"))
				.setClientSecret(Fixture.get("clientSecret"))
				.setIncludeNullValues(true));
		
		if (api.query("SELECT name FROM Account WHERE name LIKE '" + testName + "%'", Account.class).getTotalSize() > 0) {
			fail("Looks like org is not clean. Manually delete account record with name '" + testName + "' before running this test");
		}
		
		// TEST CREATE
		
		Account a = new Account();
		a.setName(testName);
		a.setAnnualRevenue(1000.0);
		a.setExternalId("1234");
		String id = api.createSObject("account", a);
		assertNotNull(id);
		
		try {
			// Set a value for name first
			a = api.getSObject("account", id).as(Account.class);
			assertEquals(new Double(1000.0), a.getAnnualRevenue());
			
			// set annual revenue to a null value
			Account updatedAccount = new Account();
			updatedAccount.setName(testName);
			api.updateSObject("account", id, updatedAccount);
			
			Account getAccount = api.getSObject("account", id).as(Account.class);
			assertEquals(null, getAccount.getAnnualRevenue());
			
			
		} finally {
			api.deleteSObject("account", id);
		}
		
	}
	
	
}
