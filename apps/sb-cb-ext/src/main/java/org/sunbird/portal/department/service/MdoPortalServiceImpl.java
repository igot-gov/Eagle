package org.sunbird.portal.department.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunbird.portal.department.PortalConstants;
import org.sunbird.portal.department.model.DepartmentInfo;

@Service
public class MdoPortalServiceImpl implements MdoPortalService {
	@Autowired
	PortalService portalService;

	@Override
	public DepartmentInfo updateDepartment(DepartmentInfo deptInfo,String rootOrg,String authorization,String xAuthenticatedUserToken) throws Exception {
		return portalService.updateDepartment(deptInfo,rootOrg,authorization,xAuthenticatedUserToken);
	}

	@Override
	public DepartmentInfo getMyDepartment(String userId, boolean isUserInfoRequired,String rootOrg,String authorization,String xAuthenticatedUserToken) throws Exception {
		return portalService.getMyDepartmentForRole(PortalConstants.MDO_ROLE_NAME, userId, isUserInfoRequired,rootOrg,authorization,xAuthenticatedUserToken);
	}
}
