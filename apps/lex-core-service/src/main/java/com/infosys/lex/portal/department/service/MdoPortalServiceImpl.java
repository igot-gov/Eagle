package com.infosys.lex.portal.department.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.portal.department.model.DepartmentInfo;
import com.infosys.lex.portal.department.PortalConstants;

@Service
public class MdoPortalServiceImpl implements MdoPortalService {
	@Autowired
	PortalService portalService;

	@Override
	public DepartmentInfo updateDepartment(DepartmentInfo deptInfo) throws Exception {
		return portalService.updateDepartment(deptInfo);
	}

	@Override
	public DepartmentInfo getMyDepartment(String userId, boolean isUserInfoRequired) throws Exception {
		return portalService.getMyDepartment(PortalConstants.MDO_DEPT_TYPE, userId, isUserInfoRequired);
	}
}
