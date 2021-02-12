package com.infosys.hubservices.autocpmplete.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "V_personalDetails", schema = "public")
public class PersonalDetails {

	@Id
	@Column(name = "ID")
	@NotNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "osid")
	private String osid;
	
	/*@Column(name = "@type")
	private String type;*/
	
	@Column(name = "firstname")
	private String firstname;
	
	@Column(name = "middlename")
	private String middlename;
	
	@Column(name = "surname")
	private String surname;
	
	@Column(name = "dob")
	private String dob;
	
	@Column(name = "nationality")
	private String nationality;
	
	@Column(name = "domicileMedium")
	private String domicileMedium;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "maritalStatus")
	private String maritalStatus;
	
	@Column(name = "category")
	private String category;
	
	@Column(name = "knownLanguages")
	private String knownLanguages;
	
	@Column(name = "countryCode")
	private String countryCode;
	
	@Column(name = "mobile")
	private Integer mobile;
	
	@Column(name = "telephone")
	private String telephone;
	
	@Column(name = "primaryEmail")
	private String primaryEmail;
	
	@Column(name = "officialEmail")
	private String officialEmail;
	
	@Column(name = "personalEmail")
	private String personalEmail;
	
	@Column(name = "postalAddress")
	private String postalAddress;
	
	@Column(name = "pincode")
	private String pincode;
	
	@Column(name = "osCreatedAt")
	private String createdAt;
	
	@Column(name = "osUpdatedAt")
	private String updatedAt;
	
	@Column(name = "osCreatedBy")
	private String createdBy;
	
	@Column(name = "osUpdatedBy")
	private String updatedBy;
	
	@Column(name = "_osroot")
	private String osroot;
	
	@Column(name = "username")
	private String username;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getDomicileMedium() {
		return domicileMedium;
	}

	public void setDomicileMedium(String domicileMedium) {
		this.domicileMedium = domicileMedium;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getKnownLanguages() {
		return knownLanguages;
	}

	public void setKnownLanguages(String knownLanguages) {
		this.knownLanguages = knownLanguages;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getMobile() {
		return mobile;
	}

	public void setMobile(Integer mobile) {
		this.mobile = mobile;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPrimaryEmail() {
		return primaryEmail;
	}

	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}

	public String getOfficialEmail() {
		return officialEmail;
	}

	public void setOfficialEmail(String officialEmail) {
		this.officialEmail = officialEmail;
	}

	public String getPersonalEmail() {
		return personalEmail;
	}

	public void setPersonalEmail(String personalEmail) {
		this.personalEmail = personalEmail;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getOsroot() {
		return osroot;
	}

	public void setOsroot(String osroot) {
		this.osroot = osroot;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	/*public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}*/

	
	public String getOsid() {
		return osid;
	}

	public void setOsid(String osid) {
		this.osid = osid;
	}

	@Override
	public String toString() {
		return "PersonalDetails [id=" + id + ", osid=" + osid + ", firstname=" + firstname + ", middlename="
				+ middlename + ", surname=" + surname + ", dob=" + dob + ", nationality=" + nationality
				+ ", domicileMedium=" + domicileMedium + ", gender=" + gender + ", maritalStatus=" + maritalStatus
				+ ", category=" + category + ", knownLanguages=" + knownLanguages + ", countryCode=" + countryCode
				+ ", mobile=" + mobile + ", telephone=" + telephone + ", primaryEmail=" + primaryEmail
				+ ", officialEmail=" + officialEmail + ", personalEmail=" + personalEmail + ", postalAddress="
				+ postalAddress + ", pincode=" + pincode + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", osroot=" + osroot + ", username="
				+ username + ", getId()=" + getId() + ", getFirstname()=" + getFirstname() + ", getMiddlename()="
				+ getMiddlename() + ", getSurname()=" + getSurname() + ", getDob()=" + getDob() + ", getNationality()="
				+ getNationality() + ", getDomicileMedium()=" + getDomicileMedium() + ", getGender()=" + getGender()
				+ ", getMaritalStatus()=" + getMaritalStatus() + ", getCategory()=" + getCategory()
				+ ", getKnownLanguages()=" + getKnownLanguages() + ", getCountryCode()=" + getCountryCode()
				+ ", getMobile()=" + getMobile() + ", getTelephone()=" + getTelephone() + ", getPrimaryEmail()="
				+ getPrimaryEmail() + ", getOfficialEmail()=" + getOfficialEmail() + ", getPersonalEmail()="
				+ getPersonalEmail() + ", getPostalAddress()=" + getPostalAddress() + ", getPincode()=" + getPincode()
				+ ", getCreatedAt()=" + getCreatedAt() + ", getUpdatedAt()=" + getUpdatedAt() + ", getCreatedBy()="
				+ getCreatedBy() + ", getUpdatedBy()=" + getUpdatedBy() + ", getOsroot()=" + getOsroot()
				+ ", getUsername()=" + getUsername() + ", getOsid()=" + getOsid() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
}
