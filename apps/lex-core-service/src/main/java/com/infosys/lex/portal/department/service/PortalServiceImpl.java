package com.infosys.lex.portal.department.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.DataValidator;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.logger.LexLogger;
import com.infosys.lex.portal.department.dto.Department;
import com.infosys.lex.portal.department.dto.DepartmentRole;
import com.infosys.lex.portal.department.dto.DepartmentType;
import com.infosys.lex.portal.department.dto.Role;
import com.infosys.lex.portal.department.dto.UserDepartmentRole;
import com.infosys.lex.portal.department.model.DepartmentInfo;
import com.infosys.lex.portal.department.model.DeptRoleInfo;
import com.infosys.lex.portal.department.model.DeptTypeInfo;
import com.infosys.lex.portal.department.model.PortalUserInfo;
import com.infosys.lex.portal.department.model.UserDepartmentInfo;
import com.infosys.lex.portal.department.repo.DepartmentRepository;
import com.infosys.lex.portal.department.repo.DepartmentRoleRepository;
import com.infosys.lex.portal.department.repo.DepartmentTypeRepository;
import com.infosys.lex.portal.department.repo.RoleRepository;
import com.infosys.lex.portal.department.repo.UserDepartmentRoleRepository;

@Service
public class PortalServiceImpl implements PortalService {

	private LexLogger logger = new LexLogger(getClass().getName());

	@Autowired
	UserDepartmentRoleRepository userDepartmentRoleRepo;

	@Autowired
	DepartmentRepository deptRepo;

	@Autowired
	DepartmentTypeRepository deptTypeRepo;

	@Autowired
	DepartmentRoleRepository deptRoleRepo;

	@Autowired
	RoleRepository roleRepo;

	@Autowired
	UserUtilityService userUtilService;

	@Override
	public List<DepartmentInfo> getAllDepartments() {
		return enrichDepartmentInfo(deptRepo.findAll(), true);
	}

	@Override
	public DepartmentInfo getDepartmentById(Integer deptId, boolean isUserInfoRequired) {
		return enrichDepartmentInfo(deptId, isUserInfoRequired, true);
	}

	@Override
	public List<Department> getDepartmentsByUserId(String userId) {
		return null;
	}

	@Override
	public DepartmentInfo addDepartment(DepartmentInfo deptInfo) throws Exception {
		if (deptRepo.existsByDeptName(deptInfo.getDeptName())) {
			throw new Exception(
					"Failed to create Department. Given deptName: '" + deptInfo.getDeptName() + "' already exists");
		}

		if (!validateDepartmentInfo(deptInfo)) {
			throw new Exception("Failed to create Department. Given Department is null OR RootOrg/DeptName is null");
		}

		if (deptInfo.getDeptTypeId() == null) {
			if (validateDepartmentTypeInfo(deptInfo.getDeptTypeInfo())) {
				// We need to create this DeptType.
				DepartmentType dType = deptTypeRepo.findByDeptTypeAndDeptSubType(
						deptInfo.getDeptTypeInfo().getDeptType(), deptInfo.getDeptTypeInfo().getDeptSubType());
				if (dType != null) {
					StringBuilder str = new StringBuilder("Failed to create DepartmentType object. DeptType: '");
					str.append(deptInfo.getDeptTypeInfo().getDeptType());
					str.append("' and DeptSubType '").append(deptInfo.getDeptTypeInfo().getDeptSubType());
					str.append("' are already exists");
					throw new Exception(str.toString());
				} else {
					DepartmentType deptType = new DepartmentType();
					deptType.setDeptType(deptInfo.getDeptTypeInfo().getDeptType());
					deptType.setDeptSubType(deptInfo.getDeptTypeInfo().getDeptSubType());
					deptType.setDescription(deptInfo.getDeptTypeInfo().getDescription());
					dType = deptTypeRepo.save(deptType);
					deptInfo.setDeptTypeId(dType.getId());
				}
			} else {
				throw new Exception(
						"Invalid Department Type details. Either deptTypeId or deptTypeInfo object should be present.");
			}
		}

		// Department is Valid -- add this Department
		Department dept = deptRepo.save(Department.clone(deptInfo));
		
		//Let's add Default roles for this Department
		Role adminRole = roleRepo.findRoleByRoleName("ADMIN");
		DepartmentRole deptAdminRole = new DepartmentRole();
		deptAdminRole.setRoleId(adminRole.getId());
		deptAdminRole.setDeptId(dept.getDeptId());
		deptRoleRepo.save(deptAdminRole);
		
		Role memberRole = roleRepo.findRoleByRoleName("MEMBER");
		DepartmentRole deptMemberRole = new DepartmentRole();
		deptMemberRole.setRoleId(memberRole.getId());
		deptMemberRole.setDeptId(dept.getDeptId());
		deptRoleRepo.save(deptMemberRole);
		
		if (!DataValidator.isCollectionEmpty(deptInfo.getAdminUserList())) {
			// Need to assign AdminRole for this Department
			DepartmentRole deptRole = deptRoleRepo.findByRoleIdAndDeptId(adminRole.getId(), dept.getDeptId());

			// We have Few admin Users to assign to this dept
			for (UserDepartmentRole userDeptRole : deptInfo.getAdminUserList()) {
				try {
					userDeptRole.setDeptId(dept.getDeptId());
					userDeptRole.setDeptRoleId(deptRole.getId());
					userDeptRole.setIsActive(true);
					userDeptRole.setIsBlocked(false);
					userDeptRole = userDepartmentRoleRepo.save(userDeptRole);
				} catch (Exception e) {
					logger.error(e);
					// TODO -- Need to decide what to do with this failed error...
				}
			}
		}
		return enrichDepartmentInfo(dept, false, true);
	}

	@Override
	public DepartmentInfo updateDepartment(DepartmentInfo deptInfo) throws Exception {
		Department existingDept = deptRepo.findById(deptInfo.getId()).get();
		if (existingDept != null) {
			existingDept.setDescription(deptInfo.getDescription());
			existingDept.setHeadquarters(deptInfo.getHeadquarters());
			existingDept.setLogo(deptInfo.getLogo());

			existingDept = deptRepo.save(existingDept);
			return enrichDepartmentInfo(existingDept, false, true);
		} else {
			throw new Exception("Failed to find Department for Id: " + deptInfo.getId());
		}
	}

	@Override
	public List<UserDepartmentInfo> getUserDepartments(String userId) {
		return enrichUserDepartments(userDepartmentRoleRepo.findByUserId(userId));
	}

	@Override
	public UserDepartmentInfo updateUserRoleInDepartment(UserDepartmentRole userDeptRole) throws Exception {
		validateUserDepartmentRole(userDeptRole);
		UserDepartmentRole existingRecord = userDepartmentRoleRepo.findByUserIdAndDeptIdAndDeptRoleId(
				userDeptRole.getUserId(), userDeptRole.getDeptId(), userDeptRole.getDeptRoleId());
		if (existingRecord != null) {
			existingRecord.setIsActive(userDeptRole.getIsActive());
			existingRecord.setIsBlocked(userDeptRole.getIsBlocked());
		} else {
			existingRecord = userDeptRole;
		}

		return enrichUserDepartment(userDepartmentRoleRepo.save(existingRecord));
	}

	@Override
	public Boolean checkAdminPrivilage(Integer deptId, String userId) throws Exception {
		List<UserDepartmentInfo> userDeptInfoList = enrichUserDepartments(
				userDepartmentRoleRepo.findByUserIdAndDeptId(userId, deptId));
		for (UserDepartmentInfo userDeptInfo : userDeptInfoList) {
			if ("ADMIN".equalsIgnoreCase(userDeptInfo.getDeptRoleInfo().getRoleName())) {
				return true;
			}
		}
		// If User is not ADMIN of given dept, then check for SPV admin.
		return checkMdoAdminPrivilage("SPV", userId);
	}

	@Override
	public Boolean checkMdoAdminPrivilage(String deptKey, String userId) throws Exception {
		boolean retValue = false;
		try {
			logger.info("checkMdoAdminPrivilage... userId: " + userId + ", deptKey: " + deptKey);
			// Find MDO Departments
			List<DepartmentType> deptTypes = deptTypeRepo.findByDeptTypeIgnoreCase(deptKey);

			List<Integer> deptTypeIds = new ArrayList<Integer>();
			if (!DataValidator.isCollectionEmpty(deptTypes)) {
				deptTypeIds = deptTypes.stream().map(i -> i.getId()).collect(Collectors.toList());
			}

			List<Department> depts = deptRepo.findAllByDeptTypeIdIn(deptTypeIds);
			List<Integer> deptIds = new ArrayList<Integer>();
			if (!DataValidator.isCollectionEmpty(depts)) {
				deptIds = depts.stream().map(i -> i.getDeptId()).collect(Collectors.toList());
			}

			Role role = roleRepo.findRoleByRoleName("ADMIN");
			List<DepartmentRole> deptRoles = deptRoleRepo.findAllByRoleIdAndDeptIdIn(role.getId(), deptIds);
			List<Integer> deptRoleIds = new ArrayList<Integer>();
			if (!DataValidator.isCollectionEmpty(deptRoles)) {
				deptRoleIds = deptRoles.stream().map(i -> i.getId()).collect(Collectors.toList());
			}

			List<UserDepartmentRole> userDeptRoles = userDepartmentRoleRepo.findAllByUserIdAndDeptRoleIdIn(userId,
					deptRoleIds);
			for (UserDepartmentRole userDeptRole : userDeptRoles) {
				if (userDeptRole.getUserId().equalsIgnoreCase(userId)) {
					retValue = userDeptRole.getIsActive() && !userDeptRole.getIsBlocked();
					break;
				}
			}

			logger.info("checkMdoAdminPrivilage... returns : " + retValue);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return retValue;
	}

	@Override
	public DepartmentInfo getMyDepartmentDetails(String userId, boolean isUserInfoRequired) throws Exception {
		List<UserDepartmentRole> userDepts = userDepartmentRoleRepo.findByUserId(userId);
		if (DataValidator.isCollectionEmpty(userDepts)) {
			throw new Exception("Failed to find department details for given userId: " + userId);
		}
		return getDepartmentById(userDepts.get(0).getDeptId(), isUserInfoRequired);
	}

	private DepartmentInfo enrichDepartmentInfo(Integer deptId, boolean isUserInfoRequired, boolean enrichData) {
		Optional<Department> dept = deptRepo.findById(deptId);
		if (dept.isPresent()) {
			return enrichDepartmentInfo(dept.get(), isUserInfoRequired, enrichData);
		}
		return null;
	}

	private DepartmentInfo enrichDepartmentInfo(Department dept, boolean isUserInfoRequired, boolean enrichData) {
		DepartmentInfo deptInfo = null;
		if (dept != null) {
			deptInfo = new DepartmentInfo();
			deptInfo.setDeptName(dept.getDeptName());
			deptInfo.setDescription(dept.getDescription());
			deptInfo.setId(dept.getDeptId());
			deptInfo.setRootOrg(dept.getRootOrg());
			deptInfo.setDeptTypeId(dept.getDeptTypeId());
			deptInfo.setHeadquarters(dept.getHeadquarters());
			deptInfo.setLogo(dept.getLogo());

			// Get Dept Type Infomation
			deptInfo.setDeptTypeInfo(enrichDepartmentTypeInfo(dept.getDeptTypeId()));

			// Get Number of Users in Department
			if (enrichData) {
				List<UserDepartmentRole> userDeptList = userDepartmentRoleRepo.findByDeptId(deptInfo.getId());
				deptInfo.setNoOfUsers(userDeptList == null ? 0 : userDeptList.size());

				// Get Role Informations
				Iterable<DepartmentRole> deptRoles = deptRoleRepo.findByDeptId(deptInfo.getId());
				if (deptRoles != null && deptRoles.iterator().hasNext()) {
					Iterator<DepartmentRole> it = deptRoles.iterator();
					while (it.hasNext()) {
						DepartmentRole dRole = it.next();
						Optional<Role> role = roleRepo.findById(dRole.getRoleId());
						if (role.isPresent()) {
							DeptRoleInfo dRoleInfo = new DeptRoleInfo();
							dRoleInfo.setDeptRoleId(dRole.getId());
							dRoleInfo.setRoleId(role.get().getId());
							dRoleInfo.setRoleName(role.get().getRoleName());
							dRoleInfo.setDescritpion(role.get().getDescription());
							deptInfo.addDeptRoleInfo(dRoleInfo);
						}
					}
				}

				Map<Integer, DeptRoleInfo> deptRoleMap = deptInfo.getRolesInfo().stream()
						.collect(Collectors.toMap(DeptRoleInfo::getDeptRoleId, deptRoleInfo -> deptRoleInfo));

				if (isUserInfoRequired && userDeptList != null && userDeptList.size() > 0) {
					Set<String> userIdSet = userDeptList.stream().map(i -> i.getUserId()).collect(Collectors.toSet());
					List<String> userIds = userIdSet.stream().collect(Collectors.toList());

					Map<String, Object> result = userUtilService.getUsersDataFromUserIds("igot", userIds,
							new ArrayList<>(Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME,
									PIDConstants.EMAIL, PIDConstants.DEPARTMENT_NAME)));
					logger.info("enrichDepartmentInfo UserIds -> " + userIds.toString() + ", fetched Information -> "
							+ result.size());
					for (UserDepartmentRole userDeptRole : userDeptList) {
						PortalUserInfo pUserInfo = new PortalUserInfo();
						pUserInfo.setUserId(userDeptRole.getUserId());
						pUserInfo.setActive(userDeptRole.getIsActive());
						pUserInfo.setBlocked(userDeptRole.getIsBlocked());
						// Fetch User Data
						if (result != null && result.containsKey(userDeptRole.getUserId())) {
							Map<String, String> userData = (Map<String, String>) result.get(userDeptRole.getUserId());
							pUserInfo.setEmailId(userData.get("email"));
							pUserInfo.setFirstName(userData.get("first_name"));
							pUserInfo.setLastName(userData.get("last_name"));
						}

						// Assign RoleInfo
						DeptRoleInfo userDeptRoleInfo = deptRoleMap.get(userDeptRole.getDeptRoleId());
						pUserInfo.setRoleInfo(userDeptRoleInfo);
						if ("ADMIN".equalsIgnoreCase(userDeptRoleInfo.getRoleName())) {
							deptInfo.addAdminUser(userDeptRole);
						}

						if (userDeptRole.getIsBlocked()) {
							deptInfo.addBlockedUser(pUserInfo);
						} else if (userDeptRole.getIsActive()) {
							deptInfo.addActiveUser(pUserInfo);
						} else {
							deptInfo.addInActiveUser(pUserInfo);
						}
					}
				}
			}

			logger.info("enrichDepartmentInfo: " + deptInfo);
			return deptInfo;
		}

		return null;
	}

	private List<DepartmentInfo> enrichDepartmentInfo(List<Department> depts, boolean enrichData) {
		List<DepartmentInfo> deptInfoList = new ArrayList<DepartmentInfo>();
		for (Department dept : depts) {
			try {
				deptInfoList.add(enrichDepartmentInfo(dept, false, enrichData));
			} catch (Exception e) {
				logger.error(e);
			}
		}

		if (deptInfoList.isEmpty()) {
			return Collections.emptyList();
		} else {
			return deptInfoList;
		}
	}

	private DeptTypeInfo enrichDepartmentTypeInfo(Integer deptTypeId) {
		DeptTypeInfo deptTypeInfo = null;

		Optional<DepartmentType> dType = deptTypeRepo.findById(deptTypeId);
		if (dType.isPresent()) {
			deptTypeInfo = new DeptTypeInfo();
			deptTypeInfo.setId(dType.get().getId());
			deptTypeInfo.setDeptType(dType.get().getDeptType());
			deptTypeInfo.setDeptSubType(dType.get().getDeptSubType());
			deptTypeInfo.setDescription(dType.get().getDescription());
			return deptTypeInfo;
		}
		return null;
	}

	private UserDepartmentInfo enrichUserDepartment(UserDepartmentRole userDeptRole) {
		UserDepartmentInfo deptInfo = new UserDepartmentInfo();
		deptInfo.setUserId(userDeptRole.getUserId());
		deptInfo.setIsActive(userDeptRole.getIsActive());
		deptInfo.setIsBlocked(userDeptRole.getIsBlocked());

		// Enrich Department Info
		deptInfo.setDeptInfo(enrichDepartmentInfo(userDeptRole.getDeptId(), false, false));

		// Enrich Department Role Info
		deptInfo.setDeptRoleInfo(enrichUserDeptRole(deptRoleRepo.findById(userDeptRole.getDeptRoleId())));

		return deptInfo;
	}

	private List<UserDepartmentInfo> enrichUserDepartments(List<UserDepartmentRole> userRoles) {
		List<UserDepartmentInfo> userDeptInfoList = new ArrayList<UserDepartmentInfo>();

		if (!DataValidator.isCollectionEmpty(userRoles)) {
			for (UserDepartmentRole userRole : userRoles) {
				userDeptInfoList.add(enrichUserDepartment(userRole));
			}
		} else {
			logger.info("There are no values for given UserId");
		}

		if (userDeptInfoList.isEmpty()) {
			return Collections.emptyList();
		} else {
			return userDeptInfoList;
		}
	}

	private DeptRoleInfo enrichUserDeptRole(Optional<DepartmentRole> deptRole) {
		if (deptRole.isPresent()) {
			DeptRoleInfo deptRoleInfo = new DeptRoleInfo();
			deptRoleInfo.setDeptRoleId(deptRole.get().getId());
			Optional<Role> role = roleRepo.findById(deptRole.get().getRoleId());
			if (role.isPresent()) {
				deptRoleInfo.setRoleName(role.get().getRoleName());
				deptRoleInfo.setDescritpion(role.get().getDescription());
			}
			return deptRoleInfo;
		}
		return null;
	}

	private boolean validateDepartmentInfo(DepartmentInfo deptInfo) {
		return (deptInfo != null) && !DataValidator.isStringEmpty(deptInfo.getDeptName())
				&& !DataValidator.isStringEmpty(deptInfo.getRootOrg());
	}

	private boolean validateDepartmentTypeInfo(DeptTypeInfo deptTypeInfo) {
		return (deptTypeInfo != null) && !DataValidator.isStringEmpty(deptTypeInfo.getDeptType())
				&& !DataValidator.isStringEmpty(deptTypeInfo.getDeptSubType());
	}

	private void validateUserDepartmentRole(UserDepartmentRole userDeptRole) throws Exception {
		// Check User exists
		if (!userUtilService.validateUser("igot", userDeptRole.getUserId())) {
			throw new Exception("Invalid UserId.");
		}

		// Check department exist
		if (!deptRepo.findById(userDeptRole.getDeptId()).isPresent()) {
			throw new Exception("Invalid Department");
		}

		// Check DeptRole exist
		DepartmentRole deptRole = deptRoleRepo.findById(userDeptRole.getDeptRoleId()).get();
		if (deptRole != null) {
			if (deptRole.getDeptId() != userDeptRole.getDeptId()) {
				throw new Exception("Invalid Request. Department and DepartmentRoleId doesn't match.");
			}
		} else {
			throw new Exception("Invalid Department Role Id");
		}
	}
}
