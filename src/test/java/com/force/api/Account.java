package com.force.api;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Account {

	@SerializedName("Id")
	String id;
    @SerializedName("Name")
	String name;
    @SerializedName("externalId__c")
	String externalId;
    @SerializedName("AnnualRevenue")
	private Double annualRevenue;

    @SerializedName("Contacts")
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
