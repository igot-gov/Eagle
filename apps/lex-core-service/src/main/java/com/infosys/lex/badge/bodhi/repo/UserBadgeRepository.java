/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
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
package com.infosys.lex.badge.bodhi.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBadgeRepository
		extends CassandraRepository<UserBadgesModel, UserBadgesPrimaryKeyModel>, UserBadgeRepositoryCustom {

	/**
	 * finds badges for a user
	 * 
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> findByPrimaryKeyRootOrgAndPrimaryKeyUserId(String rootOrg, String userId);

	/**
	 * find selected badges for a user
	 * 
	 * @param userId
	 * @param badgeIds
	 * @return
	 */
	public List<UserBadgesModel> findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyBadgeIdIn(String rootOrg,
			String userId, List<String> badgeIds);

	/**
	 * find all achieved badges for a user
	 * 
	 * @param userId
	 * @return
	 */
	@Query("select * from user_badges where root_org=?0 and user_id=?1 and received_count>0 allow filtering;")
	public List<Map<String, Object>> findByRootOrgAndUserIdAndReceivedCount(String rootOrg, String userId);

	/**
	 * find all earned badges for a user
	 *
	 * @param rootOrg
	 * @param userId
	 * @return
	 */
	@Query("select * from user_badges where root_org=?0 and user_id=?1 and received_count>0 and progress= 100 allow filtering;")
	public List<UserBadgesModel> findByRootOrgAndUserIdAndReceivedCountAndProgress(String rootOrg, String userId);

	@Query("select count(badge_id) from user_badges where root_org=?0 and user_id=?1 and received_count>0 and progress= 100 allow filtering;")
	public int countForCompleted(String rootOrg, String userId);
}
