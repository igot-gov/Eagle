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

import java.util.List;

public interface UserConnectionRepository
		extends CassandraRepository<UserConnection, UserConnectionPrimarykey> {

	@Query("SELECT * from user_connection where user_id=?0 ALLOW FILTERING;")
	public List<UserConnection> findAllByUser(String userId);

	@Query("SELECT * from user_connection where user_id=?0 AND connection_status=?1 ALLOW FILTERING;")
	public List<UserConnection> findByUserAndStatus(String userId, String status);

	@Query("SELECT * from user_connection where user_id=?0 AND connection_status=?1 AND connection_type=?2 ALLOW FILTERING;")
	public List<UserConnection> findByUserAndTypeAndStatus(String userId, String type, String status);

	@Query("SELECT * FROM user_connection WHERE user_id IN ?0 AND connection_status=?1 ALLOW FILTERING;")
	public List<UserConnection> findByUsersAndStatus(List<String> userIds, String status);

	@Query("SELECT * FROM user_connection WHERE user_id=?0 AND connection_id=?1 ALLOW FILTERING;")
	public UserConnection findByUsersAndConnection(String userId, String connectionId);

	@Query("SELECT * from user_connection where connection_id=?0 AND connection_status=?1 ALLOW FILTERING;")
	public List<UserConnection> findByConnectionAndStatus(String connectionId, String status);

}
