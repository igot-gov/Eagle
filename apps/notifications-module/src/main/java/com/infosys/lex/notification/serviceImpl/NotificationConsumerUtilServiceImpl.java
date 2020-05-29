package com.infosys.lex.notification.serviceImpl;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.notification.bodhi.repository.NotificationErrorsRepo;
import com.infosys.lex.notification.entity.TemplateFooter;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.model.cassandra.NotificationErrors;
import com.infosys.lex.notification.model.cassandra.NotificationErrorsPrimaryKey;
import com.infosys.lex.notification.properties.ApplicationServerProperties;
import com.infosys.lex.notification.repository.TenantTemplateFooterRepository;
import com.infosys.lex.notification.service.NotificationConsumerUtilService;
import com.infosys.lex.notification.util.LexNotificationLogger;

@Service
public class NotificationConsumerUtilServiceImpl implements NotificationConsumerUtilService {

	@Autowired
	NotificationErrorsRepo notificationErrorsRepo;

	@Autowired
	TenantTemplateFooterRepository templateFooterRepo;

	@Autowired
	ApplicationServerProperties appServerProps;

	@Autowired
	RestTemplate restTemplate;

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void saveError(String rootOrg, String eventId, Exception e, Object requestBody) {

		try {
			notificationErrorsRepo.save(new NotificationErrors(new NotificationErrorsPrimaryKey(rootOrg, eventId),
					e.getMessage(), mapper.writeValueAsString(requestBody), e.getStackTrace().toString()));
		} catch (Exception e1) {
			logger.error("could not save error event for rootOrg" + rootOrg + " and eventId " + eventId + "req body "
					+ requestBody.toString());
		}
	}

	@Override
	public Map<String, String> getOrgDomainMap(String rootOrg) {
		Map<String, String> orgDomainMap = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		String domainName;
		String org;

		String url = "http://" + appServerProps.getPidServiceUrl() + "/org/" + rootOrg;

		List<Map<String, Object>> responseMap;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class);

			responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<List<Map<String, Object>>>() {
					});

			if (responseMap.isEmpty())
				throw new ApplicationLogicException("No domain found for the given rootOrg");

			for (Map<String, Object> map : responseMap) {
				org = map.get("org").toString();
				domainName = map.get("domain_name").toString();
				if (!domainName.contains("https://"))
					domainName = "https://" + domainName;
				orgDomainMap.put(org, domainName);

			}
		} catch (HttpStatusCodeException ex) {
			throw new ApplicationLogicException("Pid domain service error : " + ex.getResponseBodyAsString());
		} catch (IOException e) {
			throw new ApplicationLogicException("Error fetching domain for rootOrg,org. Err : " + e.getMessage());
		}
		return orgDomainMap;
	}

	@Override
	public Map<String, String> getOrgFooterEmailMap(String rootOrg, List<String> orgs) {

		List<TemplateFooter> templateFooterEmails = templateFooterRepo.getFooterEmailsForGivenOrgs(rootOrg, orgs);

		Map<String, String> returnMap = new HashMap<>();
		for (TemplateFooter footer : templateFooterEmails) {
			returnMap.put(footer.getPrimaryKey().getOrg(), footer.getAppEmail());
		}
		return returnMap;
	}

	/**
	 * This method checkes if the timestamp is less no of days configured
	 * 
	 * @param timestamp
	 * @return
	 */
	@Override
	public boolean checkEventTimestamp(long timestamp) {

		Calendar eventDateTime = Calendar.getInstance();
		eventDateTime.setTimeInMillis(timestamp);

		Calendar filterDateTime = Calendar.getInstance();
		filterDateTime.add(Calendar.DAY_OF_YEAR, -appServerProps.getKafkaConsumerFilterDays());

		if (eventDateTime.after(filterDateTime))
			return true;
		else
			return false;

	}

}
