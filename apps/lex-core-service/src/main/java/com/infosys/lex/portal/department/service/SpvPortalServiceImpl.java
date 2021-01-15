package com.infosys.lex.portal.department.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.portal.department.model.DepartmentInfo;
import com.infosys.lex.portal.department.repo.DepartmentRepository;
import com.infosys.lex.portal.department.repo.DepartmentTypeRepository;
import com.infosys.lex.portal.department.repo.RoleRepository;
import com.infosys.lex.portal.department.repo.UserDepartmentRoleRepository;
import com.infosys.lex.portal.department.PortalConstants;

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
	public List<DepartmentInfo> getAllDepartments() throws Exception {
		return portalService.getAllDepartments();
	}

	@Override
	public DepartmentInfo getMyDepartment(String userId, boolean isUserInfoRequired) throws Exception {
		return portalService.getMyDepartment(PortalConstants.SPV_DEPT_TYPE, userId, isUserInfoRequired);
	}

	@Override
	public DepartmentInfo addDepartment(String userId, DepartmentInfo deptInfo) throws Exception {
		return portalService.addDepartment(userId, PortalConstants.SPV_ROLE_NAME, deptInfo);
	}

	@Override
	public DepartmentInfo updateDepartment(DepartmentInfo deptInfo) throws Exception {
		return portalService.updateDepartment(deptInfo);
	}

	@Override
	public DepartmentInfo getDepartmentById(Integer deptId, boolean isUserInfoRequired) throws Exception {
		return portalService.getDepartmentById(deptId, isUserInfoRequired);
	}
}
