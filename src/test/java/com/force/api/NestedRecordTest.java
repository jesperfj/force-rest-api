package com.force.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class NestedRecordTest {

    static ForceApi api = new ForceApi(new ApiConfig()
            .setUsername(Fixture.get("username"))
            .setPassword(Fixture.get("password"))
            .setClientId(Fixture.get("clientId"))
            .setClientSecret(Fixture.get("clientSecret")));

    @Test
    public void testInsert(){

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        NestedAccount acc = new NestedAccount("acc2");
        acc.setName("Acc 3");

        NestedContact con1 = new NestedContact("con1");
        con1.setLastName("Con1");
        con1.setEmail("con1@nestedrecord.com");

        NestedContact con2 = new NestedContact("con2");
        con2.setLastName("Con2");
        con2.setEmail("con2@nestedrecord.com");

        List<NestedContact> contacts = new ArrayList<NestedContact>();
        contacts.add(con1);
        contacts.add(con2);

        acc.setContacts(contacts);

        NestedContact con3 = new NestedContact("con3");
        con3.setLastName("Con3");
        con3.setEmail("con3@nestedrecord.com");

        NestedRecordRequest<NestedRecord> nestedRequest = new NestedRecordRequest<NestedRecord>("Account");
        nestedRequest.getRecords().add(acc);

        try {
            System.out.println( mapper.writeValueAsString(nestedRequest) );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        NestedResponse response = api.insertNestedObjects(nestedRequest);
        assertTrue(response != null);
    }

}
