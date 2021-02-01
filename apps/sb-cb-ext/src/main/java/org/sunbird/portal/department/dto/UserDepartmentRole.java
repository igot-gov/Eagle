package org.sunbird.portal.department.dto;

import java.util.List;

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
@Table(name = "user_department_role", schema = "wingspan")
public class UserDepartmentRole {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column(name = "user_id")
	@NotNull
	String userId;

	@Column(name = "dept_id")
	@NotNull
	Integer deptId;

	@Column(name = "role_ids")
	@NotNull
	@Type(type = "org.sunbird.portal.department.dto.GenericArrayUserType")
	Integer[] roleIds;

	@Column(name = "isactive")
	@NotNull
	Boolean isActive;

	@Column(name = "isblocked")
	@NotNull
	Boolean isBlocked;

	@Transient
	List<String> roles;

	public UserDepartmentRole() {
	}

	public UserDepartmentRole(Integer id, String userId, Integer deptId, Integer[] roleIds, Boolean isActive) {
		this.id = id;
		this.userId = userId;
		this.deptId = deptId;
		this.roleIds = roleIds;
		this.isActive = isActive;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public Integer[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(Integer[] roleIds) {
		this.roleIds = roleIds;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String toString() {
		return "[UserDepartmentRole = " + ", Id: " + ((id == null) ? 0 : id) + ", userId: " + userId + ", DeptRoleId: "
				+ roleIds + ", isActive: " + isActive + ", isBlocked: " + isBlocked + "]";
	}
}
