package org.sunbird.portal.department.model;

import java.util.ArrayList;
import java.util.List;

import org.sunbird.portal.department.dto.Role;
import org.sunbird.portal.department.dto.UserDepartmentRole;

public class DepartmentInfo implements Comparable<DepartmentInfo> {
	private Integer id;
	private String rootOrg;
	private String deptName;
	private Integer[] deptTypeIds;
	private List<DeptTypeInfo> deptTypeInfos;
	private List<Role> rolesInfo;
	private String description;
	private long noOfUsers;
	private String headquarters;
	private byte[] logo;
	private long creationDate;
	private String createdBy;
	private List<UserDepartmentRole> adminUserList;
	private List<PortalUserInfo> active_users;
	private List<PortalUserInfo> inActive_users;
	private List<PortalUserInfo> blocked_users;
	private List<String> currentUserRoles;

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

	public Integer[] getDeptTypeIds() {
		return deptTypeIds;
	}

	public void setDeptTypeIds(Integer[] deptTypeIds) {
		this.deptTypeIds = deptTypeIds;
	}

	public List<DeptTypeInfo> getDeptTypeInfos() {
		return deptTypeInfos;
	}

	public void setDeptTypeInfos(List<DeptTypeInfo> deptTypeInfo) {
		this.deptTypeInfos = deptTypeInfo;
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

	public List<Role> getRolesInfo() {
		return rolesInfo;
	}

	public void setRolesInfo(List<Role> rolesInfo) {
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

	public void addRoleInfo(Role roleInfo) {
		if (this.rolesInfo == null) {
			this.rolesInfo = new ArrayList<Role>();
		}
		this.rolesInfo.add(roleInfo);
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

	public void addAdminUser(UserDepartmentRole pUserInfo) {
		if (this.adminUserList == null) {
			this.adminUserList = new ArrayList<UserDepartmentRole>();
		}
		this.adminUserList.add(pUserInfo);
	}

	public List<String> getCurrentUserRoles() {
		return currentUserRoles;
	}

	public void setCurrentUserRoles(List<String> currentUserRoles) {
		this.currentUserRoles = currentUserRoles;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long created) {
		this.creationDate = created;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String toString() {
		StringBuilder str = new StringBuilder("DepartmentInfo:");
		str.append(" Id:").append(id);
		str.append(", RootOrg:").append(rootOrg);
		str.append(", DepartmentName: ").append(deptName);
		str.append(", DeptTypeId: ").append(deptTypeIds);
		str.append(", DeptTypeInfo: ").append(deptTypeInfos);
		str.append(", Description: ").append(description);
		str.append(", headquarters: ").append(headquarters);
		str.append(", noOfUsers: ").append(noOfUsers);
		str.append(", creationDate: ").append(creationDate);
		str.append(", createdBy: ").append(createdBy);
		str.append("]");

		return str.toString();
	}

	public int compareTo(DepartmentInfo o) {
		return Long.compare(o.getCreationDate(), this.getCreationDate());
	}
}
