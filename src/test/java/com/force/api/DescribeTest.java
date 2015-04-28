package com.force.api;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class DescribeTest {

	static ForceApi api = new ForceApi(new ApiConfig()
		.setUsername(Fixture.get("username"))
		.setPassword(Fixture.get("password"))
		.setClientId(Fixture.get("clientId"))
		.setClientSecret(Fixture.get("clientSecret")));

	
	@Test
	public void testDescribeGlobal() {
		DescribeGlobal dg = api.describeGlobal();
        assertEquals(200, dg.getMaxBatchSize());
        assertEquals("UTF-8", dg.getEncoding());
		assertNotNull(dg.getSObjects().get(0));
	}


	@Test
	public void testDescribeSObject() {
		DescribeSObject ds = api.describeSObject("Contact");
		assertEquals("Contact", ds.getName());
		assertNotNull(ds.getAllFields());
		assertNotNull(ds.getAllFields().iterator().next().getSoapType());
	}

    @Test
    public void testDiscoverSObject() throws Exception {
        DiscoverSObject<Contact> ds = api.discoverSObject("Contact", Contact.class);
        assertEquals("Contact", ds.getObjectDescribe().getName());
        assertNotNull(ds.getRecentItems());
        assertTrue(ds.getRecentItems().iterator().next().getId().startsWith("003"));
		for (Contact recentContact: ds.getRecentItems()){
			assertNotNull(recentContact.getName());
		}
    }

    @Test
	public void testGenerateJava() {
		DescribeSObject ds = api.describeSObject("Contact");
		PojoCodeGenerator gen = new PojoCodeGenerator();
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		try {
			gen.generateCode(s, ds, new ApiConfig().getApiVersionString(),null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
