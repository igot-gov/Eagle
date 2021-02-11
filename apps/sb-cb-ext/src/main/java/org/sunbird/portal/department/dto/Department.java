package org.sunbird.portal.department.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.sunbird.portal.department.model.DepartmentInfo;
import org.sunbird.portal.department.model.DeptPublicInfo;

@Entity
@Table(name = "departments", schema = "wingspan")
public class Department {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "root_org")
	@NotNull
	private String rootOrg;

	@Column(name = "dept_name")
	@NotNull
	private String deptName;

	@Column(name = "dept_type_ids")
	@NotNull
	@Type(type = "org.sunbird.portal.department.dto.GenericArrayUserType")
	private Integer[] deptTypeId;

	@Column(name = "description")
	@NotNull
	private String description;

	@Column(name = "headquarters")
	private String headquarters;

	@Column(name = "logo")
	private byte[] logo;
	
	@Column(name="creation_date")
	private Long creationDate;
	
	@Column(name="created_by")
	private String createdBy;

	public Department() {
	}

	public Department(Integer id, String rootOrg, String dept, Integer[] deptTypeId, String description) {
		this.id = id;
		this.rootOrg = rootOrg;
		this.deptName = dept;
		this.deptTypeId = deptTypeId;
		this.description = description;
	}

	public Integer getDeptId() {
		return id;
	}

	public void setDeptId(Integer deptId) {
		this.id = deptId;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public Integer[] getDeptTypeIds() {
		return deptTypeId;
	}

	public void setDeptTypeIds(Integer[] deptTypeId) {
		this.deptTypeId = deptTypeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHeadquarters() {
		return headquarters;
	}

	public void setHeadquarters(String headquarters) {
		this.headquarters = headquarters;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
	
	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long created) {
		this.creationDate = created;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public static Department clone(DepartmentInfo deptInfo) {
		Department dept = new Department();
		dept.setDeptName(deptInfo.getDeptName());
		dept.setDeptTypeIds(deptInfo.getDeptTypeIds());
		dept.setDescription(deptInfo.getDescription());
		dept.setHeadquarters(deptInfo.getHeadquarters());
		dept.setRootOrg(deptInfo.getRootOrg());
		dept.setLogo(deptInfo.getLogo());
		return dept;
	}
	
	public DeptPublicInfo getPublicInfo() {
		DeptPublicInfo pDept = new DeptPublicInfo();
		pDept.setDescription(getDescription());
		pDept.setId(getDeptId());
		pDept.setFriendly_name(getDeptName());
		pDept.setRoot_org(getRootOrg());
		return pDept;
	}
}
