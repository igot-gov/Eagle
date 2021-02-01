package org.sunbird.common.model;

import java.util.List;

public class SunbirdApiRespContent {
	
	private String rootOrgName;
	private String channel;
	private String id;

	private String identifier;
	private String rootOrgId;

	private String firstName;
	private String dob;

	private String userType;

	private String lastName;

	private String gender;

	private List<String> roles;

	private String countryCode;

	private String email;

	private String userName;

	private List<SunbirdApiRespOragainsation> organisations;

	public String getRootOrgName() {
		return rootOrgName;
	}

	public void setRootOrgName(String rootOrgName) {
		this.rootOrgName = rootOrgName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getRootOrgId() {
		return rootOrgId;
	}

	public void setRootOrgId(String rootOrgId) {
		this.rootOrgId = rootOrgId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<SunbirdApiRespOragainsation> getOrganisations() {
		return organisations;
	}

	public void setOrganisations(List<SunbirdApiRespOragainsation> organisations) {
		this.organisations = organisations;
	}
	

}
