package org.sunbird.portal.department.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "department_roles", schema = "wingspan")
public class DepartmentRole {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "role_ids")
	@NotNull
	@Type(type = "org.sunbird.portal.department.dto.GenericArrayUserType")
	private Integer[] roleIds;

	@Column(name = "dept_type")
	@NotNull
	private String deptType;

	@Transient
	private Iterable<Role> roles;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(Integer[] roleIds) {
		this.roleIds = roleIds;
	}

	public String getDeptType() {
		return deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public Iterable<Role> getRoles() {
		return roles;
	}

	public void setRoles(Iterable<Role> roles) {
		this.roles = roles;
	}
}
