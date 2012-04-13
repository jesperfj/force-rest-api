package com.force.api;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Account {

	@JsonProperty(value="Id")
	String id;
	@JsonProperty(value="Name")
	String name;
	@JsonProperty(value="externalId__c")
	String externalId;	
	@JsonProperty(value="AnnualRevenue")
	private Double annualRevenue;
	
	@JsonProperty("Contacts")
	List<Contact> contacts;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Double getAnnualRevenue() {
		return annualRevenue;
	}
	public void setAnnualRevenue(Double value) {
		annualRevenue = value;
	}

	
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

}
