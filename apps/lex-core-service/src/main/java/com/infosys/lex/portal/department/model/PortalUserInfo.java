package com.infosys.lex.portal.department.model;

public class PortalUserInfo {
	private String userId;
	private String firstName;
	private String lastName;
	private String emailId;
	private boolean isActive;
	private boolean isBlocked;
	private DeptRoleInfo roleInfo;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public DeptRoleInfo getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(DeptRoleInfo roleInfo) {
		this.roleInfo = roleInfo;
	}
}
