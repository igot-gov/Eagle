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

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.model.cassandra.UserNotificationDevice;
import com.infosys.lex.notification.model.cassandra.UserNotificationDeviceKey;
import com.infosys.lex.notification.projection.UserDeviceArnsProjection;

@Repository
public interface UserDeviceRepository extends CassandraRepository<UserNotificationDevice, UserNotificationDeviceKey> {

	/**
	 * to get device arns from database
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	
	public List<UserDeviceArnsProjection> findAllByKeyRootOrgAndKeyUserId(String rootOrg,String userId);
	
	/**
	 * This method fetches the 
	 * @param rootOrg
	 * @param userId
	 * @param deviceToken
	 * @return
	 */
	public UserDeviceArnsProjection findByKeyRootOrgAndKeyUserIdAndKeyDeviceToken(String rootOrg,String userId,String deviceToken);
	

}
