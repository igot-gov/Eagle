package com.infosys.lex.notification.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.entity.SMTPConfig;
import com.infosys.lex.notification.entity.SMTPConfigKey;

@Repository
public interface SMTPConfigRepository extends CrudRepository<SMTPConfig, SMTPConfigKey> {
	
	List<SMTPConfig> findAllByKeyRootOrgAndKeyOrgIn(String rootOrg,List<String> orgs);
	
	List<SMTPConfig> findAllByKeyRootOrg(String rootOrg);

}
