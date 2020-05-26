/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.bodhi.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CommonGoalLangRepository extends CrudRepository<CommonGoalLang, CommonGoalLangKey> {

	@Query(nativeQuery = true, value = "Select c1.id as \"Id\",c1.goal_desc as \"GoalDesc\","
			+ "c1.goal_title as \"GoalTitle\",c1.goal_group as \"GoalGroup\",c2.created_on "
			+ "as \"CreatedOn\",c2.goal_content_id as \"GoalContentId\" "
			+ "from goals.common_learning_goals_lang c1 inner join goals.common_learning_goals_v2 "
			+ "c2 on c1.id=c2.id where c1.root_org=?1 and org=?2 and c1.language=?3 and c1.id=?4")
	public CommonGoalProjection getCommonGoalDetails(String rootOrg, String org, String language, String id);

	@Query(nativeQuery = true, value = "select * from wingspan.common_learning_goals_language where root_org=?1 and id=?2 and version=?4 and language in ?3")
	public CommonGoalLang fetchCommonGoalDetails(String rootOrg, String goalId, List<String> language, Float version);

}
