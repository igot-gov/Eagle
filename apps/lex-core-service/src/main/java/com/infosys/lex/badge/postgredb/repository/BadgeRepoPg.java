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
package com.infosys.lex.badge.postgredb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.badge.postgredb.entity.Badge;
import com.infosys.lex.badge.postgredb.projection.BadgeDetailsProjection;

@Repository
public interface BadgeRepoPg extends JpaRepository<Badge, String> {

	List<Badge> findByKeyRootOrgAndBadgeGroup(String rootOrg, String badgeGroup);

	@Query(nativeQuery = true, value = "select b.badge_id as badgeId, b.image, b.badge_type as badgeType, b.threshold1, b.time_period as timePeriod, b.threshold2, b.created_date as createdDate, b.created_by as createdBy, b.start_date as startDate, b.end_date as endDate, b.sharable, b.badge_group as badgeGroup, b.group_order as groupOrder, bl.language, bl.badge_name as badgeName, bl.badge_description as badgeDescription, bl.completed_description as completedDescription, bl.message, bl.badge_group_text as badgeGroupText from wingspan.badge b join wingspan.badge_language bl on b.root_org=bl.root_org and b.badge_id=bl.badge_id where b.root_org=?1 and bl.language in (?2, 'en')")
	List<BadgeDetailsProjection> fetchBadgeDetailsByRootOrgAndLanguage(String rootOrg, String language);

	List<Badge> findByKeyRootOrgAndKeyBadgeId(String rootOrg, String badgeId);

	@Query(nativeQuery = true, value = "select b.badge_id as badgeId, b.image, b.badge_type as badgeType, b.threshold1, b.time_period as timePeriod, b.threshold2, b.created_date as createdDate, b.created_by as createdBy, b.start_date as startDate, b.end_date as endDate, b.sharable, b.badge_group as badgeGroup, b.group_order as groupOrder, bl.language, bl.badge_name as badgeName, bl.badge_description as badgeDescription, bl.completed_description as completedDescription, bl.message, bl.badge_group_text as badgeGroupText from wingspan.badge b join wingspan.badge_language bl on b.root_org=bl.root_org and b.badge_id=bl.badge_id where b.root_org=?1")
	List<BadgeDetailsProjection> fetchBadgeDetailsByRootOrgAndLang(String rootOrg);

}
