package com.infosys.lex.portal.department.service;

import com.infosys.lex.portal.department.model.DepartmentInfo;

public interface MdoPortalService {
	DepartmentInfo updateDepartment(DepartmentInfo deptInfo) throws Exception;

	DepartmentInfo getMyDepartment(String userId, boolean isUserInfoRequired) throws Exception;
}
