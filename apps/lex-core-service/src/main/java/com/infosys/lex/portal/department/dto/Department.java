package com.infosys.lex.portal.department.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.infosys.lex.portal.department.model.DepartmentInfo;

@Entity
@Table(name = "departments", schema = "wingspan")
public class Department {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer deptId;

	@Column(name = "root_org")
	@NotNull
	private String rootOrg;

	@Column(name = "dept_name")
	@NotNull
	private String deptName;

	@Column(name = "dept_type_id")
	@NotNull
	private Integer deptTypeId;

	@Column(name = "description")
	@NotNull
	private String description;

	@Column(name = "headquarters")
	private String headquarters;

	@Column(name = "logo")
	private byte[] logo;

	public Department() {
	}

	public Department(Integer deptId, String rootOrg, String dept, Integer deptTypeId, String description) {
		this.deptId = deptId;
		this.rootOrg = rootOrg;
		this.deptName = dept;
		this.deptTypeId = deptTypeId;
		this.description = description;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
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

	public Integer getDeptTypeId() {
		return deptTypeId;
	}

	public void setDeptTypeId(Integer deptTypeId) {
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

	public static Department clone(DepartmentInfo deptInfo) {
		Department dept = new Department();
		dept.setDeptName(deptInfo.getDeptName());
		dept.setDeptTypeId(deptInfo.getDeptTypeId());
		dept.setDescription(deptInfo.getDescription());
		dept.setHeadquarters(deptInfo.getHeadquarters());
		dept.setRootOrg(deptInfo.getRootOrg());
		dept.setLogo(deptInfo.getLogo());
		return dept;
	}
}
