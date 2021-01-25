package com.infosys.lex.portal.department.service;

import java.util.List;

import com.infosys.lex.portal.department.dto.Department;
import com.infosys.lex.portal.department.dto.UserDepartmentRole;
import com.infosys.lex.portal.department.model.DepartmentInfo;
import com.infosys.lex.portal.department.model.SearchUserInfo;
import com.infosys.lex.portal.department.model.UserDepartmentInfo;
import com.infosys.lex.portal.department.model.UserRoleDepartmentInfo;

public interface PortalService {
	List<DepartmentInfo> getAllDepartments();
	
	List<String> getDeptNameList();

	DepartmentInfo getDepartmentById(Integer deptId, boolean isUserInfoRequired);

	List<Department> getDepartmentsByUserId(String userId);

	UserDepartmentInfo addUserRoleInDepartment(UserDepartmentRole userDeptRole, String wid, String rootOrg, String org) throws Exception;
	
	UserDepartmentInfo updateUserRoleInDepartment(UserDepartmentRole userDeptRole, String wid, String rootOrg, String org) throws Exception;

	Boolean checkAdminPrivilage(Integer deptId, String userId) throws Exception;

	Boolean checkMdoAdminPrivilage(String deptKey, String userId) throws Exception;

	DepartmentInfo getMyDepartmentDetails(String userId, boolean isUserInfoRequired) throws Exception;

	DepartmentInfo updateDepartment(DepartmentInfo deptInfo) throws Exception;

	boolean isAdmin(String strDeptType, String roleName, String userId);

	boolean validateCBPUserLogin(String userId);

	DepartmentInfo addDepartment(String userId, String userRoleName, DepartmentInfo deptInfo) throws Exception;

	DepartmentInfo getMyDepartment(String userId) throws Exception;

	DepartmentInfo getMyDepartment(String deptType, String userId, boolean isUserInfoRequired) throws Exception;
	
    DepartmentInfo getMyCbpDepartment(String userId) throws Exception;
	
	DepartmentInfo getMyDepartmentForRole(String roleName, String userId, boolean isUserInfoRequired) throws Exception;
	
	List<SearchUserInfo> searchUserForRole(Integer deptId, String roleName, String userName) throws Exception;

	UserDepartmentRole updateUserRoleDepartment(UserRoleDepartmentInfo userRoleDepartmentInfo, String wid, String rootOrg, String org) throws Exception;
}
