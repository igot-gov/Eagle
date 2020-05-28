
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

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.model.cassandra.UserNotification;
import com.infosys.lex.notification.model.cassandra.UserNotificationPrimaryKey;

@Repository
public interface UserNotificationRepository
		extends CassandraRepository<UserNotification, UserNotificationPrimaryKey>, UserNotificationRepositoryCustom {

	/**
	 * to update seen value and time in table
	 * 
	 * @param seen
	 * @param seenTime
	 * @param userId
	 * @param notificationId
	 */
	@Query("update bodhi.user_notification set seen=true,seen_on=?0 where root_org=?4 and user_id=?1 and classified_as=?3 and notification_id=?2")
	public void markAsSeen(Timestamp seenTime, String userId, UUID notificationId, String classifiedAs, String rootOrg);

	@Query("select * from bodhi.user_notification where root_org=?0 and user_id=?1 and classified_as in ?2 and notification_id=?3")
	public List<UserNotification> fetchAllUserNotifications(String rootOrg, String userId, List<String> classification,
			UUID notificationId);

	@Query("select count(*) from bodhi.user_notification where root_org=?0 and user_id=?1 and classified_as=?2 and seen=false")
	public Map<String, Object> fetchUnSeenNotifCount(String rootOrg, String userId, String classifiedAs);

	@Query("select * from user_notification where root_org= ?0 and user_id= ?1 and classified_as = ?2 and seen=false limit ?3")
	public List<UserNotification> fetchUnSeenNotifications(String rootOrg, String userId, String classifiedAs,
			Integer size);

	@Query("select * from user_notification where root_org= ?0 and user_id= ?1 and classified_as in ('Action','Information') and notification_id in ?2")
	public List<UserNotification> fetchUnSeenNotifications(String rootOrg, String userId, List<UUID> notificationIds);

}