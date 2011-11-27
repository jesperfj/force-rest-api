package com.force.api;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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
	public void testCreateSObjectUntyped() {
		
	}
	
	@Test
	@Ignore
	public void testUpdateSObjectTyped() {
		
	}
	
	@Test
	@Ignore
	public void testUpdateSObjectUntyped() {
		
	}
	
	@Test
	@Ignore
	public void testUpsertSObjectTyped() {
		
	}
	
	@Test
	@Ignore
	public void testUpsertSObjectUntyped() {
		
	}
	
	@Test
	@Ignore
	public void testDeleteSObject() {
		
	}
	
	@Test
	public void testQueryUntyped() {
		
		Map<?,?> result = api.get(new QueryResource()
						         .setQuery("SELECT id FROM Account")).asMap();
		for(Object o : result.keySet()) {
			System.out.println(o+": "+result.get(o));
		}
		
	}
	
	@Test
	@Ignore
	public void testQueryTyped() {
		QueryResult<Account> result = api.query("SELECT id FROM Account",Account.class);
		System.out.println(result.totalSize);
		System.out.println(result.getRecords().get(0).getName());
	}

	@Test
	public void testTest() {
		ObjectMapper jsonMapper = new ObjectMapper();
		try {
			jsonMapper.readValue(Http.send(new HttpRequest()
			.url(api.session.getApiEndpoint()+"/services/data/"+api.config.getApiVersion()+"/query/?q="+URLEncoder.encode("SELECT id FROM Account","UTF-8"))
			.method("GET")
			.header("Accept", "application/json")
			.header("Authorization", "OAuth "+api.session.getAccessToken())).getStream(),new TypeReference<QueryResult<Account>>() {});
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	@Test
	@Ignore
	public void testSearch() {
		
	}
	
	
}
