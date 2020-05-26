/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.bodhi.repo;

import org.springframework.data.repository.CrudRepository;

public interface CommonGoalPGRepository extends CrudRepository<CommonGoalPG, String> {

//	@Query(nativeQuery = true, value = "Select * from goals.common_learning_goals where root_org=?1 and org=?2 and language=?3")
//	public List<CommonGoalPG> fetchAllCommonGoalsByRootOrgAndOrg(String rootOrg, String org, String language);
//
//	@Query(nativeQuery = true, value = "Select * from goals.common_learning_goals where root_org=?1 and org=?2 and id=?3 limit 1")
//	public CommonGoalPG fetchACommonGoalByRootOrgAndOrg(String rootOrg, String org, String goalId);
//
//	@Query(nativeQuery = true, value = "Select * from goals.common_learning_goals where root_org=?1 and org=?2 and id in ?3 and language=?4")
//	public List<CommonGoalPG> fetchAllCommonGoalsByRootOrgAndOrgAndLanguage(String rootOrg, String org,
//			List<String> goalIds, String language);
}
