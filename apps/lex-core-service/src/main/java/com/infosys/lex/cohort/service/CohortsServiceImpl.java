/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.cohort.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.assessment.bodhi.repo.UserAssessmentTopPerformerRepository;
import com.infosys.lex.cohort.bodhi.repo.CohortUsers;
import com.infosys.lex.cohort.bodhi.repo.EducatorsRepository;
import com.infosys.lex.common.constants.JsonKey;
import com.infosys.lex.common.service.AccessTokenService;
import com.infosys.lex.common.service.AppConfigService;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.LexConstants;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.progress.bodhi.repo.ContentProgressByLastAccessRepository;
import com.infosys.lex.training.service.TrainingsService;

@Service
public class CohortsServiceImpl implements CohortsService {

	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	ParentService parentSvc;

	@Autowired
	AccessTokenService accessTokenService;

	@Autowired
	ContentProgressByLastAccessRepository contentProgressByLastAccessRepo;

	@Autowired
	UserAssessmentTopPerformerRepository userAssessmentTopPerformerRepo;

	@Autowired
	EducatorsRepository educatorsRepo;

	@Autowired
	ContentService contentService;

	@Autowired
	LexServerProperties lexServerProp;

	@Autowired
	TrainingsService trainingServ;

	@Autowired
	AppConfigService appConfigServ;
//
//	@Override
//	public List<CohortUsers> getUserWithCommonGoals(String rootOrg, String resourceId, String userUUID, int count)
//			throws Exception {
//		List<CohortUsers> similarGoalsUsers = new ArrayList<CohortUsers>();
//		String parent = parentSvc.getCourseParent(resourceId);
//
//		parent = parent == null ? resourceId : parent;
//
//		// if no parent exists
//		if (parent != null) {
//			// Fetch user_id of users having similar goals
//			List<Map<String, Object>> sharingGoalsRecords = learningGoalsRepo.learningGoalsContainResources(parent);
//
//			if (sharingGoalsRecords != null && !sharingGoalsRecords.isEmpty()) {
//				// Sorting users with similar goals based on last updated
//				Collections.sort(sharingGoalsRecords, new Comparator<Map<String, Object>>() {
//					@Override
//					public int compare(Map<String, Object> m1, Map<String, Object> m2) {
//						if (m1.get("last_updated_on") != null && m2.get("last_updated_on") != null) {
//							return ((Date) m2.get("last_updated_on")).compareTo((Date) (m1.get("last_updated_on")));
//						} else {
//							return 1;
//						}
//					}
//				});
//			}
//
//			int counter = 1;
//
//			List<String> userNames = new ArrayList<String>();
//			for (Map<String, Object> similarGoalsRow : sharingGoalsRecords) {
//
//				if (similarGoalsRow.get("user_id") != null) {
//					String uuid = similarGoalsRow.get("user_id").toString().toLowerCase();
//					String emailId = userUtilService.getEmailIdForUUID(userUUID);
//					String userName = emailId.substring(0, emailId.indexOf("@"));
//
//					// If similar-goal-user is already in active-users list or user himself is in
//					// fetched data then
//					// do not add him to usersSharingGoals
//					if (!userUUID.equals(uuid) && !userNames.contains(userName)) {
//						userNames.add(userName);
//						CohortUsers user = new CohortUsers();
//						user.setUser_id("similargoal");
//						user.setDesc("Has similar goal");
//						user.setEmail(emailId);
//						similarGoalsUsers.add(user);
//						// for number of user needed to be displayed
//						if (counter == count)
//							break;
//						counter++;
//					}
//				}
//			}
//
//			if (!userNames.isEmpty())
//				validateUsersFromActiveDirectory(similarGoalsUsers, userNames);
//
//		}
//		return similarGoalsUsers;
//	}

	/**
	 * This method provides the active users within the given time(if filtered) else
	 * gives users within 90 days the number of users depends on count
	 * 
	 * @param rootOrg
	 * @param contentId
	 * @param userUUID
	 * @param count
	 * @param durationType
	 * @param duration
	 * @return
	 * @throws Exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<CohortUsers> getActiveUsers(String rootOrg, String contentId, String userUUID, int count,
			Boolean toFilter) throws Exception {

		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}

		Map<String, Object> parentCollection = parentSvc.getCourseHierarchyForResource(contentId);

		List<String> parentCourses = new ArrayList<String>();
		List<String> parentLearningModules = new ArrayList<String>();

		// Adding parents from parentCollection.
		parentCourses = (List<String>) parentCollection.get("courses");

		if ((parentCourses.isEmpty())) {
			parentLearningModules = (List<String>) parentCollection.get("modules");
		}

		// For active users, parents will be added to this.
		List<String> resourceParentList = new ArrayList<String>();

		// For active users, consider parent courses then learning modules and then
		// resources.
		if (!parentCourses.isEmpty()) {
			resourceParentList.addAll(parentCourses);
		} else if (!parentLearningModules.isEmpty()) {
			resourceParentList.addAll(parentLearningModules);
		} else {
			resourceParentList.add(contentId);
		}

		// Active users are added to this
		List<CohortUsers> activeUserCollection = new ArrayList<CohortUsers>();

		int counter = 1;
		// Extracted user names are stored here.
		List<String> userNames = new ArrayList<String>();
		// MailMap for contacting users
		Date d = Date.from(Instant.now().minus(90, ChronoUnit.DAYS));

		// if filter is enabled
		if (toFilter) {
			int duration = Integer
					.valueOf(appConfigServ.getConfigForKey(rootOrg, LexConstants.ACTIVEUSER_DURATION_CONFIG_KEY));
			d = Date.from(Instant.now().minus(duration, ChronoUnit.MINUTES));
		}
		List<Map<String, Object>> collection = contentProgressByLastAccessRepo.findActiveUsers(resourceParentList,
				rootOrg, d);

		/*------------------updated active users-------------------*/

		Set<String> activeUserUUIDSet = new HashSet<String>();
		for (Map<String, Object> doc : collection) {
			activeUserUUIDSet.add(doc.get("user_id").toString());
		}
//		Map<String, Object> activeUserUUIDEmailMap = userUtilService.getEmailUUIDMapForUUIDs(activeUserUUIDs);
		Map<String, Object> activeUserUUIDEmailMap = new HashMap<>();
		List<String> activeUserIdList = new ArrayList<String>(activeUserUUIDSet);
		if (!activeUserUUIDSet.isEmpty())
			activeUserUUIDEmailMap = userUtilService.getUserEmailsFromUserIds(rootOrg, activeUserIdList);
		for (Map<String, Object> userContentProgress : collection) {
			String activeUserUUID = userContentProgress.get("user_id").toString();
			if (activeUserUUIDEmailMap.containsKey(activeUserUUID)) {
				String activeUserEmail = activeUserUUIDEmailMap.get(activeUserUUID).toString();
				int isEmail = activeUserEmail.indexOf("@");
				if (isEmail > 1) {
					String userName = activeUserEmail.substring(0, isEmail);
					if (!activeUserUUID.equals(userUUID) && !userNames.contains(userName)) {
						userNames.add(userName);

						CohortUsers activeCohortUser = new CohortUsers();
						activeCohortUser.setEmail(activeUserEmail);
						activeCohortUser.setUser_id(activeUserUUID);

						// Fetch last accessed time
						double timestampData = ((Date) userContentProgress.get("last_accessed_on")).getTime();
						long lastActive = (long) timestampData;

						// Converting into minutes
						long timeLagInUse = (Instant.now().toEpochMilli() - lastActive) / (1000 * 60);

						// If user has accessed the resource within last 15 minutes
						if (timeLagInUse < 15) {
							activeCohortUser.setDesc("Currently viewing");
						} else {
							// Checking if user has been active in last 1 day then show no. of hours user
							// was active before
							long timeAgo = timeLagInUse / 1440; // Converting into days
							// With in 1 day
							if (timeAgo < 1) {
								// Less than 2 hours
								if (timeLagInUse / 60 < 2) {
									// Less than 1 hour
									if (timeLagInUse < 60) {
										activeCohortUser.setDesc("Last active " + timeLagInUse + " minutes ago");
									} else {
										// 1 hour ago
										activeCohortUser.setDesc("Last active " + timeLagInUse / 60 + " hour ago");
									}
								} else {
									activeCohortUser.setDesc("Last active " + timeLagInUse / 60 + " hours ago");
								}

							} else {
								if (timeAgo < 2)
									// 1 day ago
									activeCohortUser.setDesc("Last active " + timeAgo + " day ago");
								else
									activeCohortUser.setDesc("Last active " + timeAgo + " days ago");
							}
						}

						activeUserCollection.add(activeCohortUser);
						if (counter == count)
							break;
						counter++;
					}
				}
			}
		}

		/*-----------------(end) updated active users--------------*/

		if (!userNames.isEmpty())
			validateUsersFromActiveDirectory(rootOrg, activeUserCollection, userNames, activeUserIdList);

		return activeUserCollection;

	}

	/*
	 * This method validates the duration
	 */
//	private void validateDuration(String durationType, int duration) {
//		if (!(durationType.equalsIgnoreCase("h") || durationType.equalsIgnoreCase("m")))
//			throw new InvalidDataInputException("Invalid duration type provided");
//		else if (duration < 0)
//			throw new InvalidDataInputException("Invalid duration provided");
//
//	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CohortUsers> getAuthors(String rootOrg, String contentId, String userUUID, int count) throws Exception {

		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}

		// Here infosys mailIds are stored
		List<String> userNames = new ArrayList<String>();
		List<CohortUsers> authors = new ArrayList<CohortUsers>();

		// Get the meta of resource specifically for authors
		// In case of learning path get its children
		// Fetching only those resource with are shown in UI

		// Getting meta data related to resource
		Map<String, Map<String, Object>> contentMetaMap = contentService.filterAndFetchContentMetaToShow(
				Arrays.asList(new String[] { contentId }), new HashSet<>(Arrays.asList("creatorContacts")));

		// Getting meta data related to content
		Map<String, Object> contentMeta = null;
		if (contentMetaMap.isEmpty()) {
			throw new InvalidDataInputException("Invalid Content Id");
		}
		contentMeta = contentMetaMap.get(contentId);

		int counter = 1;
		List<String> creatorUUIDs = new ArrayList<String>();

		if (contentMeta != null && !contentMeta.isEmpty()) {

			// Remove these people from authors as they are temp authors
			String[] superAuthors = { "EMAIL", "EMAIL",
					"EMAIL", "EMAIL", "EMAIL",
					"EMAIL" };
			List<String> authorsToExclude = new ArrayList<String>(Arrays.asList(superAuthors));

			// Details for Authors
			if (contentMeta.containsKey("creatorContacts")) {

				List<Map<String, Object>> creatorDetails = (List<Map<String, Object>>) contentMeta
						.get("creatorContacts");

				for (Map<String, Object> creator : creatorDetails) {
					if (creator.get("id").toString() != null)
						creatorUUIDs.add(creator.get("id").toString());
				}

				Map<String, Object> creatorsUUIDEmailMap = userUtilService.getUserEmailsFromUserIds(rootOrg,
						creatorUUIDs);
				for (Map<String, Object> creator : creatorDetails) {
					String creatorUUID = creator.get("id").toString();

					// Check that creator is not a temp author
					if (creatorsUUIDEmailMap.containsKey(creatorUUID) && creatorsUUIDEmailMap.get(creatorUUID) != null
							&& creatorsUUIDEmailMap.get(creatorUUID).toString().contains("@")) {
						String creatorEmail = creatorsUUIDEmailMap.get(creatorUUID).toString();

						// Fetching username from mailId
						String userName = creatorEmail.toLowerCase().substring(0, creatorEmail.indexOf("@"))
								.toLowerCase();
						if (!authorsToExclude.contains(creatorEmail) && !userNames.contains(userName)
								&& !creatorUUID.equals(userUUID)) {
							CohortUsers user = new CohortUsers();
							user.setEmail(creatorEmail);
							userNames.add(userName);

							user.setDesc("Author");
							user.setUser_id(creatorUUID); // Setting a dummy user id for authors
							authors.add(user);
							if (counter == count)
								break;
							counter++;
						}
					}

				}
			}

			// Author Region - End

			if (!userNames.isEmpty())
				validateUsersFromActiveDirectory(rootOrg, authors, userNames, creatorUUIDs);
		}

		return authors;

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CohortUsers> getEducators(String rootOrg, String contentId, String userUUID, int count)
			throws Exception {
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}

		// This contains the list of all the children for provided course(resourceId) if
		// it is a learning-path.
		// Else, it will contain the parents for provided course(resourceId)
		List<String> parentResourceList = new ArrayList<String>();
		// Here infosys mailIds are stored
		List<String> userNames = new ArrayList<String>();
		String userEmail = userUtilService.getUserEmailFromUserId(rootOrg, userUUID);

		List<CohortUsers> educators = new ArrayList<CohortUsers>();

		// Get the meta of resource specifically for authors
		// In case of learning path get its children
		// Fetching only those resource with status=LIVE
		Map<String, Map<String, Object>> contentMetaMap = contentService.filterAndFetchContentMetaToShow(
				Arrays.asList(new String[] { contentId }),
				new HashSet<>(Arrays.asList("contentType", "children", "identifier")));

		// Getting meta data related to content
		Map<String, Object> contentMeta = null;
		if (contentMetaMap.isEmpty()) {
			throw new InvalidDataInputException("Invalid Content Id");
		}
		contentMeta = contentMetaMap.get(contentId);

		int counter = 1;
		if (contentMeta != null && !contentMeta.isEmpty()) {

			if (contentMeta.containsKey("contentType")) {
				if (contentMeta.get("contentType").toString().toLowerCase().equals("learning path")) {
					// Get the children of resource in case it is learning path
					if (contentMeta.containsKey("children")) {
						if (contentMeta.get("children") != null) {

							// Doing this since sometimes list of null is coming
							List<?> childrenList = (List<?>) contentMeta.get("children");

							for (Object childObj : childrenList) {
								if (childObj != null) {
									Map<String, Object> childMap = (Map<String, Object>) childObj;
									// Adding all child ids of the resource
									parentResourceList.add(childMap.get("identifier").toString());
								}
							}

						}
					}
				} else {
					// get the parents of resource
					Map<String, Object> parentCollection = parentSvc.getCourseHierarchyForResource(contentId);

					List<String> parent = new ArrayList<String>();
					if (parentCollection != null && !parentCollection.isEmpty()) {

						if (((List<Map<String, Object>>) parentCollection.get("courses")).size() > 0) {
							// If its parent is a course
							parent = (List<String>) parentCollection.get("courses");
						} else if (((List<Map<String, Object>>) parentCollection.get("modules")).size() > 0) {
							// If its parent is a module
							parent = (List<String>) parentCollection.get("modules");
						} else {
							parent.add(contentId);
						}
					}

					if (parent != null) {
						parentResourceList.addAll(parent);
					}
				}
			}

			List<String> educatorUUIDs = new ArrayList<String>();
			// Fetch educators for courses from educator table in case request is from
			// client and use Lhub api in
			// case its from infosys instance
			List<Map<String, Object>> educatorRecords = null;
			if (rootOrg.equals(JsonKey.INFOSYS_ROOTORG)) {
				// this populates the access token in servlet context
				accessTokenService.getAccessToken("lhub");

				List<String> educatorUsernames = trainingServ.getEducatorDetails(parentResourceList);
				List<String> educatorEmails = new ArrayList<String>();

				for (String educatorUsername : educatorUsernames) {
					String userName = educatorUsername.contains("@")
							? educatorUsername.substring(0, educatorUsername.indexOf("@")).trim().toLowerCase()
							: educatorUsername.trim().toLowerCase();
					educatorEmails.add(userName + "@ad.infosys.com");
				}

				Map<String, Object> educatorEmailUUIDMap = userUtilService.getUUIDsFromEmails(educatorEmails);
				for (String educatorEmail : educatorEmails) {
					// Fetching username
					String userName = educatorEmail.contains("@")
							? educatorEmail.substring(0, educatorEmail.indexOf("@")).trim().toLowerCase()
							: educatorEmail.trim().toLowerCase();
					String educatorUUID = educatorEmailUUIDMap.containsKey(educatorEmail)
							? educatorEmailUUIDMap.get(educatorEmail).toString()
							: "";
					if (!educatorUUID.isEmpty())
						educatorUUIDs.add(educatorUUID);
					// If educator is author or user himself is educator, don't add to educator List
					if (!userNames.contains(userName) && !userEmail.equals(educatorEmail)) {

						CohortUsers user = new CohortUsers();
						user.setDesc("Educator");
						user.setEmail(educatorEmail);
						user.setUser_id(educatorUUID); // setting a dummy user id for educators
						educators.add(user);

						userNames.add(userName);

						if (counter == count)
							break;
						counter++;
					}
				}

			} else {
				educatorRecords = educatorsRepo.findByPrimaryKeyRootOrgAndPrimaryKeyContentIdIn(rootOrg,
						parentResourceList);
				counter = 1;

				for (Map<String, Object> educatorRow : educatorRecords) {
					educatorUUIDs.add(educatorRow.get("user_id").toString());
				}
				Map<String, Object> educatorUUIDEmailMap = userUtilService.getUserEmailsFromUserIds(rootOrg,
						educatorUUIDs);
				for (Map<String, Object> educatorRow : educatorRecords) {

					String educatorUUID = educatorRow.get("user_id").toString();
					if (educatorUUIDEmailMap.containsKey(educatorUUID) && educatorUUIDEmailMap.get(educatorUUID) != null
							&& educatorUUIDEmailMap.get(educatorUUID).toString().contains("@")) {
						String educatorEmail = educatorUUIDEmailMap.get(educatorUUID).toString();
						// Fetching username
						String userName = educatorEmail.substring(0, educatorEmail.indexOf("@")).toLowerCase();

						// If educator is author or user himself is educator, don't add to educator List
						if (!userNames.contains(userName) && !userUUID.equals(educatorUUID)) {

							CohortUsers user = new CohortUsers();
							user.setDesc("Educator");
							user.setEmail(educatorEmail);
							user.setUser_id(educatorUUID); // setting a dummy user id for educators
							educators.add(user);

							userNames.add(userName);

							if (counter == count)
								break;
							counter++;
						}
					}
				}
			}

			// Educator Region - End

			if (!userNames.isEmpty())
				validateUsersFromActiveDirectory(rootOrg, educators, userNames, educatorUUIDs);

		}

		return educators;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CohortUsers> getTopPerformers(String rootOrg, String contentId, String userUUID, int count)
			throws Exception {
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}
		// This contains the list of all the children for provided course(resourceId) if
		// it is a learning-path.
		// Else, it will contain the parents for provided course(resourceId)
		List<String> assessmentIdList = new ArrayList<String>();

		// Here infosys mailIds are stored
		List<String> userNames = new ArrayList<String>();
		List<CohortUsers> topPerformers = new ArrayList<CohortUsers>();

		// Get the meta of resource specifically for authors
		// In case of learning path get its children
		// Fetching only those resource with status=LIVE

		Map<String, Map<String, Object>> contentMetaMap = contentService.filterAndFetchContentMetaToShow(
				Arrays.asList(new String[] { contentId }),
				new HashSet<>(Arrays.asList("contentType", "identifier", "children", "collections", "resourceType")));

		// Getting meta data related to content
		Map<String, Object> contentMeta = null;
		if (contentMetaMap.isEmpty()) {
			throw new InvalidDataInputException("Invalid Content Id");
		}
		contentMeta = contentMetaMap.get(contentId);

		int counter = 1;

		if (contentMeta != null && !contentMeta.isEmpty()) {

//			if (contentMeta.containsKey("contentType")) {
//				if (contentMeta.get("contentType").toString().toLowerCase().equals("learning path")) {
//					// Get the assessment amongst the children of children of a learning path
//					if (contentMeta.containsKey("children")) {
//						if (contentMeta.get("children") != null) {
//							List<String> childrenList = new ArrayList<String>();
//							for (Map<String, Object> childrenMap : (List<Map<String, Object>>) contentMeta
//									.get("children")) {
//								childrenList.add(childrenMap.get("identifier").toString());
//							}
//							assessmentIdList = parentSvc.getAssessmentsFromChildrenOfContentList(childrenList);
//
//						}
//					}
//				} else if (contentMeta.get("contentType").toString().equalsIgnoreCase("course")
//						|| contentMeta.get("contentType").toString().equalsIgnoreCase("collection")) {
//
//					// when the content is course/module then the assessments amongst the children
//					// of these resources are fetched
//					if (contentMeta.containsKey("children")) {
//						if (contentMeta.get("children") != null) {
//
//							List<String> childrenList = new ArrayList<String>();
//							for (Map<String, Object> childrenMap : (List<Map<String, Object>>) contentMeta
//									.get("children")) {
//								childrenList.add(childrenMap.get("identifier").toString());
//							}
//
//							assessmentIdList = parentSvc.getAssessmentsFromContentList(childrenList);
//
//						}
//					}
//
//				} else {
//					// This is if content is Resource
//
//					// if the resource is assessment then the top performers for this resource is
//					// fetched
//					// else the assessments amongst resource's parent contents children is fetched.
//					if (contentMeta.containsKey("resourceType")
//							&& "assessment".equalsIgnoreCase(String.valueOf(contentMeta.get("resourceType")))) {
//						assessmentIdList.add(contentId);
//
//					} else {
//						// get the parents of resource
//						List<String> parentList = new ArrayList<String>();
//						for (Map<String, Object> parentMap : (List<Map<String, Object>>) contentMeta
//								.get("collections")) {
//							parentList.add(parentMap.get("identifier").toString());
//						}
//						assessmentIdList = parentSvc.getAssessmentsFromChildrenOfContentList(parentList);
//					}
//
//				}
//			}

			/*-----------------------------------------------------------------------*/
			if (contentMeta.containsKey("contentType")) {
				if (contentMeta.get("contentType").toString().toLowerCase().equals("learning path")) {
					// Get the children of resource in case it is learning path
					if (contentMeta.containsKey("children")) {
						if (contentMeta.get("children") != null) {

							// Doing this since sometimes list of null is coming
							List<?> childrenList = (List<?>) contentMeta.get("children");

							for (Object childObj : childrenList) {
								if (childObj != null) {
									Map<String, Object> childMap = (Map<String, Object>) childObj;
									// Adding all child ids of the resource
									assessmentIdList.add(childMap.get("identifier").toString());
								}
							}

						}
					}
				} else {
					// get the parents of resource
					Map<String, Object> parentCollection = parentSvc.getCourseHierarchyForResource(contentId);

					List<String> parent = new ArrayList<String>();
					if (parentCollection != null && !parentCollection.isEmpty()) {

						if (((List<Map<String, Object>>) parentCollection.get("courses")).size() > 0) {
							// If its parent is a course
							parent = (List<String>) parentCollection.get("courses");
						} else if (((List<Map<String, Object>>) parentCollection.get("modules")).size() > 0) {
							// If its parent is a module
							parent = (List<String>) parentCollection.get("modules");
						} else {
							parent.add(contentId);
						}
					}

					if (parent != null) {
						assessmentIdList.addAll(parent);
					}
				}
			}
			/*------------------------------------------------------------------------*/

			/*
			 * get top peformers cutoff from cohort prop file int topPerformerCutOff = 90;
			 * Properties prop = this.readCohortPropeties(); if (prop != null &&
			 * !prop.isEmpty()) { topPerformerCutOff =
			 * Integer.parseInt(prop.getProperty("result_Percent_Cohort")); }
			 */

			// TPF Region

			// fetch top learners
			List<Map<String, Object>> topLearnerRecords = new ArrayList<>();
			/*----------------------------------------------------------------------*/

			if (!assessmentIdList.isEmpty()) {
				topLearnerRecords = userAssessmentTopPerformerRepo
						.findByPrimaryKeyRootOrgAndPrimaryKeyParentSourceIdIn(rootOrg, assessmentIdList);
			}

			/*----------------------------------------------------------------------*/

			// sorting top learners based on date of assessment in desc as for 1 resource id
			// the record is already sorted in db
			if (assessmentIdList.size() > 1) {
				Collections.sort(topLearnerRecords, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> m1, Map<String, Object> m2) {
						if (m1.get("ts_created") != null && m2.get("ts_created") != null) {
							return ((Date) m2.get("ts_created")).compareTo((Date) (m1.get("ts_created")));
						} else {
							return 1;
						}
					}
				});
			}

			// in assessment table user_id contains email id instead of userid
			counter = 1;
			Set<String> topLearnerUUIDSet = new HashSet<String>();

			for (Map<String, Object> topLearnerRow : topLearnerRecords) {
				topLearnerUUIDSet.add(topLearnerRow.get("user_id").toString());
			}
			List<String> topLearnierIdList = new ArrayList<String>(topLearnerUUIDSet);
			Map<String, Object> learnerUUIDEmailMap = new HashMap<>();
			if (!topLearnerUUIDSet.isEmpty())
				learnerUUIDEmailMap = userUtilService.getUserEmailsFromUserIds(rootOrg, topLearnierIdList);
			for (Map<String, Object> topLearnerRow : topLearnerRecords) {
				// Same Logic as before
				String topLearnerUUID = topLearnerRow.get("user_id").toString();
				if (learnerUUIDEmailMap.containsKey(topLearnerUUID) && learnerUUIDEmailMap.get(topLearnerUUID) != null
						&& learnerUUIDEmailMap.get(topLearnerUUID).toString().contains("@")) {
					String topLearnerEmail = learnerUUIDEmailMap.get(topLearnerUUID).toString();
					String userName = topLearnerEmail.toLowerCase().substring(0, topLearnerEmail.indexOf("@"));

					if (!userNames.contains(userName) && !topLearnerUUID.toLowerCase().equals(userUUID)) {

						CohortUsers user = new CohortUsers();
						user.setDesc("Top Learner");
						user.setUser_id(topLearnerUUID);
						user.setEmail(topLearnerEmail);
						topPerformers.add(user);
						userNames.add(userName);
						if (counter == count)
							break;
						counter++;
					}
				}
			}

			// TPF Region - End

			// Checking validity of all smes if
			if (!userNames.isEmpty())
				validateUsersFromActiveDirectory(rootOrg, topPerformers, userNames, topLearnierIdList);
		}

		return topPerformers;
	}

	/*
	 * This method selects those uses whose details are there in active directory,
	 * (Imp)checks whether the employee still part of the organization
	 */

	@SuppressWarnings("unchecked")
	private List<CohortUsers> validateUsersFromActiveDirectory(String rootOrg, List<CohortUsers> cohortUsers,
			List<String> userNames, List<String> cohortUserUUID) throws Exception {
		List<Map<String, Object>> usersGraphData = new ArrayList<Map<String, Object>>();
		if ("su".equals(userUtilService.getUserDataSource(rootOrg))) {
			if (!userNames.isEmpty())
				usersGraphData = userUtilService.getUsersFromActiveDirectory(new ArrayList<>(userNames));
			Map<String, Map<String, Object>> usersGraphDataEmailMap = new HashMap<String, Map<String, Object>>();
			if (!usersGraphData.isEmpty()) {
				for (Map<String, Object> userDataMap : usersGraphData) {
					String officialEmail = userDataMap.get("onPremisesUserPrincipalName").toString().toLowerCase();
					usersGraphDataEmailMap.put(officialEmail, userDataMap);

				}
			}
			ListIterator<CohortUsers> userIter = cohortUsers.listIterator();

			while (userIter.hasNext()) {
				CohortUsers user = userIter.next();

				if (usersGraphDataEmailMap.containsKey(user.getEmail())) {
					Map<String, Object> userGraphData = usersGraphDataEmailMap.get(user.getEmail());
					user.setLast_name(
							userGraphData.get("givenName") != null ? userGraphData.get("givenName").toString() : "");
					user.setFirst_name(
							userGraphData.get("surname") != null ? userGraphData.get("surname").toString() : "");
					user.setPhone_No(
							userGraphData.get("mobilePhone") != null ? userGraphData.get("mobilePhone").toString()
									: "0");
					user.setDepartment(
							userGraphData.get("department") != null ? userGraphData.get("department").toString() : "");
					user.setDesignation(
							userGraphData.get("jobTitle") != null ? userGraphData.get("jobTitle").toString() : "");
					user.setUserLocation(
							userGraphData.get("usageLocation") != null ? userGraphData.get("usageLocation").toString()
									: "");
					user.setCity(userGraphData.get("city") != null ? userGraphData.get("city").toString() : "");
					user.setUser_id(null);
				} else {
					userIter.remove();

				}

			}
		} else {
			Map<String, Object> pidUsersData = new HashMap<String, Object>();
			if (!cohortUserUUID.isEmpty())
				pidUsersData = userUtilService.getUsersDataFromUserIds(rootOrg, cohortUserUUID,
						Arrays.asList(PIDConstants.EMAIL, PIDConstants.FIRST_NAME, PIDConstants.MIDDLE_NAME,
								PIDConstants.LAST_NAME, PIDConstants.DEPARTMENT_NAME,
								PIDConstants.CONTACT_PHONE_NUMBER_OFFICE, PIDConstants.JOB_ROLE, PIDConstants.JOB_TITLE,
								PIDConstants.ORG, PIDConstants.ORGANIZATION_LOCATION_CITY, PIDConstants.UNIT_NAME,
								PIDConstants.CONTACT_PHONE_NUMBER_PERSONAL));
			ListIterator<CohortUsers> userIter = cohortUsers.listIterator();

			while (userIter.hasNext()) {
				CohortUsers user = userIter.next();

				if (pidUsersData.containsKey(user.getUser_id())) {
					Map<String, Object> userPidData = (Map<String, Object>) pidUsersData.get(user.getUser_id());
					String lastName = userPidData.get(PIDConstants.MIDDLE_NAME) != null
							? userPidData.get(PIDConstants.MIDDLE_NAME).toString()
							: "";
					lastName += userPidData.get(PIDConstants.LAST_NAME) != null
							? userPidData.get(PIDConstants.LAST_NAME).toString()
							: "";
					user.setLast_name(lastName);
					user.setFirst_name(userPidData.get(PIDConstants.FIRST_NAME) != null
							? userPidData.get(PIDConstants.FIRST_NAME).toString()
							: "");

					String phoneNo = userPidData.get(PIDConstants.CONTACT_PHONE_NUMBER_PERSONAL) != null
							? userPidData.get(PIDConstants.CONTACT_PHONE_NUMBER_PERSONAL).toString()
							: "0";

					user.setPhone_No(phoneNo);

					String department = userPidData.get(PIDConstants.DEPARTMENT_NAME) != null
							? userPidData.get(PIDConstants.DEPARTMENT_NAME).toString()
							: "";

					department = department.isEmpty() ? (userPidData.get(PIDConstants.UNIT_NAME) != null
							? userPidData.get(PIDConstants.UNIT_NAME).toString()
							: "") : department;
					department = department.isEmpty()
							? (userPidData.get(PIDConstants.ORG) != null ? userPidData.get(PIDConstants.ORG).toString()
									: "")
							: department;
					// sets department as org if department is empty else if department is shown if
					// both are empty then unit name
					user.setDepartment(department);
					String jobTitle = userPidData.get(PIDConstants.JOB_TITLE) != null
							? userPidData.get(PIDConstants.JOB_TITLE).toString()
							: (userPidData.get(PIDConstants.JOB_ROLE) != null
									? userPidData.get(PIDConstants.JOB_ROLE).toString()
									: "");
					user.setDesignation(jobTitle);
					user.setUserLocation("");

					user.setCity(userPidData.get(PIDConstants.ORGANIZATION_LOCATION_CITY) != null
							? userPidData.get(PIDConstants.ORGANIZATION_LOCATION_CITY).toString()
							: "");
					user.setUser_id(null);
				} else {
					userIter.remove();

				}

			}

		}

		return cohortUsers;
	}

}
