package com.infosys.lex.notification.service;

import java.util.List;

import com.infosys.lex.notification.dto.SMTPDTO;
import com.infosys.lex.notification.entity.SMTPConfig;

public interface SMTPConfigService {

	void putSMTPConfig(String rootOrg,String org, SMTPDTO data);

	SMTPConfig getSMTPConfig(String rootOrg, List<String> orgs);
}
