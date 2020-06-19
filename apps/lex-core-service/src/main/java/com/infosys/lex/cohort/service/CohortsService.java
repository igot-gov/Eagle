/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.cohort.service;

import java.util.List;

import com.infosys.lex.cohort.bodhi.repo.CohortUsers;

public interface CohortsService {

	List<CohortUsers> getUserWithCommonGoals(String rootOrg,String resourceId,String userUUID, int count) throws Exception;


	List<CohortUsers> getAuthors(String rootOrg,String resourceId, String userUUID, int count) throws Exception;

	List<CohortUsers> getEducators(String rootOrg,String resourceId, String userUUID, int count) throws Exception;

	List<CohortUsers> getTopPerformers(String rootOrg,String resourceId, String userUUID, int count) throws Exception;

	

	List<CohortUsers> getActiveUsers(String rootOrg, String contentId, String userUUID, int count,
			 Boolean toFilter) throws Exception;
	
	

	

}
