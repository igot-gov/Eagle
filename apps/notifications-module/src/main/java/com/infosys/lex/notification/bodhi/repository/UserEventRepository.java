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

package com.infosys.lex.notification.bodhi.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.model.cassandra.UserEvent;
import com.infosys.lex.notification.model.cassandra.UserEventPrimaryKey;

@Repository
public interface UserEventRepository extends CassandraRepository<UserEvent, UserEventPrimaryKey> {

	@Query("select * from user_notification_event where root_org=?0 and event_id=?1 and receiving_role in ?2 and user_id in ?3")
	public List<Map<String, Object>> findUserNotificationSettings(String rootOrg, String eventId,
			List<String> receiving_roles, List<String> userId);

	@Query("Select * from user_notification_event where root_org=?0 and user_id = ?1 and receiving_role in ?2")
	public List<Map<String, Object>> findByUserId(String rootOrg, String userId, List<String> receivingRole);

}
