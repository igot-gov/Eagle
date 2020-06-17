/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.common.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.infosys.lex.core.exception.ApplicationLogicError;

public interface UserUtilityService {
//
//	/**
//	 * verify users from cassandra and sb-ext if graph is enabled
//	 * 
//	 * @param emails
//	 * @return
//	 * @throws Exception
//	 */
//	Map<String, Object> verifyUsers(String rootOrg, ArrayList<String> emails) throws Exception;
//
//	/**
//	 * verify user uuids
//	 * 
//	 * @param emails
//	 * @return
//	 * @throws Exception
//	 */
////	Map<String, Object> verifyUserUUIDs(List<String> uuids);
//
//	/**
//	 * validate userUUID and email and fetches userUUID
//	 * 
//	 * @param userIdType
//	 * @param id
//	 * @return
//	 * @throws Exception
//	 */
//	String validateAndFetchUser(String userIdType, String id) throws Exception;
//
//	/**
//	 * gets user data from sb-ext (graph)
//	 * 
//	 * @param emails
//	 * @return
//	 * @throws Exception
//	 */
	List<Map<String, Object>> getUsersFromActiveDirectory(List<String> emails);

//
//	String getEmailIdForUUID(String uuid) throws Exception;
//
//	List<Map<String, Object>> getEmailIdsForUUIDs(List<String> uuidList) throws Exception;
//
//	/**
//	 * fetch uuids for a given emailIds
//	 * 
//	 * @param emails
//	 * @return
//	 * @throws Exception
//	 */
	Map<String, Object> getUUIDsFromEmails(List<String> emails);

//
//	/**
//	 * fetches user data for given uuids assuming the uuids are already verified
//	 * 
//	 * @param uuids
//	 * @return
//	 * @throws Exception
//	 */
//	Map<String, Object> getUserDataFromUUID(List<String> uuids) throws Exception;
//
//	
//
//	/**
//	 * validates whether a user with a given userid belongs to a particular rootOrg
//	 * or not.
//	 * 
//	 * @param rootOrg
//	 * @param userId
//	 * @return
//	 */
//	boolean validateUserAndRootOrg(String rootOrg, String userId);
//
//	/**
//	 * fetches the user data source for a given rootOrg
//	 * 
//	 * @param rootOrg
//	 * @return
//	 */
	String getUserDataSource(String rootOrg);

//	Map<String, Object> verifyUserUUIDs(String rootOrg, List<String> uuids) throws Exception;
//
//	Map<String, Object> validateUsersAndRootOrg(String rootOrg, List<String> userId);
//
//	Map<String, Object> getEmailUUIDMapForUUIDs(List<String> uuidList) throws Exception;

	boolean validateUser(String rootOrg, String userId) throws ApplicationLogicError;

	Map<String, Object> validateUsers(String rootOrg, List<String> userIds);

	Map<String, Object> getUsersDataFromUserIds(String rootOrg, List<String> userIds, List<String> source);

	Map<String, Object> getUserDataFromUserId(String rootOrg, String userId, List<String> source);

	Map<String, Object> getUserEmailsFromUserIds(String rootOrg, List<String> userIds);

	String getUserEmailFromUserId(String rootOrg, String userId);

	boolean validatePreviewUser(String rootOrg, String org, String userId, Map<String, Object> contentMeta);

	String getAnswerKeyForExerciseAuthoringPreview(Map<String, Object> contentMeta);
	
	
	Map<String, Object> getContentMeta(String contentId, String rootOrg, String org, String[] fields)
			throws JsonParseException, JsonMappingException, IOException;

	Map<String, Object> validateAndFetchNewUsers(String rootOrg, List<String> usersList);

	Map<String, Object> validateAndFetchNewUsersSet(String rootOrg, List<String> userIds);


	Map<String, Object> fetchUsersDataByUserProperty(String rootOrg, String userPropertyName,
			List<String> propertyValues, List<String> sources, Map<String, Object> conditions);

	Map<String, Object> fetchUserDataByUserProperty(String rootOrg, Map<String, Object> conditions,
			List<String> sources);

}
