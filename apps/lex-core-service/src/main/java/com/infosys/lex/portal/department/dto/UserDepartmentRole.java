package com.infosys.lex.portal.department.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_department_role", schema = "public")
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

	@Column(name = "dept_role_id")
	@NotNull
	Integer deptRoleId;

	@Column(name = "isactive")
	@NotNull
	Boolean isActive;

	@Column(name = "isblocked")
	@NotNull
	Boolean isBlocked;

	public UserDepartmentRole() {
	}

	public UserDepartmentRole(Integer id, String userId, Integer deptId, Integer deptRoleId, Boolean isActive) {
		this.id = id;
		this.userId = userId;
		this.deptId = deptId;
		this.deptRoleId = deptRoleId;
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

	public Integer getDeptRoleId() {
		return deptRoleId;
	}

	public void setDeptRoleId(Integer deptRoleId) {
		this.deptRoleId = deptRoleId;
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

	public String toString() {
		return "[UserDepartmentRole = " + ", Id: " + ((id == null) ? 0 : id) + ", userId: " + userId + ", DeptRoleId: "
				+ deptRoleId + ", isActive: " + isActive + ", isBlocked: " + isBlocked + "]";
	}
}
