package org.sunbird.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sunbird.common.model.SunbirdApiResp;
import org.sunbird.common.model.SunbirdApiRespContent;
import org.sunbird.common.util.CbExtServerProperties;
import org.sunbird.common.util.Constants;
import org.sunbird.core.exception.ApplicationLogicError;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author akhilesh.kumar05
 *
 */
@Service
public class UserUtilityServiceImpl implements UserUtilityService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	CbExtServerProperties props;

	@Override
	public boolean validateUser(String rootOrg, String userId, String authorization, String xAuthenticatedUserToken)
			throws ApplicationLogicError {

		Map<String, Object> requestMap = new HashMap<String, Object>();

		Map<String, Object> request = new HashMap<String, Object>();

		Map<String, String> filters = new HashMap<String, String>();
		filters.put(Constants.USER_ID, userId);
		request.put(Constants.FILTERS, filters);

		requestMap.put("request", request);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authorization);
		headers.add("X-Authenticated-User-Token", xAuthenticatedUserToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			String reqBodyData = new ObjectMapper().writeValueAsString(requestMap);

			HttpEntity<String> requestEnty = new HttpEntity<>(reqBodyData, headers);

			String serverUrl = props.getSbUrl() + "api/user/v1/search";

			SunbirdApiResp sunbirdApiResp = restTemplate.postForObject(serverUrl, requestEnty, SunbirdApiResp.class);

			if (sunbirdApiResp != null && "OK".equalsIgnoreCase(sunbirdApiResp.getResponseCode())
					&& sunbirdApiResp.getResult().getResponse().getCount() >= 1) {
				return true;
			} else
				return false;

		} catch (Exception e) {
			throw new ApplicationLogicError("Sunbird Service ERROR: ", e);
		}

	}

	@Override
	public Map<String, Object> getUsersDataFromUserIds(String rootOrg, List<String> userIds, List<String> source,
			String authorization, String xAuthenticatedUserToken) {

		Map<String, Object> result = new HashMap<>();

		Map<String, Object> requestMap = new HashMap<String, Object>();
		Map<String, Object> request = new HashMap<String, Object>();

		Map<String, String> filters = new HashMap<String, String>();
		// filters.put(Constants.ROOT_ORG, rootOrg);
		filters.put(Constants.USER_ID, userIds.get(0).toString());
		request.put(Constants.FILTERS, filters);

		requestMap.put("request", request);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authorization);
		headers.add("X-Authenticated-User-Token", xAuthenticatedUserToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			String reqBodyData = new ObjectMapper().writeValueAsString(requestMap);

			HttpEntity<String> requestEnty = new HttpEntity<>(reqBodyData, headers);

			String serverUrl = props.getSbUrl() + "api/user/v1/search";

			SunbirdApiResp sunbirdApiResp = restTemplate.postForObject(serverUrl, requestEnty, SunbirdApiResp.class);

			if (sunbirdApiResp != null && "OK".equalsIgnoreCase(sunbirdApiResp.getResponseCode())
					&& sunbirdApiResp.getResult().getResponse().getCount() >= 1) {
				SunbirdApiRespContent content = sunbirdApiResp.getResult().getResponse().getContent().get(0);
				result.put(userIds.get(0).toString(), content);
				return result;
			}

		} catch (Exception e) {
			throw new ApplicationLogicError("Sunbird Service ERROR: ", e);
		}

		return result;
	}
}