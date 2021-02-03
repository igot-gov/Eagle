package org.sunbird.portal.department.service;

import java.util.List;

import org.sunbird.portal.department.model.DepartmentInfo;

public interface SpvPortalService {
	public List<DepartmentInfo> getAllDepartments(String rootOrg) throws Exception;

	public DepartmentInfo addDepartment(String userId, DepartmentInfo deptInfo,String rootOrg) throws Exception;

	DepartmentInfo updateDepartment(DepartmentInfo deptInfo,String rootOrg) throws Exception;

	DepartmentInfo getMyDepartment(String userId, boolean isUserInfoRequired,String rootOrg) throws Exception;

	DepartmentInfo getDepartmentById(Integer deptId, boolean isUserInfoRequired,String rootOrg) throws Exception;
}
