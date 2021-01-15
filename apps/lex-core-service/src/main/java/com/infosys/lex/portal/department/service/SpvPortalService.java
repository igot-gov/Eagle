package com.infosys.lex.portal.department.service;

import java.util.List;

import com.infosys.lex.portal.department.model.DepartmentInfo;

public interface SpvPortalService {
	public List<DepartmentInfo> getAllDepartments() throws Exception;

	public DepartmentInfo addDepartment(String userId, DepartmentInfo deptInfo) throws Exception;

	DepartmentInfo updateDepartment(DepartmentInfo deptInfo) throws Exception;

	DepartmentInfo getMyDepartment(String userId, boolean isUserInfoRequired) throws Exception;

	DepartmentInfo getDepartmentById(Integer deptId, boolean isUserInfoRequired) throws Exception;
}
