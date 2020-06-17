package com.infosys.lex.userroles.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.AccessForbidenError;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.userroles.bodhi.repo.UserRolesRepository;
import com.infosys.lex.userroles.entities.UserRoles;
import com.infosys.lex.userroles.entities.UserRolesKey;
import com.infosys.lex.userroles.postgres.entity.UserRole;
import com.infosys.lex.userroles.postgres.entity.UserRoleDescription;
import com.infosys.lex.userroles.postgres.entity.UserRolePrimaryKey;
import com.infosys.lex.userroles.postgres.repo.UserRoleDescriptionRepo;
import com.infosys.lex.userroles.postgres.repo.UserRolesRepo;

@Service
public class UserRolesServiceImpl implements UserRolesService {

	@Autowired
	UserRolesRepository userRepo;

	@Autowired
	UserRoleDescriptionRepo userRoleDescriptionRepo;

	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	UserRolesRepo userRolesRepo;

	public static final String DEFAULT_USER = "defaultuser";

	public static final String MASTER_USER = "masteruser";

	@Override
	public Set<String> getUserRoles(String rootOrg, String userId) {

		if (!(userId.equals(MASTER_USER) || userUtilService.validateUser(rootOrg, userId))) {
			throw new BadRequestException("Invalid User: " + userId);
		}

		Set<String> users = new HashSet<String>();
		Set<String> returnList = new HashSet<String>();
		users.add(userId);
		users.add(DEFAULT_USER);
		List<UserRoles> allUsers = new ArrayList<UserRoles>();
		allUsers = userRepo.findById(rootOrg, users);
		UserRoles defaultUser = new UserRoles();
		UserRoles userbyId = new UserRoles();
		if (allUsers.size() == 2) {
			defaultUser = allUsers.get(0);
			userbyId = allUsers.get(1);
			if (defaultUser.getRoles() == null) {
				throw new ApplicationLogicError("Default User does not have proper roles");
			}
			returnList.addAll(defaultUser.getRoles());
			returnList.addAll(userbyId.getRoles());
			return returnList;
		} else if (allUsers.size() == 1) {
			defaultUser = allUsers.get(0);
			returnList.addAll(defaultUser.getRoles());
			return returnList;
		} else {
			throw new ApplicationLogicError("Default User Roles Not Found in DB");
		}
	}

	@Override
	public void removeRoles(String rootOrg, String userId, List<String> userRole) {
		UserRoles user = new UserRoles();

		if (!(userId.equals(MASTER_USER) || userUtilService.validateUser(rootOrg, userId))) {
			throw new BadRequestException("Invalid User: " + userId);
		}
		user = userRepo.findByUserRolesKeyRootOrgAndUserRolesKeyUserId(rootOrg, userId);
		if (user != null) {
			UserRoles userToBeRemoved = new UserRoles();
			UserRolesKey userRolesKey = new UserRolesKey();
			userRolesKey.setRootOrg(rootOrg);
			userRolesKey.setUserId(userId);
			userToBeRemoved.setUserRolesKey(userRolesKey);
			Set<String> rolesToBeRemoved = new HashSet<String>();
			Set<String> roleList = new HashSet<String>();
			roleList = user.getRoles();
			for (String tableString : userRole) {
				if (roleList.contains(tableString)) {
					rolesToBeRemoved.add(tableString);
				} else {
					throw new InvalidDataInputException("no role access found for this user ");
				}
			}
			Set<String> rolesToBeUpdated = new HashSet<String>();
			for (String stringTable : roleList) {
				if (!rolesToBeRemoved.contains(stringTable)) {
					rolesToBeUpdated.add(stringTable);
				}
			}
			userToBeRemoved.setRoles(rolesToBeUpdated);
			if (!rolesToBeUpdated.isEmpty()) {
				userRepo.save(userToBeRemoved);
			} else {
				userRepo.delete(userToBeRemoved);
			}

		} else {
			throw new InvalidDataInputException("User doesnot exist");
		}
	}

	@Override
	public void addRoles(String rootOrg, String userId, List<String> userRole) {
		Set<String> rolesToBeAdded = new HashSet<String>();
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User: " + userId);
		}
		UserRoles user = new UserRoles();
		if (userId == null || userId.isEmpty())
			throw new InvalidDataInputException("Enter a valid User id");
		user = userRepo.findByUserRolesKeyRootOrgAndUserRolesKeyUserId(rootOrg, userId);
//		default roles
		if (user == null) {
			UserRoles defaultUser = new UserRoles();
			defaultUser = userRepo.findByUserRolesKeyRootOrgAndUserRolesKeyUserId(rootOrg, DEFAULT_USER);
			if (defaultUser == null) {
				throw new ApplicationLogicError("Default User Roles Not Found in DB");
			}
			rolesToBeAdded.addAll(defaultUser.getRoles());
		} else {
			for (String fromTable : user.getRoles()) {
				rolesToBeAdded.add(fromTable);
			}
		}
//		Adding role from the list through ui
		for (String eachRole : userRole) {
			rolesToBeAdded.add(eachRole.toLowerCase().trim());
		}
//		Creating a new User role to be added in cassandra
		UserRoles newUser = new UserRoles();
		UserRolesKey userRolesKey = new UserRolesKey();
		userRolesKey.setRootOrg(rootOrg);
		userRolesKey.setUserId(userId);
		newUser.setUserRolesKey(userRolesKey);
		newUser.setRoles((rolesToBeAdded));
		userRepo.save(newUser);
//		-------------Master User Role------------
		UserRoles masterUser = new UserRoles();
		masterUser = userRepo.findByUserRolesKeyRootOrgAndUserRolesKeyUserId(rootOrg, MASTER_USER);
		if (masterUser == null) {
			UserRoles newMaster = new UserRoles();
			UserRolesKey masterKey = new UserRolesKey();
			masterKey.setRootOrg(rootOrg);
			masterKey.setUserId(MASTER_USER);
			newMaster.setUserRolesKey(masterKey);
			newMaster.setRoles(rolesToBeAdded);
			userRepo.save(newMaster);
		} else {
			for (String interest : userRole) {
				masterUser.getRoles().add(interest);
			}
			userRepo.save(masterUser);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.infosys.lex.userroles.service.UserRolesService#getUserRolesByUserId(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getUserRolesByUserId(String rootOrg, String userId) {

		// validate userId
		if (!(userUtilService.validateUser(rootOrg, userId))) {
			throw new BadRequestException("Invalid User: " + userId);
		}

		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<String> userRolesList = new ArrayList<>();
		List<String> defaultRolesList = new ArrayList<>();

		// fetch roles of user and defaultuser
		List<UserRole> allUsers = userRolesRepo.findByUserRolePrimaryKeyRootOrgAndUserRolePrimaryKeyUserIdIn(rootOrg,
				Arrays.asList(userId, DEFAULT_USER));

		// make two lists of userRoles and default roles
		for (UserRole userRecord : allUsers) {
			if (userRecord.getUserRolePrimaryKey().getUserId().equals(userId)) {
				userRolesList.add(userRecord.getUserRolePrimaryKey().getRole());
			} else {
				defaultRolesList.add(userRecord.getUserRolePrimaryKey().getRole());
			}
		}

		returnMap.put("default_roles", defaultRolesList);
		returnMap.put("user_roles", userRolesList);

		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.infosys.lex.userroles.service.UserRolesService#getUserDetailsByRole(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getUserDetailsByRole(String rootOrg, String role, Integer prevPageNo, Integer pageSize){

		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("users", new ArrayList<>());

		Slice<UserRole> slicedUserRoles;
		List<String> allUserIds = new ArrayList<>();
		Integer pageNo = prevPageNo + 1;
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		slicedUserRoles = userRolesRepo
				.findByUserRolePrimaryKeyRootOrgAndUserRolePrimaryKeyRoleAndUserRolePrimaryKeyUserIdNot(rootOrg, role,
						MASTER_USER, pageable);

		List<UserRole> usersList = slicedUserRoles.getContent();
		// if no user has that role return map

		if (usersList.isEmpty()) {
			return returnMap;
		}

		// get all the userIds in a list
		for (UserRole userRecord : usersList) {
			String recordId = userRecord.getUserRolePrimaryKey().getUserId();
			if (!recordId.equals(DEFAULT_USER) && !recordId.equals(MASTER_USER)) {
				allUserIds.add(recordId);
			} else if (recordId.equals(DEFAULT_USER)) {
				returnMap.put("users", Arrays.asList(DEFAULT_USER));

			}

		}

		returnMap.put("pageNo", pageNo);
		returnMap.put("hasNextPage", slicedUserRoles.hasNext());
		// if no user has that role return
		if (allUserIds.isEmpty()) {
			return returnMap;
		}

		// add user details of that user
		Map<String, Object> userData = this.getMultipleUserData(rootOrg, allUserIds);
		returnMap.put("users", userData.values());

		return returnMap;
	}

	// add user details of a userId
	private Map<String, Object> getMultipleUserData(String rootOrg, List<String> uuids) {

		Map<String, Object> result = userUtilService.getUsersDataFromUserIds(rootOrg, uuids,
				new ArrayList<>(Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.EMAIL,
						PIDConstants.SOURCE_ID, PIDConstants.DEPARTMENT_NAME)));

		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.infosys.lex.userroles.service.UserRolesService#addUserRoles(java.lang.
	 * String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void addUserRoles(String rootOrg, String userId, String role, Map<String, Object> userRoleMap) {

		// format the user roles data

		this.formatUserRoleMap(rootOrg, userId, role, userRoleMap);
		List<UserRole> userRoles = new ArrayList<>();
		List<String> userIds = new ArrayList<>();
		List<String> rolesToBeAdded = new ArrayList<>();

		// make different lists of allUsers and all rolesToBeAdded
		for (Map<String, Object> userRole : (List<Map<String, Object>>) userRoleMap.get("user_roles")) {
			if (!userIds.contains(userRole.get("user_id").toString())) {
				userIds.add(userRole.get("user_id").toString());
			}
			if (!rolesToBeAdded.contains(userRole.get("role").toString())) {
				rolesToBeAdded.add(userRole.get("role").toString());
			}

		}
		// check for valid users
		Map<String, Object> validOrInvalidUsers = userUtilService.validateUsers(rootOrg, userIds);
		List<String> validUsers = (List<String>) validOrInvalidUsers.get("valid_users");

		if (validUsers.isEmpty()) {
			throw new InvalidDataInputException("invalid.users");
		}

		List<UserRole> defaultAndMasterRoles = userRolesRepo
				.findByUserRolePrimaryKeyRootOrgAndUserRolePrimaryKeyUserIdIn(rootOrg,
						Arrays.asList(DEFAULT_USER, MASTER_USER));

		if (defaultAndMasterRoles.isEmpty()) {
			throw new InvalidDataInputException("defaultRoles.notPresent");
		}

		List<String> masterRoles = new ArrayList<>();
		// remove default roles from the list to added
		for (UserRole defaultRole : defaultAndMasterRoles) {
			if (defaultRole.getUserRolePrimaryKey().getUserId().equals(DEFAULT_USER)) {
				rolesToBeAdded.remove(defaultRole.getUserRolePrimaryKey().getRole());
			} else if (defaultRole.getUserRolePrimaryKey().getUserId().equals(MASTER_USER)) {
				masterRoles.add(defaultRole.getUserRolePrimaryKey().getRole());
			}
		}

		if (masterRoles.isEmpty()) {
			throw new InvalidDataInputException("invalid.masterRoles");
		}

		List<String> validRoles = new ArrayList<>();
		for (String roles : rolesToBeAdded) {
			if (masterRoles.contains(roles)) {
				validRoles.add(roles);
			}
		}

		if (validRoles.isEmpty()) {
			throw new InvalidDataInputException("invalid.roles");
		}

		// check that only admin can give role to a user
		this.checkForAcces(rootOrg, userRoleMap);

		for (Map<String, Object> userRole : (List<Map<String, Object>>) userRoleMap.get("user_roles")) {
			if (validUsers.contains(userRole.get("user_id")) && validRoles.contains(userRole.get("role"))) {
				Date dateCreatedOn = new Date();
				Timestamp timeCreatedOn = new Timestamp(dateCreatedOn.getTime());
				// add valid user record
				UserRole userRecord = new UserRole(new UserRolePrimaryKey(rootOrg, (String) userRole.get("user_id"),
						(String) userRole.get("role")), timeCreatedOn, (String) userRoleMap.get("action_by"));

				userRoles.add(userRecord);
			}
		}

		userRolesRepo.saveAll(userRoles);
	}

	// check if a user can give role to other user
	private void checkForAcces(String rootOrg, Map<String, Object> userRoleMap) {

		if (!userRoleMap.containsKey("action_by") || userRoleMap.get("action_by") == null) {
			throw new InvalidDataInputException("invalid.actionBy");
		}

		String userId = userRoleMap.get("action_by").toString();
		List<UserRole> userRoles = userRolesRepo.findByUserRolePrimaryKeyRootOrgAndUserRolePrimaryKeyUserId(rootOrg,
				userId);

		List<String> roles = new ArrayList<String>();
		for (UserRole userRole : userRoles) {
			roles.add(userRole.getUserRolePrimaryKey().getRole());
		}

		if (!roles.contains("admin"))
			throw new AccessForbidenError("access.forbidden");
	}

	@SuppressWarnings("unchecked")
	private void formatUserRoleMap(String rootOrg, String userId, String role, Map<String, Object> userRoleMap) {

		if (userRoleMap.containsKey("user_roles") && userRoleMap.get("user_roles") != null) {
			return;
		}

		List<String> userIds = new ArrayList<>();
		List<String> roles = new ArrayList<>();
		if (!userId.equals(DEFAULT_USER)) {
			userIds.add(userId);
		} else if (userRoleMap.containsKey("users") && userRoleMap.get("users") != null) {
			userIds.addAll((List<String>) userRoleMap.get("users"));
		}

		if (!role.equals("defaultrole")) {
			roles.add(role);
		} else if (userRoleMap.containsKey("roles") && userRoleMap.get("roles") != null) {
			roles.addAll((List<String>) userRoleMap.get("roles"));
		}

		List<Map<String, Object>> userRoles = new ArrayList<>();
		for (String user : userIds) {
			for (String userRole : roles) {
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("user_id", user);
				userMap.put("role", userRole);
				userRoles.add(userMap);
			}
		}

		userRoleMap.put("user_roles", userRoles);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.infosys.lex.userroles.service.UserRolesService#removeUserRoles(java.lang.
	 * String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void removeUserRoles(String rootOrg, String userId, String role, Map<String, Object> userRoleMap) {

		this.formatUserRoleMap(rootOrg, userId, role, userRoleMap);

		this.checkForAcces(rootOrg, userRoleMap);
		//
		List<UserRole> userRecord = new ArrayList<>();
		for (Map<String, Object> userRole : (List<Map<String, Object>>) userRoleMap.get("user_roles")) {
			if (!userRole.get("user_id").equals(DEFAULT_USER)) {
				UserRolePrimaryKey userRoleKey = new UserRolePrimaryKey(rootOrg, (String) userRole.get("user_id"),
						(String) userRole.get("role"));

				UserRole user = new UserRole(userRoleKey, null, null);
				userRecord.add(user);
			}

		}

		userRolesRepo.deleteAll(userRecord);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.infosys.lex.userroles.service.UserRolesService#getAllRoles(java.lang.
	 * String)
	 */
	@Override
	public Map<String, Object> getAllRoles(String rootOrg, String langCode) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<String> masterRolesList = new ArrayList<>();
		List<String> defaultRolesList = new ArrayList<>();

		// fetch roles of masteruser and defaultuser
		List<UserRole> masterAndDefaultUserRoles = userRolesRepo
				.findByUserRolePrimaryKeyRootOrgAndUserRolePrimaryKeyUserIdIn(rootOrg,
						Arrays.asList(MASTER_USER, DEFAULT_USER));

		// make two lists of master roles and default roles
		for (UserRole userRecord : masterAndDefaultUserRoles) {
			if (userRecord.getUserRolePrimaryKey().getUserId().equals(MASTER_USER)) {
				masterRolesList.add(userRecord.getUserRolePrimaryKey().getRole());
			} else {
				defaultRolesList.add(userRecord.getUserRolePrimaryKey().getRole());
			}
		}

		List<UserRoleDescription> rolesDescription = userRoleDescriptionRepo
				.findByKeyRoleInAndKeyLanguage(masterRolesList, langCode);

		masterRolesList.removeAll(defaultRolesList);

		List<Map<String, Object>> defaultRolesData = new ArrayList<>();
		List<Map<String, Object>> masterRolesData = new ArrayList<>();

		for (String role : defaultRolesList) {
			Map<String, Object> roleMap = new HashMap<>();
			roleMap.put("role", role);
			roleMap.put("description", "");
			for (UserRoleDescription roleDescription : rolesDescription) {
				if (roleDescription.getKey().getRole().equals(role)) {
					roleMap.put("description", roleDescription.getDescription());
					break;
				}
			}
			defaultRolesData.add(roleMap);
		}
		for (String role : masterRolesList) {
			Map<String, Object> roleMap = new HashMap<>();
			roleMap.put("role", role);
			roleMap.put("description", "");
			for (UserRoleDescription roleDescription : rolesDescription) {
				if (roleDescription.getKey().getRole().equals(role)) {
					roleMap.put("description", roleDescription.getDescription());
					break;
				}
			}
			masterRolesData.add(roleMap);
		}

		returnMap.put("default_roles", defaultRolesData);
		returnMap.put("master_roles", masterRolesData);

		return returnMap;
	}

	@Override
	public void addOrDeleteUserRoles(String rootOrg, String userId, String role, Map<String, Object> userRoleMap){

		String operation = userRoleMap.get("operation").toString();

		if (operation == null || operation.isEmpty()) {
			throw new InvalidDataInputException("invalid.operation");
		}

		if (operation.equalsIgnoreCase("remove")) {
			this.removeUserRoles(rootOrg, userId, role, userRoleMap);
		} else if (operation.equalsIgnoreCase("add")) {
			this.addUserRoles(rootOrg, userId, role, userRoleMap);
		} else {
			throw new InvalidDataInputException("invalid.operation");
		}

	}

}
