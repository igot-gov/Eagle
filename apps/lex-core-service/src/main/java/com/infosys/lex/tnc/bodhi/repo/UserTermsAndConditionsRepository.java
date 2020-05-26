/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.tnc.bodhi.repo;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTermsAndConditionsRepository
		extends CassandraRepository<UserTermsAndConditions, UserTermsAndConditionsPrimaryKey> {

	@Query("Select * from bodhi.user_terms_condition where root_org=?0 and user_id=?1 and doc_name='Generic T&C' and doc_for in ('User','Author')")
	List<UserTermsAndConditions> findUserByRootOrg(String rootOrg, String userId);

	@Query("Select * from bodhi.user_terms_condition where root_org=?0 and user_id in ?1 and doc_name='Generic T&C' and doc_for in ('User','Author')")
	List<UserTermsAndConditions> findUsersByRootOrg(String rootOrg, List<String> userId);

	@Query("Select * from bodhi.user_terms_condition where root_org=?0 and user_id=?1 and doc_name in ?2 and doc_for in ('User','Author')")
	List<UserTermsAndConditions> findUserRecord(String rootOrg, String userId, List<String> docName);
}
