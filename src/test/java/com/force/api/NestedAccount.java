package com.force.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NestedAccount extends NestedRecord {

    @JsonProperty(value = "Name")
    private String name;

    public NestedAccount(String refId){

        this.attribute = new Attribute("Account", refId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContacts(List<NestedContact> contacts){

        Map<String, NestedRecordRequest> innerNestedRecordsByType = getInnerNestedRecordsByType();
        if(innerNestedRecordsByType == null)
            innerNestedRecordsByType = new HashMap<String, NestedRecordRequest>();

        NestedRecordRequest innerContacts = innerNestedRecordsByType.get("Contacts");
        if(innerContacts == null)
            innerContacts = new NestedRecordRequest("Account");

        innerContacts.setRecords(contacts);
        innerNestedRecordsByType.put("Contacts", innerContacts);
        setInnerNestedRecordsByType(innerNestedRecordsByType);

    }
}
