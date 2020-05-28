package com.infosys.lex.notification.serviceImpl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.kafka.common.network.InvalidReceiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.infosys.lex.notification.dto.SMTPDTO;
import com.infosys.lex.notification.entity.SMTPConfig;
import com.infosys.lex.notification.entity.SMTPConfigKey;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.repository.SMTPConfigRepository;
import com.infosys.lex.notification.service.EncryptionService;
import com.infosys.lex.notification.service.SMTPConfigService;

@Service
public class SMTPConfigServiceImpl implements SMTPConfigService {

	@Autowired
	Environment env;

	@Autowired
	SMTPConfigRepository smtpRepo;

	@Autowired
	EncryptionService encryptionService;

	@Override
	public void putSMTPConfig(String rootOrg,String org, SMTPDTO data) {

		if (rootOrg == null || rootOrg.isEmpty())
			throw new InvalidReceiveException("rootOrg is mandatory");

		if (data == null)
			throw new InvalidReceiveException("smpt config data is mandatory");


		SMTPConfig config = new SMTPConfig(new SMTPConfigKey(rootOrg,org), data.getUserName(),
				data.getPassword().isEmpty() ? ""
						: encryptionService.encrypt(data.getPassword(), env.getProperty(rootOrg + ".smtpKey")),
				data.getHost(), data.getSignEmail(),data.getPort(), data.getSenderId(), new Timestamp(new Date().getTime()), "",data.getChunkSize());

		smtpRepo.save(config);
	}

	@Override
	public SMTPConfig getSMTPConfig(String rootOrg, List<String> orgs) {

		if (rootOrg == null || rootOrg.isEmpty())
			throw new ApplicationLogicException("root org is not present");
		List<SMTPConfig> smtpConfigs = null;

		// if orgs is null or empty fetch all SMTP config for rootOrg and whichever
		// first has a valid Host and sender id
		if (orgs == null || orgs.isEmpty()) {
			smtpConfigs = smtpRepo.findAllByKeyRootOrg(rootOrg);

		} else {
			smtpConfigs = smtpRepo.findAllByKeyRootOrgAndKeyOrgIn(rootOrg, orgs);

		}
		SMTPConfig validConfig = null;
		for (SMTPConfig config : smtpConfigs) {
			if (config.getHost() == null || config.getHost().isEmpty())
				continue;

			if (config.getSenderId() == null || config.getSenderId().isEmpty())
				continue;

			validConfig = config;
			break;
		}

		if (validConfig == null)
			throw new ApplicationLogicException("smtp config not found for " + rootOrg);
		// de-crypt stored password in db
		if (!validConfig.getPassword().isEmpty())
			validConfig.setPassword(
					encryptionService.decrypt(validConfig.getPassword(), env.getProperty(rootOrg + ".smtpKey")));
		return validConfig;
	}
}