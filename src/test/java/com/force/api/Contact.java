package com.force.api;

import com.google.gson.annotations.SerializedName;


public class Contact {

    @SerializedName("Id")
    private String id;

    @SerializedName("Email")
	private String email;

    @SerializedName("FirstName")
	private String firstName;

    @SerializedName("LastName")
	private String lastName;

    @SerializedName("AccountId")
	private String accountId;

    @SerializedName("Account")
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
