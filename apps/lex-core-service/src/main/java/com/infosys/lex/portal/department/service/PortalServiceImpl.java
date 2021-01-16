package com.infosys.lex.portal.department.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.DataValidator;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.logger.LexLogger;
import com.infosys.lex.portal.department.PortalConstants;
import com.infosys.lex.portal.department.dto.Department;
import com.infosys.lex.portal.department.dto.DepartmentRole;
import com.infosys.lex.portal.department.dto.DepartmentType;
import com.infosys.lex.portal.department.dto.Role;
import com.infosys.lex.portal.department.dto.UserDepartmentRole;
import com.infosys.lex.portal.department.model.DepartmentInfo;
import com.infosys.lex.portal.department.model.DeptTypeInfo;
import com.infosys.lex.portal.department.model.PortalUserInfo;
import com.infosys.lex.portal.department.model.SearchUserInfo;
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

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	LexServerProperties serverConfig;
	
	private static final String ROOT_ORG_CONST = "rootOrg";
	private static final String ORG_CONST = "org";

	@Override
	public List<DepartmentInfo> getAllDepartments() {
		return enrichDepartmentInfo(deptRepo.findAll(), true);
	}

	@Override
	public DepartmentInfo getDepartmentById(Integer deptId, boolean isUserInfoRequired) {
		return enrichDepartmentInfo(deptId, isUserInfoRequired, true);
	}

	@Override
	public DepartmentInfo getMyDepartment(String deptType, String userId, boolean isUserInfoRequired) throws Exception {
		return enrichDepartmentInfo(getMyActiveDepartment(deptType, userId), isUserInfoRequired, true);
	}

	private Department getMyActiveDepartment(String strDeptType, String userId) throws Exception {
		List<UserDepartmentRole> userList = userDepartmentRoleRepo.findAllByUserIdAndIsActiveAndIsBlocked(userId, true,
				false);
		if (DataValidator.isCollectionEmpty(userList)) {
			throw new Exception("No records exist for UserId: " + userId);
		}
		List<Integer> deptIds = userList.stream().map(i -> i.getDeptId()).collect(Collectors.toList());
		logger.info("List of User Records -> " + userList.size() + ", DeptIds: " + deptIds.toString());

		Department myDept = null;
		Iterable<Department> deptList = deptRepo.findAllById(deptIds);
		for (Department dept : deptList) {
			Iterable<DepartmentType> deptTypeList = deptTypeRepo.findAllById(Arrays.asList(dept.getDeptTypeIds()));
			for (DepartmentType deptType : deptTypeList) {
				if (deptType.getDeptType().equalsIgnoreCase(strDeptType)) {
					if (myDept != null) {
						throw new Exception("More than one Department is available for DeptType: " + strDeptType);
					} else {
						myDept = dept;
					}
				}
			}
		}
		return myDept;
	}

	@Override
	public DepartmentInfo getMyDepartment(String userId) throws Exception {
		return getMyDepartment("MDO", userId, true);
	}

	@Override
	public List<Department> getDepartmentsByUserId(String userId) {
		return null;
	}

	@Override
	public DepartmentInfo addDepartment(String userId, String userRoleName, DepartmentInfo deptInfo) throws Exception {
		validateDepartmentInfo(deptInfo);

		if (deptInfo.getDeptTypeIds() == null) {
			validateDepartmentTypeInfo(deptInfo.getDeptTypeInfos());

			List<Integer> deptTypeIds = new ArrayList<Integer>();
			// We need to make sure this DeptType & subDeptType exist.
			for (DeptTypeInfo deptTypeInfo : deptInfo.getDeptTypeInfos()) {
				DepartmentType dType = deptTypeRepo.findByDeptTypeAndDeptSubType(deptTypeInfo.getDeptType(),
						deptTypeInfo.getDeptSubType());
				if (dType == null) {
					DepartmentType deptType = new DepartmentType();
					deptType.setDeptType(deptTypeInfo.getDeptType());
					deptType.setDeptSubType(deptTypeInfo.getDeptSubType());
					deptType.setDescription(deptTypeInfo.getDescription());
					dType = deptTypeRepo.save(deptType);
//				} else {
//					if (!dType.getDeptType().equalsIgnoreCase(strDeptType)) {
//						throw new Exception("DepartmentType value is different than the Access Level.");
//					}
				}
				deptTypeIds.add(dType.getId());
			}
			deptInfo.setDeptTypeIds(deptTypeIds.toArray(new Integer[deptTypeIds.size()]));
		} else {
			validateDepartmentTypeInfo(deptInfo.getDeptTypeIds());
		}

		// Department is Valid -- add this Department
		Department dept = Department.clone(deptInfo);
		dept.setCreationDate(java.time.Instant.now().toEpochMilli());
		dept.setCreatedBy(userId);
		dept = deptRepo.save(dept);

		Iterator<Role> roles = roleRepo.findAll().iterator();
		List<Integer> roleIds = new ArrayList<Integer>();
		while (roles.hasNext()) {
			Role r = roles.next();
			if (userRoleName.equalsIgnoreCase(r.getRoleName())) {
				roleIds.add(r.getId());
			}
		}

		if (!DataValidator.isCollectionEmpty(deptInfo.getAdminUserList())) { // We have Few admin Users to assign to
			for (UserDepartmentRole userDeptRole : deptInfo.getAdminUserList()) {
				try {
					userDeptRole.setDeptId(dept.getDeptId());
					userDeptRole.setRoleIds(roleIds.toArray(new Integer[roleIds.size()]));
					userDeptRole.setIsActive(true);
					userDeptRole.setIsBlocked(false);
					userDeptRole = userDepartmentRoleRepo.save(userDeptRole);
				} catch (Exception e) {
					logger.error(e); // TODO -- Need to decide what to do with this failed error...
				}
			}
		}
		return enrichDepartmentInfo(dept, false, true);
	}

	@Override
	public DepartmentInfo updateDepartment(DepartmentInfo deptInfo) throws Exception {
		Department existingDept = deptRepo.findById(deptInfo.getId()).get();
		logger.info("Updating Department record -> " + existingDept);
		if (existingDept != null) {
			existingDept.setDescription(deptInfo.getDescription());
			existingDept.setHeadquarters(deptInfo.getHeadquarters());
			existingDept.setLogo(deptInfo.getLogo());
			existingDept.setDeptName(deptInfo.getDeptName());
			existingDept.setDeptTypeIds(deptInfo.getDeptTypeIds());
			logger.info("Updating Department with existing record -> " + existingDept);

			existingDept = deptRepo.save(existingDept);
			return enrichDepartmentInfo(existingDept, false, true);
		} else {
			throw new Exception("Failed to find Department for Id: " + deptInfo.getId());
		}
	}

	@Override
	public UserDepartmentInfo addUserRoleInDepartment(UserDepartmentRole userDeptRole, String wid, String rootOrg, String org) throws Exception {
		validateUserDepartmentRole(userDeptRole);
		UserDepartmentRole existingRecord = userDepartmentRoleRepo.findByUserIdAndDeptId(userDeptRole.getUserId(),
				userDeptRole.getDeptId());
		if (existingRecord != null) {
			throw new Exception("Record already exist for UserId: '" + userDeptRole.getUserId() + ", RoleName: "
					+ userDeptRole.getRoles());
		}

		existingRecord = userDeptRole;
		Iterator<Role> roles = roleRepo.findAll().iterator();
		Set<Integer> roleIds = new HashSet<Integer>();

		for (String r : userDeptRole.getRoles()) {
			while (roles.hasNext()) {
				Role role = roles.next();
				if (role.getRoleName().equalsIgnoreCase(r)) {
					roleIds.add(role.getId());
					continue;
				}
			}
		}

		int prevDeptId = 0;
		existingRecord.setIsActive(userDeptRole.getIsActive());
		existingRecord.setIsBlocked(userDeptRole.getIsBlocked());
		prevDeptId = existingRecord.getDeptId();
		existingRecord.setDeptId(userDeptRole.getDeptId());
		existingRecord.setRoleIds(roleIds.stream().collect(Collectors.toList()).toArray(new Integer[roleIds.size()]));

		UserDepartmentInfo userDeptInfo = enrichUserDepartment(userDepartmentRoleRepo.save(existingRecord));

		// Update the WF history and OpenSaber profile for department details
		HashMap<String, Object> request = new HashMap<>();
		request.put("userId", userDeptInfo.getUserId());
		request.put("applicationId", userDeptInfo.getUserId());
		request.put("actorUserId", wid);
		request.put("serviceName", "profile");
		request.put("comment", "Updating Department Details.");
		ArrayList<HashMap<String, Object>> fieldValues = new ArrayList<>();
		HashMap<String, Object> fieldValue = new HashMap<>();
		fieldValue.put("fieldKey", "employmentDetails");

		// Try to get existing dept if available
		String prevDeptName = "";
		if (prevDeptId != 0) {
			Department prevDept = deptRepo.findById(prevDeptId).get();
			if (prevDept != null) {
				prevDeptName = prevDept.getDeptName();
			}
		}
		
		HashMap<String, Object> fromValue = new HashMap<>();
		fromValue.put("departmentName", prevDeptName);
		fieldValue.put("fromValue", fromValue);
		HashMap<String, Object> toValue = new HashMap<>();
		toValue.put("departmentName", userDeptInfo.getDeptInfo().getDeptName());
		fieldValue.put("toValue", toValue);
		fieldValues.add(fieldValue);
		request.put("updateFieldValues", fieldValues);

		HttpHeaders headers = new HttpHeaders();
		headers.set(ROOT_ORG_CONST, rootOrg);
		headers.set(ORG_CONST, org);
		HttpEntity<Object> entity = new HttpEntity<>(request, headers);
		restTemplate.postForObject(serverConfig.getWfServiceHost() + serverConfig.getWfServicePath(), entity,
				Map.class);
		return userDeptInfo;
	}

	public UserDepartmentInfo updateUserRoleInDepartment(UserDepartmentRole userDeptRole, String wid, String rootOrg, String org) throws Exception {
		validateUserDepartmentRole(userDeptRole);
		UserDepartmentRole existingRecord = userDepartmentRoleRepo.findByUserIdAndDeptId(userDeptRole.getUserId(),
				userDeptRole.getDeptId());
		if (existingRecord == null) {
			throw new Exception("Failed to identify User details for UserId: " + userDeptRole.getUserId());
		}
		Iterator<Role> roles = roleRepo.findAll().iterator();
		Set<Integer> roleIds = new HashSet<Integer>();

		for (String r : userDeptRole.getRoles()) {
			while (roles.hasNext()) {
				Role role = roles.next();
				if (role.getRoleName().equalsIgnoreCase(r)) {
					roleIds.add(role.getId());
					continue;
				}
			}
		}

		int prevDeptId = 0;
		existingRecord.setIsActive(userDeptRole.getIsActive());
		existingRecord.setIsBlocked(userDeptRole.getIsBlocked());
		prevDeptId = existingRecord.getDeptId();
		existingRecord.setDeptId(userDeptRole.getDeptId());
		existingRecord.setRoleIds(roleIds.stream().collect(Collectors.toList()).toArray(new Integer[roleIds.size()]));

		UserDepartmentInfo userDeptInfo = enrichUserDepartment(userDepartmentRoleRepo.save(existingRecord));

		// Update the WF history and OpenSaber profile for department details
		HashMap<String, Object> request = new HashMap<>();
		request.put("userId", userDeptInfo.getUserId());
		request.put("applicationId", userDeptInfo.getUserId());
		request.put("actorUserId", wid);
		request.put("serviceName", "profile");
		request.put("comment", "Updating Department Details.");
		ArrayList<HashMap<String, Object>> fieldValues = new ArrayList<>();
		HashMap<String, Object> fieldValue = new HashMap<>();
		fieldValue.put("fieldKey", "employmentDetails");

		// Try to get existing dept if available
		String prevDeptName = "";
		if (prevDeptId != 0) {
			Department prevDept = deptRepo.findById(prevDeptId).get();
			if (prevDept != null) {
				prevDeptName = prevDept.getDeptName();
			}
		}
		HashMap<String, Object> fromValue = new HashMap<>();
		fromValue.put("departmentName", prevDeptName);
		fieldValue.put("fromValue", fromValue);
		HashMap<String, Object> toValue = new HashMap<>();
		toValue.put("departmentName", userDeptInfo.getDeptInfo().getDeptName());
		fieldValue.put("toValue", toValue);
		fieldValues.add(fieldValue);
		request.put("updateFieldValues", fieldValues);

		HttpHeaders headers = new HttpHeaders();
		headers.set(ROOT_ORG_CONST, rootOrg);
		headers.set(ORG_CONST, org);
		HttpEntity<Object> entity = new HttpEntity<>(request, headers);
		restTemplate.postForObject(serverConfig.getWfServiceHost() + serverConfig.getWfServicePath(), entity,
				Map.class);
		return userDeptInfo;
	}

	@Override
	public Boolean checkAdminPrivilage(Integer deptId, String userId) throws Exception {
		UserDepartmentInfo userDeptInfoList = enrichUserDepartment(
				userDepartmentRoleRepo.findByUserIdAndDeptId(userId, deptId));
		Iterator<Role> roles = userDeptInfoList.getRoleInfo().iterator();
		while (roles.hasNext()) {
			Role r = roles.next();
			if ("MDO ADMIN".equalsIgnoreCase(r.getRoleName())) {
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

			List<Department> depts = deptRepo.findAllByIdIn(deptTypeIds);
			List<Integer> deptIds = new ArrayList<Integer>();
			if (!DataValidator.isCollectionEmpty(depts)) {
				deptIds = depts.stream().map(i -> i.getDeptId()).collect(Collectors.toList());
			}

			Role role = roleRepo.findRoleByRoleName("MDO ADMIN");

			List<UserDepartmentRole> userDeptRoles = userDepartmentRoleRepo.findAllByUserIdAndDeptId(userId, deptIds);
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
		return null;
//		UserDepartmentRole userDept = userDepartmentRoleRepo.findByUserId(userId);
//		return getDepartmentById(userDept.getDeptId(), isUserInfoRequired);
	}

	@Override
	public boolean isAdmin(String strDeptType, String roleName, String userId) {
		return isAdmin(strDeptType, roleName, userId, -1);
	}

	@Override
	public boolean isAdmin(String strDeptType, String roleName, String userId, Integer deptId) {
		List<UserDepartmentRole> userDeptRoleList = userDepartmentRoleRepo
				.findAllByUserIdAndIsActiveAndIsBlocked(userId, true, false);
		if (!DataValidator.isCollectionEmpty(userDeptRoleList)) {
			for (UserDepartmentRole userDeptRole : userDeptRoleList) {
				if (!userDeptRole.getIsActive() || userDeptRole.getIsBlocked()) {
					continue;
				}
				// Get Roles
				Iterable<Role> roles = roleRepo.findAllById(Arrays.asList(userDeptRole.getRoleIds()));
				if (!DataValidator.isCollectionEmpty(roles)) {
					for (Role role : roles) {
						if (role.getRoleName().contains(roleName)) {
							// Just check this department type is equal to given roleName
							Department dept = deptRepo.findById(userDeptRole.getDeptId()).get();
							if (dept != null) {
								if (deptId != -1 && deptId != dept.getDeptId()) {
									// If the department Id didn't match, simply continue.
									continue;
								}
								Iterable<DepartmentType> deptTypeList = deptTypeRepo
										.findAllById(Arrays.asList(dept.getDeptTypeIds()));
								if (!DataValidator.isCollectionEmpty(deptTypeList)) {
									for (DepartmentType deptType : deptTypeList) {
										if (deptType.getDeptType().equalsIgnoreCase(strDeptType)) {
											// We have found the expected Department.
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public List<SearchUserInfo> searchUserForRole(Integer deptId, String roleName, String userName) {
		return null;
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
			deptInfo.setDeptTypeIds(dept.getDeptTypeIds());
			deptInfo.setHeadquarters(dept.getHeadquarters());
			deptInfo.setLogo(dept.getLogo());
			deptInfo.setCreationDate(dept.getCreationDate());
			deptInfo.setCreatedBy(dept.getCreatedBy());

			// Get Dept Type Information
			deptInfo.setDeptTypeInfos(enrichDepartmentTypeInfo(dept.getDeptTypeIds()));

			// Get Number of Users in Department
			if (enrichData) {
				List<UserDepartmentRole> userDeptList = userDepartmentRoleRepo.findByDeptId(deptInfo.getId());
				deptInfo.setNoOfUsers(userDeptList == null ? 0 : userDeptList.size());

				// Get Role Informations
				List<Role> roleList = getDepartmentRoles(Arrays.asList(deptInfo.getDeptTypeIds()));
				deptInfo.setRolesInfo(roleList);

				// TODO Current User Roles

				Map<Integer, Role> deptRoleMap = deptInfo.getRolesInfo().stream()
						.collect(Collectors.toMap(Role::getId, roleInfo -> roleInfo));

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
						List<Role> userRoleInfo = new ArrayList<Role>();
						for (Integer roleId : userDeptRole.getRoleIds()) {
							Role r = deptRoleMap.get(roleId);
							userRoleInfo.add(r);
							r.incrementUserCount();
						}
						pUserInfo.setRoleInfo(userRoleInfo);

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
			Collections.sort(deptInfoList);
			return deptInfoList;
		}
	}

	private List<DeptTypeInfo> enrichDepartmentTypeInfo(Integer[] deptTypeId) {
		List<DeptTypeInfo> deptTypeInfoList = new ArrayList<DeptTypeInfo>();

		Iterable<DepartmentType> dTypeList = deptTypeRepo.findAllById(Arrays.asList(deptTypeId));
		if (!DataValidator.isCollectionEmpty(dTypeList)) {
			for (DepartmentType dType : dTypeList) {
				DeptTypeInfo deptTypeInfo = new DeptTypeInfo();
				deptTypeInfo.setId(dType.getId());
				deptTypeInfo.setDeptType(dType.getDeptType());
				deptTypeInfo.setDeptSubType(dType.getDeptSubType());
				deptTypeInfo.setDescription(dType.getDescription());
				deptTypeInfoList.add(deptTypeInfo);
			}
		}
		return deptTypeInfoList;
	}

	private UserDepartmentInfo enrichUserDepartment(UserDepartmentRole userDeptRole) {
		UserDepartmentInfo deptInfo = new UserDepartmentInfo();
		deptInfo.setUserId(userDeptRole.getUserId());
		deptInfo.setIsActive(userDeptRole.getIsActive());
		deptInfo.setIsBlocked(userDeptRole.getIsBlocked());

		// Enrich Department Info
		deptInfo.setDeptInfo(enrichDepartmentInfo(userDeptRole.getDeptId(), false, false));

		// Enrich Department Role Info
		deptInfo.setRoleInfo(roleRepo.findAllById(Arrays.asList(userDeptRole.getRoleIds())));

		return deptInfo;
	}

	private void validateDepartmentInfo(DepartmentInfo deptInfo) throws Exception {
		if (deptRepo.existsByDeptNameIgnoreCase(deptInfo.getDeptName())) {
			throw new Exception(
					"Failed to create Department. Given deptName: '" + deptInfo.getDeptName() + "' already exists");
		}
		boolean isValid = (deptInfo != null) && !DataValidator.isStringEmpty(deptInfo.getDeptName())
				&& !DataValidator.isStringEmpty(deptInfo.getRootOrg());
		if (!isValid) {
			throw new Exception("Failed to create Department. Given Department is null OR RootOrg/DeptName is null");
		}
	}

	private void validateDepartmentTypeInfo(List<DeptTypeInfo> deptTypeInfoList) throws Exception {
		if (!DataValidator.isCollectionEmpty(deptTypeInfoList)) {
			for (DeptTypeInfo deptTypeInfo : deptTypeInfoList) {
				boolean isValid = !DataValidator.isStringEmpty(deptTypeInfo.getDeptType())
						&& !DataValidator.isStringEmpty(deptTypeInfo.getDeptSubType());
				if (!isValid) {
					throw new Exception("Failed to create Department. Given deptType or deptSubType is Empty");
				}
			}
		}
	}

	private void validateDepartmentTypeInfo(Integer[] deptTypeIds) throws Exception {
		for (Integer deptTypeId : deptTypeIds) {
			Optional<DepartmentType> dType = deptTypeRepo.findById(deptTypeId);
			if (!dType.isPresent()) {
				throw new Exception(
						"Failed to create Department. Given deptTypeId: '" + deptTypeId + "' doesn't exist");
			}
		}
	}

	private void validateUserDepartmentRole(UserDepartmentRole userDeptRole) throws Exception {
		// Check User exists
		if (!userUtilService.validateUser("igot", userDeptRole.getUserId())) {
			throw new Exception("Invalid UserId.");
		}

		// Check department exist
		Department dept = deptRepo.findById(userDeptRole.getDeptId()).get();
		if (dept == null) {
			throw new Exception("Invalid Department");
		}

		// Check Role exist
		if (!DataValidator.isCollectionEmpty(userDeptRole.getRoles())) {
			List<Role> roles = roleRepo.findAllByRoleNameIn(userDeptRole.getRoles());
			if (DataValidator.isCollectionEmpty(roles) || roles.size() != userDeptRole.getRoles().size()) {
				throw new Exception("Invalid Role Names Provided");
			}

			List<Role> rolesAvailableInDept = getDepartmentRoles(Arrays.asList(dept.getDeptTypeIds()));

			Set<String> availableRoleNames = rolesAvailableInDept.stream().map(i -> i.getRoleName())
					.collect(Collectors.toSet());
			for (String roleName : userDeptRole.getRoles()) {
				if (!availableRoleNames.contains(roleName)) {
					throw new Exception("Invalid Role Name provided for the Department");
				}
			}
		}
	}

	private List<Role> getDepartmentRoles(List<Integer> deptTypeIdList) {
		Iterable<DepartmentType> deptTypeList = deptTypeRepo.findAllById(deptTypeIdList);
		if (DataValidator.isCollectionEmpty(deptTypeList)) {
			return null;
		}
		Set<String> deptTypeNames = new HashSet<String>();
		deptTypeNames.add("COMMON");
		for (DepartmentType deptType : deptTypeList) {
			deptTypeNames.add(deptType.getDeptType());
		}

		Iterable<DepartmentRole> deptRoleList = deptRoleRepo
				.findAllByDeptTypeIn(deptTypeNames.stream().collect(Collectors.toList()));
		Set<Role> roleList = new HashSet<Role>();
		if (DataValidator.isCollectionEmpty(deptRoleList)) {
			return null;
		}

		for (DepartmentRole deptRole : deptRoleList) {
			Iterable<Role> existingRoles = roleRepo.findAllById(Arrays.asList(deptRole.getRoleIds()));
			for (Role r : existingRoles) {
				roleList.add(r);
			}
		}
		return roleList.stream().collect(Collectors.toList());
	}

	@Override
	public boolean validateCBPUserLogin(String userId) {
		List<UserDepartmentRole> userDeptRoleList = userDepartmentRoleRepo
				.findAllByUserIdAndIsActiveAndIsBlocked(userId, true, false);
		DepartmentRole cbpRole = deptRoleRepo.findByDeptTypeIgnoreCase(PortalConstants.CBP_DEPT_TYPE);
		Set<Integer> cbpRoleIds = new HashSet<Integer>();
		if (cbpRole != null) {
			cbpRoleIds.addAll(Arrays.asList(cbpRole.getRoleIds()));
		}
		if (!DataValidator.isCollectionEmpty(userDeptRoleList)) {
			for (UserDepartmentRole userDeptRole : userDeptRoleList) {
				if (!userDeptRole.getIsActive() || userDeptRole.getIsBlocked()) {
					continue;
				}
				// Just check this department type is "SPV"
				Department dept = deptRepo.findById(userDeptRole.getDeptId()).get();
				if (dept != null) {
					Iterable<DepartmentType> deptTypeList = deptTypeRepo
							.findAllById(Arrays.asList(dept.getDeptTypeIds()));
					if (!DataValidator.isCollectionEmpty(deptTypeList)) {
						for (DepartmentType deptType : deptTypeList) {
							if (deptType.getDeptType().equalsIgnoreCase(PortalConstants.CBP_DEPT_TYPE)) {
								// We have found the expected Department.
								// Let's check user has at least one Role.
								for (Integer uRoleId : userDeptRole.getRoleIds()) {
									if (cbpRoleIds.contains(uRoleId)) {
										return true;
									}
								}
							} else {
								continue;
							}
						}
					}
				}
			}
		}
		return false;
	}
}
