package com.infosys.hubservices.autocpmplete.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "V_UserProfile", schema = "public")
public class UserProfile {

	@Id
	@Column(name = "ID")
	@NotNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/*@Column(name = "@type")
	private String type;*/

	@Column(name = "osid")
	private String osid;
	
	@Column(name = "photo")
	private String photo;
	
	@Column(name = "personalDetailsosid")
	private String personalDetailsosid;
	
	@Column(name = "academics_arr_osid")
	private String academicsArrOsid;
	
	@Column(name = "employmentDetails_osid")
	private String employmentDetailsOsid;
	
	@Column(name = "professionalDetails_arr_osid")
	private String professionalDetailsArrOsid;
	
	@Column(name = "skills_osid")
	private String skillsOosid;
	
	@Column(name = "interests_osid")
	private String interestsOosid;
	
	@Column(name = "userId")
	private String userId;
	
	/*@Column(name = "id", insertable=false, updatable=false)
	private String id_1;
	
	@Column(name = "@id")
	private String id_2;*/
	
	@Column(name = "osCreatedAt")
	private String createdAt;
	
	@Column(name = "osUpdatedAt")
	private String updatedAt;
	
	@Column(name = "osCreatedBy")
	private String createdBy;
	
	@Column(name = "osUpdatedBy")
	private String updatedBy;
	
	@Column(name = "competencies_arr_osid")
	private String competenciesArrOsid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPersonalDetailsosid() {
		return personalDetailsosid;
	}

	public void setPersonalDetailsosid(String personalDetailsosid) {
		this.personalDetailsosid = personalDetailsosid;
	}

	public String getAcademicsArrOsid() {
		return academicsArrOsid;
	}

	public void setAcademicsArrOsid(String academicsArrOsid) {
		this.academicsArrOsid = academicsArrOsid;
	}

	public String getEmploymentDetailsOsid() {
		return employmentDetailsOsid;
	}

	public void setEmploymentDetailsOsid(String employmentDetailsOsid) {
		this.employmentDetailsOsid = employmentDetailsOsid;
	}

	public String getProfessionalDetailsArrOsid() {
		return professionalDetailsArrOsid;
	}

	public void setProfessionalDetailsArrOsid(String professionalDetailsArrOsid) {
		this.professionalDetailsArrOsid = professionalDetailsArrOsid;
	}

	public String getSkillsOosid() {
		return skillsOosid;
	}

	public void setSkillsOosid(String skillsOosid) {
		this.skillsOosid = skillsOosid;
	}

	public String getInterestsOosid() {
		return interestsOosid;
	}

	public void setInterestsOosid(String interestsOosid) {
		this.interestsOosid = interestsOosid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/*public String getId_1() {
		return id_1;
	}

	public void setId_1(String id_1) {
		this.id_1 = id_1;
	}

	public String getId_2() {
		return id_2;
	}

	public void setId_2(String id_2) {
		this.id_2 = id_2;
	}*/

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

	public String getCompetenciesArrOsid() {
		return competenciesArrOsid;
	}

	public void setCompetenciesArrOsid(String competenciesArrOsid) {
		this.competenciesArrOsid = competenciesArrOsid;
	}

	@Override
	public String toString() {
		return "UserProfile [id=" + id + ", osid=" + osid + ", photo=" + photo + ", personalDetailsosid="
				+ personalDetailsosid + ", academicsArrOsid=" + academicsArrOsid + ", employmentDetailsOsid="
				+ employmentDetailsOsid + ", professionalDetailsArrOsid=" + professionalDetailsArrOsid
				+ ", skillsOosid=" + skillsOosid + ", interestsOosid=" + interestsOosid + ", userId=" + userId
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", createdBy=" + createdBy + ", updatedBy="
				+ updatedBy + ", competenciesArrOsid=" + competenciesArrOsid + "]";
	}

	
}
