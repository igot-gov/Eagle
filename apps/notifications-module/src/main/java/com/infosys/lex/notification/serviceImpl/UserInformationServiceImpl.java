package com.infosys.lex.notification.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.notification.bodhi.repository.AppConfigRepository;
import com.infosys.lex.notification.bodhi.repository.UserPreferencesRepository;
import com.infosys.lex.notification.dto.UserInfo;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.model.cassandra.UserPreferencesModel;
import com.infosys.lex.notification.properties.ApplicationServerProperties;
import com.infosys.lex.notification.service.UserInformationService;
import com.infosys.lex.notification.service.UserNotificationsConfigurationService;
import com.infosys.lex.notification.util.LexNotificationLogger;
import com.infosys.lex.notification.util.PIDConstants;
import com.infosys.lex.notification.util.ProjectCommonUtil;

@Service
public class UserInformationServiceImpl implements UserInformationService {

	@Autowired
	UserNotificationsConfigurationService userNotificationConfigService;

	@Autowired
	AppConfigRepository appConfigRepo;

	@Autowired
	ApplicationServerProperties appServerProps;

	@Autowired
	UserPreferencesRepository userPreferencesRepo;

	@Autowired
	RestTemplate restTemplate;

	private static final ObjectMapper mapper = new ObjectMapper();

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	@Override
	public Map<String, UserInfo> getUserInfo(String rootOrg, List<String> userIds) {

		Map<String, UserInfo> usersInfoMap = new HashMap<>();
		getUsersPidInfo(rootOrg, userIds, usersInfoMap);

//		if (usersInfoMap.keySet().size() != userIds.size())
//			throw new ApplicationLogicException("Could not fetch data for all user userIds ->" + userIds.toString()
//					+ ", fetched pid for userIds " + usersInfoMap.keySet());

		return usersInfoMap;
	}

	@Override
	public Map<String, UserInfo> getUserInfoAndNotificationPreferences(String rootOrg, String eventId,
			List<String> recipientRoles, List<String> userIds) {

		Map<String, UserInfo> usersInfoMap = new HashMap<>();
		getUsersPidInfo(rootOrg, userIds, usersInfoMap);

//		if (usersInfoMap.keySet().size() != userIds.size())
//			throw new ApplicationLogicException("Could not fetch data for all user userIds ->" + userIds.toString());

		getUsersLangaugePreferences(rootOrg, userIds, usersInfoMap);
		getUsersNotificationPreferences(rootOrg, eventId, recipientRoles, userIds, usersInfoMap);
		return usersInfoMap;
	}

	private void getUsersNotificationPreferences(String rootOrg, String eventId, List<String> recipientRoles,
			List<String> userIds, Map<String, UserInfo> usersInfoMap) {

		// get modes configured by user's for the event and given recipient role in
		// request body
		userNotificationConfigService.getUserNotificationPreferences(rootOrg, eventId, recipientRoles, userIds)
				.forEach(userNotificationConfigMap -> {

					String userId = (String) userNotificationConfigMap.get("user_id");
					String recipientRole = (String) userNotificationConfigMap.get("receiving_role");

					usersInfoMap.get(userId).getRecieveConfig().put(recipientRole,
							userNotificationConfigMap.get("modes_activated"));
				});
	}

	private void getUsersPidInfo(String rootOrg, List<String> userIds, Map<String, UserInfo> usersInfoMap) {

		getUserInfoFromPid(rootOrg, userIds, usersInfoMap);

		if (usersInfoMap.isEmpty())
			throw new ApplicationLogicException(
					"Could not fetch users for rootOrg " + rootOrg + " and userIds " + userIds.toString());
	}

	@SuppressWarnings("unchecked")
	private void getUserInfoFromPid(String rootOrg, List<String> userIds, Map<String, UserInfo> usersInfoMap) {

		String url = "http://" + appServerProps.getPidServiceUrl() + "/user/multi-fetch/wid";
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("source_fields", Arrays.asList("email", "first_name", "last_name", "org", "wid"));
		Map<String, Object> conditions = new HashMap<>();
		conditions.put("root_org", rootOrg);
		requestBody.put("conditions", conditions);
		requestBody.put("values", userIds);

		try {
			List<Map<String, Object>> responseMaps = restTemplate.postForObject(url, requestBody, List.class);
			for (Map<String, Object> responseMap : responseMaps) {

				UserInfo userInfo = new UserInfo(rootOrg, (String) responseMap.get("wid"),
						responseMap.get("first_name") != null ? responseMap.get("first_name").toString() : "",
						responseMap.get("last_name") != null ? responseMap.get("last_name").toString() : "",
						(String) responseMap.get("email"), ProjectCommonUtil.getDisplayName(responseMap),
						Arrays.asList((String) responseMap.get("org")), new HashMap<>(), Arrays.asList("en"));

				usersInfoMap.put(responseMap.get("wid").toString(), userInfo);
			}
		} catch (HttpStatusCodeException e) {
			throw new ApplicationLogicException("user info service(pid) response status " + e.getStatusCode()
					+ " message " + e.getResponseBodyAsString());
		} catch (Exception e) {
			throw new ApplicationLogicException("Could not Parse user pid data");
		}

		if (usersInfoMap.size() != userIds.size())
			logger.error("could not extract pid for all the user " + userIds.toString());
	}

	private void getUsersLangaugePreferences(String rootOrg, List<String> userIds, Map<String, UserInfo> usersInfoMap) {

		List<UserPreferencesModel> userPreferences = userPreferencesRepo.getPreferencesByRootOrgAndUserIds(rootOrg,
				userIds);

		for (UserPreferencesModel userPreference : userPreferences) {
			List<String> preferedLanguages = new ArrayList<>();
			preferedLanguages.add("en");
			String preferedLanguage = "";
			try {
				Map<String, Object> preferenceData = mapper.readValue(userPreference.getPreference(),
						new TypeReference<Map<String, Object>>() {
						});
				if (preferenceData.containsKey("selectedLangGroup")) {
					preferedLanguage = preferenceData.get("selectedLangGroup").toString();
					if (!preferedLanguage.isEmpty())
						preferedLanguages = ProjectCommonUtil.convertLanguagePreferencesToList(preferedLanguage);
				}
			} catch (IOException ex) {
				logger.error("could not fetch prefered languages for user");
			}
			usersInfoMap.get(userPreference.getUserPreferencePrimaryKey().getUserId())
					.setPreferedLanguages(preferedLanguages);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean validateUser(String rootOrg, String userId) throws Exception {

		Map<String, Object> pidRequestMap = new HashMap<String, Object>();
		pidRequestMap.put("source_fields", Arrays.asList("wid", "root_org"));
		Map<String, String> conditions = new HashMap<String, String>();
		conditions.put("root_org", rootOrg);
		conditions.put("wid", userId);
		pidRequestMap.put("conditions", conditions);

		try {

			String url = "http://" + appServerProps.getPidServiceUrl() + "/user";
			List<Map<String, Object>> pidResponse = restTemplate.postForObject(url, pidRequestMap, List.class);

			if (pidResponse.isEmpty() || pidResponse == null) {
				return false;
			} else {
				return (pidResponse.get(0).get("wid").equals(userId));
			}

		} catch (Exception e) {
			throw new ApplicationLogicException("PID ERROR: ", e);
		}

	}

	/**
	 * This method returns the manager_id mapped to the user_id(For whom the manager
	 * id exists/not null)
	 * 
	 * @param rootOrg
	 * @param userIds
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchManager(String rootOrg, List<String> userIds) {
		Map<String, String> respMap = new HashMap<>();
		try {
		
		String url = "http://" + appServerProps.getPidServiceUrl() + "/user/multi-fetch/wid";
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("source_fields", Arrays.asList(PIDConstants.WID, PIDConstants.MANAGER_ID));
		Map<String, Object> conditions = new HashMap<>();
		conditions.put("root_org", rootOrg);
		requestBody.put("conditions", conditions);
		requestBody.put("values", userIds);

		
			List<Map<String, Object>> responseMaps = restTemplate.postForObject(url, requestBody, List.class);
			for (Map<String, Object> responseMap : responseMaps) {

				if (responseMap.get(PIDConstants.MANAGER_ID) != null)
					respMap.put(responseMap.get(PIDConstants.WID).toString(), responseMap.get(PIDConstants.MANAGER_ID).toString());
			}
		} catch (HttpStatusCodeException e) {
			throw new ApplicationLogicException("user info service(pid) response status " + e.getStatusCode()
					+ " message " + e.getResponseBodyAsString());
		} catch (Exception e) {
			throw new ApplicationLogicException("Could not Parse user pid data");
		}

		return respMap;
	}

}