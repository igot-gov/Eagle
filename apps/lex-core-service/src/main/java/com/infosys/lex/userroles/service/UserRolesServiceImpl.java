/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.userroles.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.userroles.bodhi.repo.UserRolesRepository;
import com.infosys.lex.userroles.entities.UserRoles;
import com.infosys.lex.userroles.entities.UserRolesKey;

@Service
public class UserRolesServiceImpl implements UserRolesService {

	@Autowired
	UserRolesRepository userRepo;

	@Autowired
	UserUtilityService userUtilService;

	@Override
	public Set<String> getUserRoles(String rootOrg, String userId) throws Exception {

//		userUtilService.validateAndFetchUser("uuid", userId);

		if (!(userId.equals("masteruser") || userUtilService.validateUser(rootOrg, userId))) {
			throw new BadRequestException("Invalid User: " + userId);
		}

		Set<String> users = new HashSet<String>();
		Set<String> returnList = new HashSet<String>();
		users.add(userId);
		users.add("defaultuser");
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
	public void removeRoles(String rootOrg, String userId, List<String> userRole) throws Exception {
		UserRoles user = new UserRoles();

		if (!(userId.equals("masteruser") || userUtilService.validateUser(rootOrg, userId))) {
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
	public void addRoles(String rootOrg, String userId, List<String> userRole) throws Exception {
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
			defaultUser = userRepo.findByUserRolesKeyRootOrgAndUserRolesKeyUserId(rootOrg, "defaultuser");
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
		masterUser = userRepo.findByUserRolesKeyRootOrgAndUserRolesKeyUserId(rootOrg, "masteruser");
		if (masterUser == null) {
			UserRoles newMaster = new UserRoles();
			UserRolesKey masterKey = new UserRolesKey();
			masterKey.setRootOrg(rootOrg);
			masterKey.setUserId("masteruser");
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
}