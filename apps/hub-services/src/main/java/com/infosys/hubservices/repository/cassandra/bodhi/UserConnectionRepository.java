/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.repository.cassandra.bodhi;

import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.model.cassandra.UserConnectionPrimarykey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface UserConnectionRepository
		extends CassandraRepository<UserConnection, UserConnectionPrimarykey> {


	@Query("SELECT count(user_id) from user_connection where root_org=?0 AND user_id=?1 ;")
	public int countByUser(String rootOrg, String userId);

	@Query("SELECT count(user_id) from user_connection where user_id=?0 AND connection_status=?1 ALLOW FILTERING;")
	public int countByUserAndStatus(String userId, String status);

	Slice<UserConnection> findByUserConnectionPrimarykeyRootOrgAndUserConnectionPrimarykeyUserId(String rootOrg, String userId,  Pageable pageable);

	//Slice<UserConnection> findByUserConnectionPrimarykeyRootOrgAndUserConnectionPrimarykeyConnectionId(String rootOrg, String connectionId,  Pageable pageable);

	@Query("SELECT * FROM user_connection WHERE root_org=?0 AND connection_id=?1 AND connection_status=?2 ALLOW FILTERING;")
	public List<UserConnection> findByConnection(String rootOrg, String connectionId, String status);

	@Query("SELECT * FROM user_connection WHERE root_org=?0 AND user_id IN ?1 ;")
	public List<UserConnection> findByUsersAndRootOrg(String rootOrg, List<String> userIds);


	@Query("SELECT * FROM user_connection WHERE root_org=?0 AND user_id=?1 AND connection_id=?2 ;")
	public UserConnection findByUsersAndConnection(String rootOrg, String userId, String connectionId);

}
