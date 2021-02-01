package org.sunbird.portal.department.service;

import org.sunbird.portal.department.model.DepartmentInfo;

public interface MdoPortalService {
	DepartmentInfo updateDepartment(DepartmentInfo deptInfo,String rootOrg,String authorization,String xAuthenticatedUserToken) throws Exception;

	DepartmentInfo getMyDepartment(String userId, boolean isUserInfoRequired,String rootOrg,String authorization,String xAuthenticatedUserToken) throws Exception;
}
