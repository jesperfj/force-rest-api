package com.force.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)

public class Contact {

    @JsonProperty(value="Id")
    private String id;
    
	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("FirstName")
	private String firstName;

	@JsonProperty("LastName")
	private String lastName;

	@JsonProperty("AccountId")
	private String accountId;

    @JsonProperty("Account")
    private Account account;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	public String getAccountId() {
		return this.accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public Contact(){
		super();
	}

	public Contact(String email, String firstName,String lastName) {
		this();
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
}
