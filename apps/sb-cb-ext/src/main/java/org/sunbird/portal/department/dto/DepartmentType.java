package org.sunbird.portal.department.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "department_types", schema = "wingspan")
public class DepartmentType {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column(name = "dept_type")
	@NotNull
	private String deptType;

	@Column(name = "dept_subtype")
	@NotNull
	private String deptSubType;

	@Column(name = "description")
	@NotNull
	private String description;

	public DepartmentType() {
	}

	public DepartmentType(Integer id, String deptType, String deptSubType, String description) {
		this.id = id;
		this.deptType = deptType;
		this.deptSubType = deptSubType;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDeptType() {
		return deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public String getDeptSubType() {
		return deptSubType;
	}

	public void setDeptSubType(String deptSubType) {
		this.deptSubType = deptSubType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return "[DepartmentType=id:" + id + ", deptType: " + deptType + ", deptSubType: " + deptSubType
				+ ", description: " + description + "]";
	}
}
