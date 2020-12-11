package com.infosys.lex.portal.department.model;

public class UserDepartmentInfo {
	private String userId;
	private DepartmentInfo deptInfo;
	private DeptRoleInfo deptRoleInfo;
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

	public DeptRoleInfo getDeptRoleInfo() {
		return deptRoleInfo;
	}

	public void setDeptRoleInfo(DeptRoleInfo deptRoleInfo) {
		this.deptRoleInfo = deptRoleInfo;
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
