/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.language.postgredb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.language.entity.LanguageTenant;
import com.infosys.lex.language.projection.LanguageProjection;

@Repository
public interface LanguageTenantRepo extends JpaRepository<LanguageTenant, String> {

	@Query(nativeQuery = true, value = "select tenant.root_org as rootOrg,tenant.language as language,master.country as country,master.locales as locales,master.flag_url as flagUrl,master.country_user_friendly as countryUserFriendly,master.language_user_friendly as languageUserFriendly from language_tenant as tenant inner join language_master as master on tenant.language=master.language and tenant.root_org=?1 and tenant.language=?2 and master.country=?3")
	public LanguageProjection findRecord(String rootOrg, String lang,String country);
	
	@Query(nativeQuery=true,value="select tenant.id,tenant.root_org,tenant.language from language_tenant tenant where tenant.root_org=?1 and tenant.language=?2")
	public LanguageTenant findTenantRecord(String rootOrg, String lang);

	@Modifying
	@Query(nativeQuery=true,value="delete from language_tenant where root_org=?1 and language=?2")	
	public void delete(String rootOrg, String lang);
}
