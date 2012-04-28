package com.force.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Address {

	@JsonProperty(value="Id")
	String id;

	@JsonProperty(value="Contact__r")
	private Contact contact;
	
	@JsonProperty(value="Contact__c")
	private String contactId;
	
	@JsonProperty(value="Street_Address__c")
	private String streetAddress;

	public Address() {
		super();
	}
	public Address(String contactId, String streetAddress) {
		this();
		this.contactId = contactId;
		this.streetAddress = streetAddress;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
}
