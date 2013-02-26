package com.force.api;

import java.util.Date;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Identity {
	
	String id;
    @SerializedName("asserted_user")boolean assertedUser;
    @SerializedName("user_id") String userId;
    @SerializedName("organization_id") String organizationId;
	String username;
    @SerializedName("nick_name") String nickName;
    @SerializedName("display_name") String displayName;
	String email;
    @SerializedName("first_name") String firstName;
    @SerializedName("last_name") String lastName;
	Status status;
	Map<String, String> photos;
	Map<String, String> urls;
	boolean isActive;
    @SerializedName("user_type") String userType;
	String language;
	String locale;
	long utcOffset;
    @SerializedName("last_modified_date") Date lastModifiedDate;



	
	public class Status {

        @SerializedName("created_date")
		Date createdDate;
		String body;
		public Date getCreatedDate() {
			return createdDate;
		}
		public String getBody() {
			return body;
		}

		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}
		public void setBody(String body) {
			this.body = body;
		}

	}

	public String getId() {
		return id;
	}

	public boolean isAssertedUser() {
		return assertedUser;
	}

	public String getUserId() {
		return userId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public String getUsername() {
		return username;
	}

	public String getNickName() {
		return nickName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Status getStatus() {
		return status;
	}

	public Map<String, String> getPhotos() {
		return photos;
	}

	public Map<String, String> getUrls() {
		return urls;
	}

	public boolean isActive() {
		return isActive;
	}

	public String getUserType() {
		return userType;
	}

	public String getLanguage() {
		return language;
	}

	public String getLocale() {
		return locale;
	}

	public long getUtcOffset() {
		return utcOffset;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAssertedUser(boolean assertedUser) {
		this.assertedUser = assertedUser;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setPhotos(Map<String, String> photos) {
		this.photos = photos;
	}

	public void setUrls(Map<String, String> urls) {
		this.urls = urls;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setUtcOffset(long utcOffset) {
		this.utcOffset = utcOffset;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	
}
