/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.contentsource.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.contentsource.bodhi.repo.ContentSourceUserRegistration;
import com.infosys.lex.contentsource.bodhi.repo.ContentSourceUserRegistrationKey;
import com.infosys.lex.contentsource.bodhi.repo.ContentSourceUserRegistrationProjection;
import com.infosys.lex.contentsource.bodhi.repo.ContentSourceUserRegistrationRepo;
import com.infosys.lex.contentsource.dto.ContentSourceNameListDto;
import com.infosys.lex.contentsource.dto.ContentSourceUserDetailDto;
import com.infosys.lex.contentsource.postgres.entity.ContentSource;
import com.infosys.lex.contentsource.postgres.entity.ContentSourcePrimaryKey;
import com.infosys.lex.contentsource.postgres.projection.ContentSourceProj;
import com.infosys.lex.contentsource.postgres.projection.ContentSourceShortNameProj;
import com.infosys.lex.contentsource.postgres.repo.ContentSourceRepository;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;

@Service
public class ContentSourceServiceImpl implements ContentSourceService {

	@Autowired
	ContentSourceUserRegistrationRepo registrationRepo;

	@Autowired
	ContentSourceRepository sourceRepo;

	@Autowired
	UserUtilityService userSvc;

	/**
	 * Checks whether the user has registered in the content-source
	 * 
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> getUser(String rootOrg, String sourceShortName, String userId) throws Exception {

		Boolean userExists = userSvc.validateUser(rootOrg, userId);
		if (!userExists) {
			throw new BadRequestException("invalid.user");
		}
		if (sourceShortName == null)
			throw new InvalidDataInputException("invalid.source");

		ContentSourcePrimaryKey key = new ContentSourcePrimaryKey(rootOrg, sourceShortName);
		ContentSource contentSource = sourceRepo.findById(key).orElse(null);

//		ContentSourceUserRegistrationKey contentKey = new ContentSourceUserRegistrationKey(rootOrg, userId, sourceShortName);
		ContentSourceUserRegistrationProjection registration = registrationRepo
				.findByKeyRootOrgAndKeySourceShortNameAndKeyUserId(rootOrg, sourceShortName, userId);

//		Optional<ContentSourceUserRegistration> registration = registrationRepo.findById(contentKey);
		Map<String, Object> returnObject = new HashMap<String, Object>();
		if (registration == null) {
			returnObject.put("hasAccess", false);
		} else {
			returnObject.put("hasAccess", true);

		}
		returnObject.put("userRegistrationDetails", registration);
		if (contentSource != null) {
			returnObject.put("registrationUrl", contentSource.getRegistrationUrl());
			returnObject.put("registrationEnabled", contentSource.getRegistrationEnabled());
		}
		else {
			returnObject.put("registrationUrl", null);
			returnObject.put("registrationEnabled", null);
		}
		return returnObject;

	}

	/**
	 * Fetches all the content-source details based on registration required
	 * 
	 * @param rootOrg
	 * @return
	 */
	@Cacheable("root_org_content_source")
	@Override
	public List<ContentSourceProj> fetchAllContentsourcesForRootOrg(String rootOrg, Boolean registrationProvided) {
		List<ContentSourceProj> resp;
		if (registrationProvided == null) {
			resp = sourceRepo.findAllByKeyRootOrg(rootOrg);
		} else {
			resp = sourceRepo.findAllByKeyRootOrgAndRegistrationProvided(rootOrg, registrationProvided);
		}
		return resp;
	}

	/**
	 * fetch content source detail for rootOrg
	 * 
	 * @param rootOrg
	 * @param sourceName
	 * @return
	 */
	@Cacheable("root_org_content_source_details")
	@Override
	public ContentSourceProj fetchContentsourceDetails(String rootOrg, String sourceShortName,
			Boolean registrationProvided) throws InvalidDataInputException {
		ContentSourceProj contentSource;
		if (sourceShortName == null)
			throw new InvalidDataInputException("invalid.source");

		if (registrationProvided == null)
			contentSource = sourceRepo.findByKeyRootOrgAndKeySourceShortName(rootOrg, sourceShortName);
		else
			contentSource = sourceRepo.findByKeyRootOrgAndKeySourceShortNameAndRegistrationProvided(rootOrg,
					sourceShortName, registrationProvided);
		if (contentSource == null)
			throw new InvalidDataInputException("Content Source not found for rootOrg");
		return contentSource;
	}

	/**
	 * This method registers the given users for the contentSource
	 * 
	 * @param rootOrg
	 * @param contentSource(course     short Name)
	 * @param contentSourceUserDetails
	 * @return
	 */
	@Override
	public Map<String, Object> registerContentSourceForUsers(String rootOrg, String sourceShortName,
			List<ContentSourceUserDetailDto> contentSourceUserDetails) {
		Map<String, Object> resp = new HashMap<String, Object>();

		if (sourceShortName == null)
			throw new InvalidDataInputException("invalid.source");

		// this will check if contentsource exists for the given rootOrg
		ContentSourceProj contentSource = this.fetchContentsourceDetails(rootOrg, sourceShortName, null);

		// registraion provided if null is considered as false

		if (contentSource.getProgressProvided() == null || contentSource.getRegistrationProvided())
			throw new InvalidDataInputException("Admin User Registration not applicable for this content Source");

		Map<String, ContentSourceUserDetailDto> userIdUserDetailMap = new HashMap<String, ContentSourceUserDetailDto>();
		List<String> registerUserIdList = new ArrayList<String>();

		List<String> alreadyRegisteredUsers = new ArrayList<String>();
		List<String> successfullyRegisteredUsers = new ArrayList<String>();
		List<Map<String, String>> registrationErrorUserDetails = new ArrayList<Map<String, String>>();

		for (ContentSourceUserDetailDto userDetail : contentSourceUserDetails) {

			if (userDetail.getUserId() == null || userDetail.getUserId().isEmpty())
				throw new InvalidDataInputException("UserId is either empty or null");

			String userId = userDetail.getUserId();

			if (registerUserIdList.contains(userId))
				throw new InvalidDataInputException("Duplicate userId exist in the userdetail List");

			registerUserIdList.add(userId);
			// This map is mantained to return the userdetail for given userId when updating
			// to DB
			userIdUserDetailMap.put(userId, userDetail);

		}
		// Here already registered users userId is stored and they are removed from the
		// userId list that are to be registered
		List<ContentSourceUserRegistration> contentSourceUserRegistrationList = registrationRepo
				.findAllByKeyRootOrgAndKeySourceShortNameAndKeyUserIdIn(rootOrg, sourceShortName, registerUserIdList);
		for (ContentSourceUserRegistration userRegDetail : contentSourceUserRegistrationList) {
			String alreadyRegisteredUserId = userRegDetail.getKey().getUserId();
			alreadyRegisteredUsers.add(alreadyRegisteredUserId);
			registerUserIdList.remove(alreadyRegisteredUserId);
		}

		for (String registerUserId : registerUserIdList) {
			this.registerContentSourceForUser(rootOrg, contentSource, userIdUserDetailMap.get(registerUserId),
					successfullyRegisteredUsers, registrationErrorUserDetails);
		}

		resp.put("alreadyRegistered", alreadyRegisteredUsers);
		resp.put("successfullyRegistered", successfullyRegisteredUsers);
		resp.put("registrationError", registrationErrorUserDetails);
		return resp;
	}

	/**
	 * validate the user details date and updates it in db and also records the
	 * error userids
	 * 
	 * @param rootOrg
	 * @param sourceShortName
	 * @param registerUserDetails
	 * @param successfullyRegisteredUsers
	 * @param registrationErrorUsers
	 */
	private void registerContentSourceForUser(String rootOrg, ContentSourceProj contentSource,
			ContentSourceUserDetailDto registerUserDetails, List<String> successfullyRegisteredUsers,
			List<Map<String, String>> registrationErrorUsers) {
		String userId = registerUserDetails.getUserId();
		try {
			if (!userSvc.validateUser(rootOrg, userId)) {
				throw new InvalidDataInputException("Invalid User : " + userId);
			}

			if (registerUserDetails.getStartDate() == null || registerUserDetails.getStartDate().isEmpty())
				throw new InvalidDataInputException("StartDate is either empty or null");
			if (registerUserDetails.getEndDate() == null || registerUserDetails.getEndDate().isEmpty())
				throw new InvalidDataInputException("EndDate is either empty or null");
			Date startDate = this.validateDateFormatAndFetchDate(registerUserDetails.getStartDate());
			Date endDate = this.validateDateFormatAndFetchDate(registerUserDetails.getEndDate());
			if (startDate.getTime() > endDate.getTime())
				throw new InvalidDataInputException("End Date is before start date");

			if (contentSource.getLicenseExpiresOn() != null
					&& endDate.getTime() > contentSource.getLicenseExpiresOn().getTime())
				throw new InvalidDataInputException("Use registration End Date is after the source license expires");
			ContentSourceUserRegistrationKey key = new ContentSourceUserRegistrationKey(rootOrg, userId,
					contentSource.getSourceShortName());
			ContentSourceUserRegistration contentSourceUserRegistration = new ContentSourceUserRegistration(key,
					startDate, endDate, null);
			registrationRepo.save(contentSourceUserRegistration);
			successfullyRegisteredUsers.add(userId);
		} catch (InvalidDataInputException ex) {
			Map<String, String> userRegistrationErrorMap = new HashMap<String, String>();
			userRegistrationErrorMap.put("userId", userId);
			userRegistrationErrorMap.put("errorReason", ex.getCode());
			registrationErrorUsers.add(userRegistrationErrorMap);

		} catch (Exception ex) {
			Map<String, String> userRegistrationErrorMap = new HashMap<String, String>();
			userRegistrationErrorMap.put("userId", userId);
			userRegistrationErrorMap.put("errorReason", "DB Error");
			registrationErrorUsers.add(userRegistrationErrorMap);
		}
	}

	/**
	 * Validates date format sent
	 * 
	 * @param dateStr
	 * @return
	 * @throws InvalidDataInputException
	 */
	private Date validateDateFormatAndFetchDate(String dateStr) throws InvalidDataInputException {
		Date date = null;
		try {

//			date = sdf.parse(dateStr);
//			if (!dateStr.equals(sdf.format(date))) {
//				throw new InvalidDataInputException("Invalid Date format (dd-MM-yyyy)");
//			}

			LocalDate localDate = LocalDate.parse(dateStr);
			date = localDate.toDate();
		} catch (Exception ex) {
			throw new InvalidDataInputException("Invalid Date format (ISO Date format)");
		}
		return date;
	}
	
	
	/**
	 * fetch registered user for contentSource
	 */
	@Override
	public Collection<Object> getRegisteredUsers(String rootOrg,String sourceShortName)
	{
		List<String> userIds = new ArrayList<>();
		Map<String,Object> userDetailMap = new HashMap<>();
		List<ContentSourceUserRegistrationProjection> registeredUsers =  registrationRepo.findAllByKeyRootOrgAndKeySourceShortName(rootOrg, sourceShortName);
		
		for (ContentSourceUserRegistrationProjection registeredUser : registeredUsers)
		{
			userIds.add(registeredUser.getUserId());
		}
		
		//fetches userdetail mapped to thier userid
		if(!userIds.isEmpty())
			userDetailMap = userSvc.getUsersDataFromUserIds(rootOrg, userIds, Arrays.asList(new String[] {PIDConstants.UUID,PIDConstants.FIRST_NAME,PIDConstants.LAST_NAME,PIDConstants.EMAIL}));
		return userDetailMap.values();
	}

	
	/**
	 * delete users who have access to given content-source(de register user)
	 */
	
	@Override
	public void deRegisterUser(String rootOrg,String sourceShortName,List<String> registeredUsers)throws Exception
	{

		registrationRepo.deleteUserForSourceShortName(rootOrg, sourceShortName, registeredUsers);
		
	}

	
	/**
	 * fetch content source detail for rootOrg
	 * 
	 * @param rootOrg
	 * @param sourceName
	 * @return
	 */
	@Cacheable("root_org_content_source_details_for_sourcename")
	@Override
	public Map<String, ContentSourceShortNameProj> fetchContentsourceDetailsForSourceName(String rootOrg, ContentSourceNameListDto req) 
			throws InvalidDataInputException {
		Map<String,ContentSourceShortNameProj> resp = new HashMap<>();
		List<String> sourceNames = req.getContentSourceNames();
		
		List<ContentSourceShortNameProj> contentSources = sourceRepo.findByKeyRootOrgAndSourceNameIn(rootOrg, sourceNames);
		
		for (ContentSourceShortNameProj contentSource:contentSources)
		{
			resp.put(contentSource.getSourceName(), contentSource);
		}
		
		
		return resp;
	}

	
}
