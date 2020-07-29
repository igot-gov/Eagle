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
package com.infosys.lex.goal.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.infosys.lex.goal.dto.ActionDTO;
import com.infosys.lex.goal.dto.GoalDTO;

public interface GoalsService {

	/**
	 * This method is used to remove a goal from user_learning_goals table.
	 * 
	 * @param goalType
	 * @param userUUID
	 * @param goalId
	 * @throws Exception
	 */
	void removeUserLearningGoal(String rootOrg, String goalType, String userUUID, String goalId) throws Exception;

	/**
	 * 
	 * This method is used to insert user goals depending on various constraints.
	 * Various types of goals that are created by this method are based on the
	 * following goal types in below mentioned table:<br>
	 * <br>
	 * Table <b>user_learning_goals:</b> <br>
	 * a) user - Custom goal created by user for himself. <br>
	 * b) common - Goals that are pre-created and listed for all users to choose
	 * from.<br>
	 * c) tobeshared- Goal created for sharing with others but not shared yet. This
	 * goal type is deleted once the goal has been shared with even one person.<br>
	 * d) commonshared- Choosing a common goal for sharing but not shared yet. This
	 * goal type is deleted once the goal has been shared with even one person.<br>
	 * 
	 *
	 * @param rootOrg
	 * @param org
	 * @param userGoalsData
	 * @param userUUID
	 * @return
	 * @throws Exception
	 */
	void createLearningGoal(String rootOrg, GoalDTO userGoalsData, String userUUID) throws Exception;

	/**
	 * This method is used to insert shared goals depending on various constraints.
	 * Various types of goals that are created by this method are based on the
	 * following goal types in below mentioned table:<br>
	 * <br>
	 * Table <b>user_shared_goals:</b> <br>
	 * a) common_shared - Common goal created by user for others. <br>
	 * b) custom_shared - Custom goal created by user for others.<br>
	 * 
	 * 
	 * @param recipients
	 * @param userUUID
	 * @param goalId
	 * @param goalType
	 * @throws Exception
	 */
	Map<String, Object> shareGoal_v1(List<String> recipients, String userUUID, String goalId, String goalType,
			String rootOrg, String message) throws Exception;

	Map<String, Object> shareGoal(List<String> recipients, String userUUID, String goalId, String goalType,
			String rootOrg, String message) throws Exception;

	/**
	 * This mehtod is used for taking action on a goal that ha been shared to the
	 * user by someone. Action can take 2 values:<br>
	 * a) Accept a goal (accept) <br>
	 * b) Reject a goal (reject)<br>
	 * <br>
	 * 
	 * While taking action, a check has been made whether or not, in case of any
	 * common goal shared to the user, has been added by self or accepted when some
	 * one else shared the goal. In the former case, the goal must be deleted from
	 * ULG and the newly shared goal will be kept. In the latter case, a duplicate
	 * goal record will be created for the newly shared goal while the previous goal
	 * record will also be kept. <br>
	 * Furthermore, in case of accept, the goal in user_shared_goals table gets
	 * updated with the calculation of goal_start_date and goal_end_date. Also the
	 * value of status field gets updated to 1 indicating user acceptance.<br>
	 * In case of reject, the goal in user_shared_goals table gets updated with the
	 * value of status field which gets updated to -1 indicating user rejection.
	 * 
	 * @param userUUID
	 * @param goalId
	 * @param action
	 * @param sharedBy
	 * @param goalType
	 * @param message
	 * @throws Exception
	 */
	Map<String, Object> takeAction(String action, ActionDTO actionData, Boolean confirm, String userUUID, String goalId,
			String rootOrg) throws Exception;

	/**
	 * This method is used to remove specific users from a shared goal. In case, all
	 * the users are removed from a goal, it must be treated as a not-yet-shared
	 * goal.
	 * 
	 * @param targetIdsMap &nbsp; The target ids which should be removed from the
	 *                     goal
	 * @param userUUID     &nbsp; The user's id
	 * @param goalId       &nbsp; The id of the shared goal
	 * @param goalType     &nbsp; The type of the goal
	 */
	public Map<String, Object> removeGoalSharing_v1(Map<String, Object> targetIdsMap, String userUUID, String goalId,
			String goalType, String rootOrg) throws Exception;

	public Map<String, Object> removeGoalSharing(Map<String, Object> targetIdsMap, String userUUID, String goalId,
			String goalType, String rootOrg) throws Exception;

	/**
	 * This method is responsible for fetching all the goals that are displayed
	 * under "MY GOALS" section. It will fetch the goals from both ULG (if you have
	 * created a common or custom goal) and USG (in case a goal has been shared with
	 * you and you've accepted it). This method is also responsible for displaying
	 * individual resource progress and over all goal completion progress
	 * 
	 * @param userUUID &nbsp; The mailId of the user
	 * 
	 * @return &nbsp; Returns completed and incomplete goals
	 */
	public Map<String, Object> fetchMyGoalsWithProgress_v1(String userUUID, String rootOrg, String language,
			List<String> meta) throws Exception;

	public Map<String, Object> fetchMyGoalsWithProgress(String userUUID, String rootOrg, String language,
			List<String> meta) throws Exception;

	public Map<String, Object> fetchUserGoalsContentWithProgress(String userUUID, String rootOrg, List<String> metaFields) throws Exception;

	/**
	 * This method is responsible for fetching all the goals that are displayed
	 * under "GOALS FOR OTHERS" section. It will fetch the goals from USG(whose
	 * shared_by is userEmail) and from ULG(whose user_id is userEmail and goal_type
	 * is either commonshared or tobeshared).
	 * 
	 * @param userUUID &nbsp;The user for whom goals should be fetched
	 * 
	 * @return The list of goals
	 * @throws Exception
	 */
	public List<Map<String, Object>> fetchGoalsSharedByMe_v1(String userUUID, String rootOrg, String language,
			List<String> meta) throws Exception;

	public List<Map<String, Object>> fetchGoalsSharedByMe(String userUUID, String rootOrg, String language,
			List<String> meta) throws Exception;

	/**
	 * This method is responsible for fetching all the goals that are displayed
	 * under "Action Required" section. It will fetch the goals from USG (in case a
	 * goal has been shared with user and the user has not taken any action on it).
	 * This method will fetch the goals that are still awaiting an
	 * action(accept/reject) by the user.<br>
	 * In other words, those goal details are fetched from the user_shared_goals
	 * table which have been shared with the user and whose status field is 0.
	 * 
	 * @param userUUID &nbsp; The mailId of the user
	 * @return List of goals for My Action.
	 */

	public List<Map<String, Object>> fetchGoalsForAction_v1(String userUUID, String rootOrg, String language,
			List<String> meta) throws Exception;

	public List<Map<String, Object>> fetchGoalsForAction(String userUUID, String rootOrg, String language,
			List<String> meta) throws Exception;

	/**
	 * This method is responsible for displaying the goals list in homepage whenever
	 * the user tries to add a resource to his own goal by pressing the add-to-goals
	 * button. Depending on the goalType, this method can add the new resource to
	 * either custom goals or tobeshared goals.
	 * 
	 * @param userUUID &nbsp; The mailId of the user
	 * @param goalType &nbsp; The type of the goal
	 * 
	 * @return The list of goals
	 */
	public Map<String, Object> fetchGoalsByGoalType(String userUUID, String rootOrg, String goalType) throws Exception;

//	public Map<String, Object> fetchGoalsByGoalType(String userUUID, String rootOrg, String goalType) throws Exception;

	/**
	 * This method is responsible for the tracking feature of the shared goals.<br>
	 * It can determine the number of acceptors, rejectors and number of pending
	 * requests for a shared goal, along with the progress for the acceptors.
	 * 
	 * @param userUUID &nbsp; The mailId of the user
	 * @param goalId   &nbsp; The id of the goal
	 * @param goalType &nbsp; The type of the goal
	 * 
	 * @return The goal details along with progress for accepted goals
	 * 
	 * @throws Exception
	 */
	public Map<String, Object> trackSharedGoal_v1(String rootOrg, String userUUID, String goalId, String goalType,
			List<String> meta) throws Exception;

	public Map<String, Object> trackSharedGoal(String rootOrg, String userUUID, String goalId, String goalType,
			List<String> meta) throws Exception;

	/**
	 * This method is responsible for fetching the common goals visible under
	 * "Suggested Goals" section. Generally, the end user can only create a common
	 * goal only two times, that is: <br>
	 * a) For self <br>
	 * b) For others <br>
	 * 
	 * Once the user has created a common goal for self as well as for others, that
	 * goal must not be visible <br>
	 * to the user in the suggested goals section. Also, while creating any common
	 * goal, a warning must be <br>
	 * shown to the user informing the user that the goal has already been created
	 * for self/others. <br>
	 * The suggested goals will be visible as a grouped entity, grouped by their
	 * goal groups.
	 * 
	 * @param rootOrg
	 * @param org
	 * @param userUUID
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> getSuggestedGoals(String rootOrg, String userUUID, String language) throws Exception;

	/**
	 * This method is used to update a common goal duration created by the user for
	 * self/others.
	 * 
	 * @param userUUID
	 * @param goalType
	 * @param goalId
	 * @param duration
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> updateCommonGoalDuration(String rootOrg, String userUUID, String goalType, String goalId,
			int duration) throws Exception;

	/**
	 * This method is responsible for adding a single content to a goal. While
	 * adding the content, it needs to be checked if its parent already is present
	 * in the goal or its children are present in the goal. Depending on either
	 * conditions, appropriate action needs to be taken. Also, the new goal duration
	 * needs to be calculated based on the addition of a new content.
	 * 
	 * @param userUUID
	 * @param goalId
	 * @param lexId
	 * @param goalType
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> addContentToUserGoal(String userUUID, String goalId, String lexId, String goalType,
			String rootOrg) throws Exception;

	/**
	 * This method is used to check whether in the requested goal content list,
	 * there exists already a parent of the latest resource being added. It also
	 * checks whether any children of the latest resource being added already exists
	 * in the goal content list. <br>
	 * If the case is former, then the latest resource will not be added. In the
	 * latter case, the children needs to be removed and the parent must be added.
	 * 
	 * @param goalContentList
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> updateGoalReosurces(List<String> goalContentList) throws Exception;

	/**
	 * This method is responsible for deleting a single content from a goal. While
	 * deleting the content, it needs to be checked if after deleting it there are
	 * no more contents remaining in the content list of the goal. If it does then
	 * the goal must be deleted from the user_learning_goals table. Else the content
	 * will only be deleted. Also, the new goal duration needs to be calculated
	 * based on the deletion of a content.
	 * 
	 * @param userUUID
	 * @param goalId
substitute url based on requirement
	 * @param goalType
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> deleteResourceFromUserGoal(String rootOrg, String userUUID, String goalId, String lexId,
			String goalType) throws Exception;

	List<Map<String, Object>> getGoalGroups(String rootOrg, String userUUID, String language)
			throws JsonMappingException, IOException;

	Map<String, Object> getSuggestedGoalsByGoalGroup(String rootOrg, String userUUID, String goalGroup, String language,
			List<String> meta) throws Exception;

	List<Map<String, Object>> getAllCommonGoals(String rootOrg);
}
