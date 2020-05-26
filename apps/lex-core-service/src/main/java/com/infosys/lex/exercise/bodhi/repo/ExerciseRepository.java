/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
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
package com.infosys.lex.exercise.bodhi.repo;

import java.util.List;
import java.util.Map;

public interface ExerciseRepository {

	/**
	 * fetches all exercises submissions for a user and a content id
	 * @param userUUID
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> getAllExerciseSubmitted(String rootOrg,String userUUID,String contentId) throws Exception;
	
	/**
	 * fetches the latest exercise submission for a user and a content id
	 * @param userUUID
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> getLatestExerciseSubmitted(String rootOrg,String userUUID,String contentId) throws Exception;
	
	/**
	 * fetches a specific exercise submissions for a user and a content id
	 * @param userUUID
	 * @param contentId
	 * @param submissionId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getSpecificExercise(String rootOrg,String userUUID,String contentId, String submissionId) throws Exception;
	
	/**
	 * gets all groups for an educator
	 * @param educatorUUID
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getAllGroupsForEducators(String rootOrg,String educatorUUID) throws Exception;
	
	/**
	 * gets all latest submissions in a group
	 * @param groupId
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getSubmissionsByGroups(String rootOrg,String groupId, String contentId) throws Exception;
	
	/**
	 * validate submission id to be time based uuid
	 * @param submissionId
	 * @throws Exception
	 */
	void validateSubmissionID(String submissionId) throws Exception;

	/**
	 * gets the latest feedbacks for the submission for the user
	 * @param userUUID
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> getNotificationForUser(String rootOrg,String userUUID) throws Exception;

	void validateExerciseID(String contentId) throws Exception;

}

