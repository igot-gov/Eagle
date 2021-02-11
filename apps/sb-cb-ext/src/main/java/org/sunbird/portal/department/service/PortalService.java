package org.sunbird.portal.department.service;

import java.util.List;

import org.sunbird.portal.department.dto.Department;
import org.sunbird.portal.department.dto.UserDepartmentRole;
import org.sunbird.portal.department.model.DepartmentInfo;
import org.sunbird.portal.department.model.DeptPublicInfo;
import org.sunbird.portal.department.model.SearchUserInfo;
import org.sunbird.portal.department.model.UserDepartmentInfo;

public interface PortalService {
	List<DepartmentInfo> getAllDepartments(String rootOrg);
	
	List<String> getDeptNameList();
	
	List<DeptPublicInfo> getAllDept();
	
	DeptPublicInfo searchDept(String deptName);

	DepartmentInfo getDepartmentById(Integer deptId, boolean isUserInfoRequired,String rootOrg);

	List<Department> getDepartmentsByUserId(String userId);

	UserDepartmentInfo addUserRoleInDepartment(UserDepartmentRole userDeptRole, String wid, String rootOrg, String org) throws Exception;
	
	UserDepartmentInfo updateUserRoleInDepartment(UserDepartmentRole userDeptRole, String wid, String rootOrg, String org) throws Exception;

	Boolean checkAdminPrivilage(Integer deptId, String userId,String rootOrg) throws Exception;

	Boolean checkMdoAdminPrivilage(String deptKey, String userId) throws Exception;

	DepartmentInfo getMyDepartmentDetails(String userId, boolean isUserInfoRequired) throws Exception;

	DepartmentInfo updateDepartment(DepartmentInfo deptInfo,String rootOrg) throws Exception;

	boolean isAdmin(String strDeptType, String roleName, String userId);

	boolean validateCBPUserLogin(String userId);

	DepartmentInfo addDepartment(String userId, String userRoleName, DepartmentInfo deptInfo,String rootOrg) throws Exception;

	DepartmentInfo getMyDepartment(String userId,String rootOrg) throws Exception;

	DepartmentInfo getMyDepartment(String deptType, String userId, boolean isUserInfoRequired,String rootOrg) throws Exception;
	
    DepartmentInfo getMyCbpDepartment(String userId,String rootOrg) throws Exception;
	
	DepartmentInfo getMyDepartmentForRole(String roleName, String userId, boolean isUserInfoRequired,String rootOrg) throws Exception;
	
	List<SearchUserInfo> searchUserForRole(Integer deptId, String roleName, String userName) throws Exception;
}
