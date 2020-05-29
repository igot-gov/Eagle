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

package com.infosys.lex.notification.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.entity.TenantMode;
import com.infosys.lex.notification.entity.TenantModePrimaryKey;
import com.infosys.lex.notification.projection.ModesProjection;

@Repository
public interface ModesRepository extends CrudRepository<TenantMode, TenantModePrimaryKey> {

	@Query(nativeQuery = true, value = "select mode from wingspan.tbl_tenant_mode where root_org=?1 and org=?2 and activated=true")
	public List<String> getActivatedModesForRootOrgAndOrg(String rootOrg, String org);

	@Query(nativeQuery = true, value = "select ten.root_org,ten.org,ten.mode,ten.activated from wingspan.tbl_tenant_mode def inner join wingspan.tbl_tenant_mode ten on def.mode=ten.mode where def.activated=true and def.root_org='default' and def.org='default' and ten.root_org=?1 and ten.org in ?2 and ten.activated=true")
	public List<Map<String, Object>> getActivatedTenantModesByRootOrgAndOrgs(String rootOrg, List<String> orgs);

	@Query(nativeQuery = true, value = "select md.org as Org,mlang.mode as ModeId,mlang.mode_name as "
			+ "ModeName,md.activated as ModeActivated,md.icon_id IconId , mlang.language as Language from "
			+ "wingspan.tbl_tenant_mode md inner join wingspan.tenant_mode_language "
			+ "mlang on md.mode=mlang.mode where md.root_org=?1 and md.org = ?2 "
			+ "and mlang.language in ?3 order by mlang.language")
	public List<ModesProjection> fetchTenantModesByLanguages(String rootOrg, String org, List<String> languages);

}
