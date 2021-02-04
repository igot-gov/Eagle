package org.sunbird.portal.department.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sunbird.common.util.DataValidator;
import org.sunbird.core.logger.CbExtLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.sunbird.portal.department.dto.DepartmentRole;
import org.sunbird.portal.department.dto.Role;
import org.sunbird.portal.department.dto.UserDepartmentRole;
import org.sunbird.portal.department.repo.DepartmentRoleRepository;
import org.sunbird.portal.department.repo.RoleRepository;
import org.sunbird.portal.department.repo.UserDepartmentRoleRepository;

import javax.transaction.NotSupportedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	RoleRepository roleRepo;

	@Autowired
	DepartmentRoleRepository deptRoleRepo;

	@Autowired
	UserDepartmentRoleRepository userDepartmentRoleRepository;

	@Override
	public Iterable<Role> getAllRoles() {
		return roleRepo.findAll();
	}

	@Override
	public Role getRoleById(Integer roleId) {
		return roleRepo.findById(roleId).get();
	}

	private CbExtLogger logger = new CbExtLogger(RoleServiceImpl.class.getName());

	@Override
	public Role addRole(Role role) throws Exception {
		Role existingRole = roleRepo.findRoleByRoleName(role.getRoleName());
		if (existingRole != null) {
			throw new Exception("Role exist with name - " + role.getRoleName());
		} else {
			return roleRepo.save(role);
		}
	}

	@Override
	public Role updateRole(Role role) throws Exception {
		Optional<Role> existingRole = roleRepo.findById(role.getId());
		if (existingRole.isPresent()) {
			existingRole.get().setRoleName(role.getRoleName());
			existingRole.get().setDescription(role.getDescription());
			return roleRepo.save(existingRole.get());
		} else {
			throw new Exception("Role doesn't exist with Id - " + role.getId());
		}
	}

	@Override
	public Iterable<DepartmentRole> getAllDepartmentRoles() {
		return enrichDepartmentRoleInfo(deptRoleRepo.findAll());
	}

	@Override
	public DepartmentRole getDepartmentRoleById(String deptType) {
		return enrichDepartmentRoleInfo(deptRoleRepo.findByDeptTypeIgnoreCase(deptType));
	}

	@Override
	public DepartmentRole addDepartmentRole(DepartmentRole deptRole) throws Exception {
//		DepartmentRole existingDeptRole = deptRoleRepo.findByRoleIdAndDeptId(deptRole.getRoleId(),
//				deptRole.getDeptId());
//
//		if (existingDeptRole != null) {
//			throw new Exception(
//					"DeptRole mapping exist for RoleId: " + deptRole.getRoleId() + ", deptId: " + deptRole.getDeptId());
//		} else {
//			return deptRoleRepo.save(deptRole);
//		}
		throw new NotSupportedException("Yet to implement this method");
	}

	@Override
	public boolean removeDepartmentRole(Integer deptRoleId) throws Exception {
//		Optional<DepartmentRole> existingDeptRole = deptRoleRepo.findById(deptRoleId);
//		if (existingDeptRole.isPresent()) {
//			deptRoleRepo.delete(existingDeptRole.get());
//			return true;
//		} else {
//			throw new Exception("DeptRole mapping doesn't exist for deptRoleId: " + deptRoleId);
//		}
		throw new NotSupportedException("Yet to implement this method");
	}

	private Iterable<DepartmentRole> enrichDepartmentRoleInfo(Iterable<DepartmentRole> deptRoleList) {
		if (!DataValidator.isCollectionEmpty(deptRoleList)) {
			for (DepartmentRole deptRole : deptRoleList) {
				enrichDepartmentRoleInfo(deptRole);
			}
		}
		return deptRoleList;
	}

	private DepartmentRole enrichDepartmentRoleInfo(DepartmentRole deptRole) {
		if (deptRole != null) {
			Iterable<Role> rList = roleRepo.findAllById(Arrays.asList(deptRole.getRoleIds()));
			deptRole.setRoles(rList);
		}
		return deptRole;
	}

	/**
	 * @param userId wid of the user
	 * @return return role list for department user
	 */
	public List<String> getUserDepartMentRoles(String userId) {

		List<String> returnedRoleList = new ArrayList<>();
		try {
			List<UserDepartmentRole> userDepartmentRoles = userDepartmentRoleRepository.findAllByUserIdAndIsActiveAndIsBlocked(userId, true, false);
			if (CollectionUtils.isEmpty(userDepartmentRoles))
				return Collections.emptyList();
			List<Integer> roleIds = new ArrayList<>();
			userDepartmentRoles.forEach(userDepartmentRole -> {
				roleIds.addAll(Arrays.asList(userDepartmentRole.getRoleIds()));
			});

			if (roleIds.isEmpty())
				return Collections.emptyList();
			Iterator<Role> iterableRole = roleRepo.findAllById(roleIds).iterator();
			while (iterableRole.hasNext()) {
				returnedRoleList.add(iterableRole.next().getRoleName());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnedRoleList;
	}

	public Set<String> getUserRoles(String userId) {
		List<String> userRoles = getUserDepartMentRoles(userId);
		if (CollectionUtils.isEmpty(userRoles))
			return Collections.emptySet();
		return userRoles.stream().collect(Collectors.toSet());
	}
}
