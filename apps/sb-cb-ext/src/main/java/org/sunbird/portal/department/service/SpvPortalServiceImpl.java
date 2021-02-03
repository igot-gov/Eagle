package org.sunbird.portal.department.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunbird.portal.department.PortalConstants;
import org.sunbird.portal.department.model.DepartmentInfo;
import org.sunbird.portal.department.repo.DepartmentRepository;
import org.sunbird.portal.department.repo.DepartmentTypeRepository;
import org.sunbird.portal.department.repo.RoleRepository;
import org.sunbird.portal.department.repo.UserDepartmentRoleRepository;

@Service
public class SpvPortalServiceImpl implements SpvPortalService {
	@Autowired
	PortalService portalService;

	@Autowired
	UserDepartmentRoleRepository userDepartmentRoleRepo;

	@Autowired
	RoleRepository roleRepo;

	@Autowired
	DepartmentRepository deptRepo;

	@Autowired
	DepartmentTypeRepository deptTypeRepo;

	@Override
	public List<DepartmentInfo> getAllDepartments(String rootOrg) throws Exception {
		return portalService.getAllDepartments(rootOrg);
	}

	@Override
	public DepartmentInfo getMyDepartment(String userId, boolean isUserInfoRequired, String rootOrg) throws Exception {
		return portalService.getMyDepartment(PortalConstants.SPV_DEPT_TYPE, userId, isUserInfoRequired, rootOrg);
	}

	@Override
	public DepartmentInfo addDepartment(String userId, DepartmentInfo deptInfo, String rootOrg) throws Exception {
		return portalService.addDepartment(userId, PortalConstants.SPV_ROLE_NAME, deptInfo, rootOrg);
	}

	@Override
	public DepartmentInfo updateDepartment(DepartmentInfo deptInfo, String rootOrg) throws Exception {
		return portalService.updateDepartment(deptInfo, rootOrg);
	}

	@Override
	public DepartmentInfo getDepartmentById(Integer deptId, boolean isUserInfoRequired, String rootOrg)
			throws Exception {
		return portalService.getDepartmentById(deptId, isUserInfoRequired, rootOrg);
	}
}
