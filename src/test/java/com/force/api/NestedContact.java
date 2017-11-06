package com.force.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NestedContact extends NestedRecord {

    @JsonProperty(value = "LastName")
    private String lastName;

    @JsonProperty(value = "Email")
    private String email;

    public NestedContact(String refId){

        this.attribute = new NestedRecord.Attribute("Contact", refId);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
