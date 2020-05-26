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
package com.infosys.lex.exercise.service;

import java.util.List;
import java.util.Map;

import com.infosys.lex.exercise.dto.NewCodeExerciseDTO;
import com.infosys.lex.exercise.dto.NewExerciseFeedbackDTO;
import com.infosys.lex.exercise.dto.NewLNDExerciseDTO;

public interface ExerciseService {

    /**
     * gets all submissions for a user's exercise
     * @param userId
     * @param contentId
     * @param userIdType
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getExerciseSubmissionsByUser(String rootOrg,String userId, String contentId) throws Exception;
    
    /**
     * gets the latest submission for a user's exercise
     * @param userId
     * @param contentId
     * @param userIdType
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getLatestData(String rootOrg,String userId, String contentId) throws Exception;
    
    /**
     * gets a specific submission for a user's exercise
     * @param userId
     * @param contentId
     * @param submissionId
     * @param userIdType
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOneData(String rootOrg,String userId, String contentId, String submissionId) throws Exception;
    
    /**
     * gets the group for an educator
     * @param educatorId
     * @param userIdType
     * @return
     * @throws Exception
     */
    public List<Map<String, String>> getEducatorGroups(String rootOrg,String educatorId) throws Exception;
    
    /**
     * gets the latest submissions for a group
     * @param groupId
     * @param contentId
     * @return
     * @throws Exception
     */
    public Map<String, Object> getSubmissionsByGroups(String rootOrg,String groupId, String contentId) throws Exception;
    
    /**
     * get latest feedbacks for a user's exercise for the notification page
     * @param userId
     * @param userIdType
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getExerciseNotification(String rootOrg,String userId) throws Exception;

	/**
	 * inserts a feedback for a user's exercise submission
	 * @param meta
	 * @param contentId
	 * @param userId
	 * @param submissionId
	 * @return
	 * @throws Exception
	 */
	String insertFeedback(String rootOrg,NewExerciseFeedbackDTO meta, String contentId, String userId, String submissionId)
			throws Exception;

	/**
	 * inserts submission for code based exercise
	 * @param meta
	 * @param contentId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	String insertCodeSubmission(String rootOrg,NewCodeExerciseDTO meta, String contentId, String userId) throws Exception;
	
	/**
	 * inserts submission for LND exercise
	 * @param meta
	 * @param contentId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	String insertLNDSubmission(String rootOrg,NewLNDExerciseDTO meta, String contentId, String userId) throws Exception;

	/**
	 * insert data in exercise and last exercise table
	 * @param meta
	 * @param contentId
	 * @param userUUID
	 * @return
	 * @throws Exception
	 */
	String insertInExercise(String rootOrg,Map<String, Object> meta, String contentId, String userUUID) throws Exception;


}
