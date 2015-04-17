package com.force.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Date;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Identity {
	
	String id;
	boolean assertedUser;
	String userId;
	String organizationId;
	String username;
	String nickName;
	String displayName;
	String email;
	String firstName;
	String lastName;
	Status status;
	Map<String, String> photos;
	Map<String, String> urls;
	boolean isActive;
	String userType;
	String language;
	String locale;
	long utcOffset;
	Date lastModifiedDate;
	
	public class Status {
		
		Date createdDate;
		String body;
		public Date getCreatedDate() {
			return createdDate;
		}
		public String getBody() {
			return body;
		}

		@JsonSetter(value="created_date")
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

	@JsonSetter(value="asserted_user")
	public void setAssertedUser(boolean assertedUser) {
		this.assertedUser = assertedUser;
	}

	@JsonSetter(value="user_id")
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JsonSetter(value="organization_id")
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonSetter(value="nick_name")
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@JsonSetter(value="display_name")
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonSetter(value="first_name")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@JsonSetter(value="last_name")
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

	@JsonSetter(value="user_type")
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

	@JsonSetter(value="last_modified_date")
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	
}
