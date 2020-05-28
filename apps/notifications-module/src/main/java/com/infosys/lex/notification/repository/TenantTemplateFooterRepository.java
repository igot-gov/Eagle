package com.infosys.lex.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.infosys.lex.notification.entity.TemplateFooter;
import com.infosys.lex.notification.entity.TemplateFooterPrimaryKey;

public interface TenantTemplateFooterRepository extends CrudRepository<TemplateFooter, TemplateFooterPrimaryKey> {

	@Query(nativeQuery = true, value = "Select * from wingspan.tenant_template_footer where root_org=?1 and org in ?2")
	public List<TemplateFooter> getFooterEmailsForGivenOrgs(String rootOrg, List<String> org);
}
