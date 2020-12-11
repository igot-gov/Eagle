package com.infosys.lex.portal.department.model;

import java.util.ArrayList;
import java.util.List;

import com.infosys.lex.portal.department.dto.UserDepartmentRole;

public class DepartmentInfo {
	private Integer id;
	private String rootOrg;
	private String deptName;
	private Integer deptTypeId;
	private DeptTypeInfo deptTypeInfo;
	private List<DeptRoleInfo> rolesInfo;
	private String description;
	private long noOfUsers;
	private String headquarters;
	private byte[] logo;
	private List<UserDepartmentRole> adminUserList;
	private List<PortalUserInfo> active_users;
	private List<PortalUserInfo> inActive_users;
	private List<PortalUserInfo> blocked_users;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public DeptTypeInfo getDeptTypeInfo() {
		return deptTypeInfo;
	}

	public void setDeptTypeInfo(DeptTypeInfo deptTypeInfo) {
		this.deptTypeInfo = deptTypeInfo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getNoOfUsers() {
		return noOfUsers;
	}

	public void setNoOfUsers(long noOfUsers) {
		this.noOfUsers = noOfUsers;
	}

	public List<DeptRoleInfo> getRolesInfo() {
		return rolesInfo;
	}

	public void setRolesInfo(List<DeptRoleInfo> rolesInfo) {
		this.rolesInfo = rolesInfo;
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

	public List<UserDepartmentRole> getAdminUserList() {
		return adminUserList;
	}

	public void setAdminUserList(List<UserDepartmentRole> adminUserList) {
		this.adminUserList = adminUserList;
	}

	public List<PortalUserInfo> getActive_users() {
		return active_users;
	}

	public void setActive_users(List<PortalUserInfo> active_users) {
		this.active_users = active_users;
	}

	public List<PortalUserInfo> getInActive_users() {
		return inActive_users;
	}

	public void setInActive_users(List<PortalUserInfo> inActive_users) {
		this.inActive_users = inActive_users;
	}

	public List<PortalUserInfo> getBlocked_users() {
		return blocked_users;
	}

	public void setBlocked_users(List<PortalUserInfo> blocked_users) {
		this.blocked_users = blocked_users;
	}

	public void addDeptRoleInfo(DeptRoleInfo deptRoleInfo) {
		if (this.rolesInfo == null) {
			this.rolesInfo = new ArrayList<DeptRoleInfo>();
		}
		this.rolesInfo.add(deptRoleInfo);
	}

	public void addActiveUser(PortalUserInfo pUserInfo) {
		if (this.active_users == null) {
			this.active_users = new ArrayList<PortalUserInfo>();
		}
		this.active_users.add(pUserInfo);
	}

	public void addInActiveUser(PortalUserInfo pUserInfo) {
		if (this.inActive_users == null) {
			this.inActive_users = new ArrayList<PortalUserInfo>();
		}
		this.inActive_users.add(pUserInfo);
	}

	public void addBlockedUser(PortalUserInfo pUserInfo) {
		if (this.blocked_users == null) {
			this.blocked_users = new ArrayList<PortalUserInfo>();
		}
		this.blocked_users.add(pUserInfo);
	}

	public String toString() {
		StringBuilder str = new StringBuilder("DepartmentInfo:");
		str.append(" Id:").append(id);
		str.append(", RootOrg:").append(rootOrg);
		str.append(", DepartmentName: ").append(deptName);
		str.append(", DeptTypeId: ").append(deptTypeId);
		str.append(", DeptTypeInfo: ").append(deptTypeInfo);
		str.append(", Description: ").append(description);
		str.append(", headquarters: ").append(headquarters);
		str.append(", noOfUsers: ").append(noOfUsers);
		str.append("]");

		return str.toString();
	}
}
