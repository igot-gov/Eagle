/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.postgredb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.faq.postgredb.entity.FaqGroup;
import com.infosys.lex.faq.postgredb.entity.FaqGroupPrimaryKey;
import com.infosys.lex.faq.postgredb.projection.FaqGroupProjection;

@Repository
public interface FaqGroupRepo extends JpaRepository<FaqGroup, FaqGroupPrimaryKey> {

	@Query("select p.faqGroupPrimaryKey.groupId as groupId,p.groupName as groupName from FaqGroup p where p.faqGroupPrimaryKey.rootOrg =?1 and p.faqGroupPrimaryKey.language =?2")
	public List<FaqGroupProjection> findGroup(String rootOrg, String language);

	@Query("select p.faqGroupPrimaryKey.groupId,p.groupName from FaqGroup p where p.faqGroupPrimaryKey.rootOrg =?1 and p.faqGroupPrimaryKey.language =?2 and p.groupName=?3 ")
	public Optional<FaqGroup> getGroup(String rootOrg, String language, String groupName);

}
