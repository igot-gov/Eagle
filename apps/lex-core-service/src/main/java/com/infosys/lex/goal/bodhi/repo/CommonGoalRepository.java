/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.bodhi.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CommonGoalRepository extends CrudRepository<CommonGoal, CommonGoalKey> {

	@Query(nativeQuery = true, value = "select * from wingspan.common_learning_goals where root_org=?1 and id=?2 and version=?3")
	public List<CommonGoal> findCommonGoalsByOrgs(String rootOrg, String goalId, Float version);

	@Query(nativeQuery = true, value = "select clg_lang.id as Id,clg_lang.language as Language,clg_lang.goal_desc as GoalDesc,clg_lang.goal_title as GoalTitle,clg_lang.group_name as GroupName,clg.goal_content_id as GoalContentId,clg.created_on as CreatedOn,clg.updated_on as UpdatedOn,clg.created_by as CreatedBy,clg.version as Version,clg.group_id as GroupId from wingspan.common_learning_goals clg inner join wingspan.common_learning_goals_language clg_lang on clg.root_org=clg_lang.root_org and clg.id=clg_lang.id and clg.version=clg_lang.version where clg_lang.root_org=?1 and clg_lang.id in ?2 and clg_lang.language in ?3 and clg_lang.version in ?4")
	public List<CommonGoalProjection> fetchCommonGoalsByRootOrgIdAndLanguage(String rootOrg, List<String> goalIds,
			List<String> language, List<Float> versions);

	@Query(nativeQuery = true, value = "select clg_lang.id as Id,clg_lang.language as Language,clg_lang.goal_desc as GoalDesc,clg_lang.goal_title as GoalTitle,clg_lang.group_name as GroupName,clg.goal_content_id as GoalContentId,clg.created_on as CreatedOn,clg.updated_on as UpdatedOn,clg.created_by as CreatedBy,clg.version as Version,clg.group_id as GroupId from wingspan.common_learning_goals clg inner join wingspan.common_learning_goals_language clg_lang on clg.root_org=clg_lang.root_org and clg.id=clg_lang.id and clg.version=?3 where clg_lang.root_org=?1 and clg_lang.language in ?2")
	public List<CommonGoalProjection> fetchAllCommonGoalsByRootOrgAndLanguage(String rootOrg, List<String> language,
			Float version);

	@Query(nativeQuery = true, value = "select distinct clg.group_id as GroupId,clg_lang.group_name as GroupName,clg_lang.language as Language from wingspan.common_learning_goals clg inner join wingspan.common_learning_goals_language clg_lang on clg.root_org=clg_lang.root_org and clg.id=clg_lang.id and clg.version=?3 where clg_lang.root_org=?1 and clg_lang.language in ?2")
	public List<GroupProjection> fetchAllCommonGoalGroups(String rootOrg, List<String> languages, Float version);

	@Query(nativeQuery = true, value = "select clg_lang.id as Id,clg_lang.language as Language,clg_lang.goal_desc as GoalDesc,clg_lang.goal_title as GoalTitle,clg_lang.group_name as GroupName,clg.goal_content_id as GoalContentId,clg.created_on as CreatedOn,clg.updated_on as UpdatedOn,clg.created_by as CreatedBy,clg.version as Version,clg.group_id as GroupId from wingspan.common_learning_goals clg inner join wingspan.common_learning_goals_language clg_lang on clg.root_org=clg_lang.root_org and clg.id=clg_lang.id and clg.version=?3 where clg_lang.root_org=?1 and clg_lang.language in ?2 and clg.group_id=?4")
	public List<CommonGoalProjection> fetchAllCommonGoalsByGoalGroup(String rootOrg, List<String> language,
			Float version, String goalGroup);

	@Query(nativeQuery = true, value = "select clg_lang.id as Id,clg_lang.language as Language,clg_lang.goal_desc as GoalDesc,clg_lang.goal_title as GoalTitle,clg_lang.group_name as GroupName,clg.goal_content_id as GoalContentId,clg.created_on as CreatedOn,clg.updated_on as UpdatedOn,clg.created_by as CreatedBy,clg.version as Version,clg.group_id as GroupId from wingspan.common_learning_goals clg inner join wingspan.common_learning_goals_language clg_lang on clg.root_org=clg_lang.root_org and clg.id=clg_lang.id and clg.version=clg_lang.version where clg_lang.root_org=?1")
	public List<CommonGoalProjection> fetchAllCommonGoalsByRootOrg(String rootOrg);

}
