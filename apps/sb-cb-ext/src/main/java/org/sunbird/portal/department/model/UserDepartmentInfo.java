package org.sunbird.portal.department.model;

import org.sunbird.portal.department.dto.Role;

public class UserDepartmentInfo {
	private String userId;
	private DepartmentInfo deptInfo;
	private Iterable<Role> roleInfo;
	private Boolean isActive;
	private Boolean isBlocked;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public DepartmentInfo getDeptInfo() {
		return deptInfo;
	}

	public void setDeptInfo(DepartmentInfo deptInfo) {
		this.deptInfo = deptInfo;
	}

	public Iterable<Role> getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(Iterable<Role> roleInfo) {
		this.roleInfo = roleInfo;
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

}
