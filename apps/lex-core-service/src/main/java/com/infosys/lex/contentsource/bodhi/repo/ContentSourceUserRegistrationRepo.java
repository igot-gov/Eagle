/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.contentsource.bodhi.repo;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentSourceUserRegistrationRepo extends CassandraRepository<ContentSourceUserRegistration, ContentSourceUserRegistrationKey> {

	
	public List<ContentSourceUserRegistration> findAllByKeyRootOrgAndKeySourceShortNameAndKeyUserIdIn(String rootOrg,String sourceShortName,List<String> userIdList);
	
	public ContentSourceUserRegistrationProjection  findByKeyRootOrgAndKeySourceShortNameAndKeyUserId(String rootOrg ,String sourceShortName,String userId);
	
	public List<ContentSourceUserRegistrationProjection> findAllByKeyRootOrgAndKeySourceShortName(String rootOrg,String sourceShortName);
	
	@Query("delete from content_source_user_registration where root_org = ?0 and source_short_name = ?1 and user_id in ?2")
	public void deleteUserForSourceShortName(String rootOrg,String sourceShortName,List<String> users);

}
