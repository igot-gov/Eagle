package com.infosys.lex.ext.Models.Cassandra;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user")
public class User {

	@PrimaryKey
	@Column("id")
	private String id;

	@Column("avatar")
	private String avatar;

	@Column("countrycode")
	private String countryCode;

	@Column("createdby")
	private String createdBy;

	@Column("createddate")
	private String createdDate;

	@Column("currentlogintime")
	private String currentLoginTime;

	@Column("dob")
	private String dob;

	@Column("email")
	private String email;

	@Column("emailverified")
	private boolean emailVerified;

	@Column("firstname")
	private String firstName;

	@Column("gender")
	private String gender;

	@Column("grade")
	private List<String> grade;

	@Column("isdeleted")
	private boolean isDeleted;

	@Column("language")
	private List<String> language;

	@Column("lastlogintime")
	private String lastLoginTime;

	@Column("lastname")
	private String lastName;

	@Column("location")
	private String location;

	@Column("loginid")
	private String loginId;

	@Column("password")
	private String password;

	@Column("phone")
	private String phone;

	@Column("profilesummary")
	private String profileSummary;

	@Column("profilevisibility")
	private Map<String, String> profileVisibility;

	@Column("provider")
	private String provider;

	@Column("regorgid")
	private String regOrgId;

	@Column("roles")
	private List<String> roles;

	@Column("rootorgid")
	private String rootOrgId;

	@Column("status")
	private int status;

	@Column("subject")
	private List<String> subject;

	@Column("tcstatus")
	private String tcStatus;

	@Column("tcupdateddate")
	private String tcUpdatedDate;

	@Column("temppassword")
	private String tempPassword;

	@Column("thumbnail")
	private String thumbnail;

	@Column("updatedby")
	private String updatedBy;

	@Column("updateddate")
	private String updatedDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCurrentLoginTime() {
		return currentLoginTime;
	}

	public void setCurrentLoginTime(String currentLoginTime) {
		this.currentLoginTime = currentLoginTime;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<String> getGrade() {
		return grade;
	}

	public void setGrade(List<String> grade) {
		this.grade = grade;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public List<String> getLanguage() {
		return language;
	}

	public void setLanguage(List<String> language) {
		this.language = language;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProfileSummary() {
		return profileSummary;
	}

	public void setProfileSummary(String profileSummary) {
		this.profileSummary = profileSummary;
	}

	public Map<String, String> getProfileVisibility() {
		return profileVisibility;
	}

	public void setProfileVisibility(Map<String, String> profileVisibility) {
		this.profileVisibility = profileVisibility;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getRegOrgId() {
		return regOrgId;
	}

	public void setRegOrgId(String regOrgId) {
		this.regOrgId = regOrgId;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getRootOrgId() {
		return rootOrgId;
	}

	public void setRootOrgId(String rootOrgId) {
		this.rootOrgId = rootOrgId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<String> getSubject() {
		return subject;
	}

	public void setSubject(List<String> subject) {
		this.subject = subject;
	}

	public String getTcStatus() {
		return tcStatus;
	}

	public void setTcStatus(String tcStatus) {
		this.tcStatus = tcStatus;
	}

	public String getTcUpdatedDate() {
		return tcUpdatedDate;
	}

	public void setTcUpdatedDate(String tcUpdatedDate) {
		this.tcUpdatedDate = tcUpdatedDate;
	}

	public String getTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

}
