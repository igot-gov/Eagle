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
import org.sunbird.common.model.OpenSaberApiResp;
import org.sunbird.common.model.OpenSaberApiUserProfile;
import org.sunbird.common.model.SunbirdApiResp;
import org.sunbird.common.util.CbExtServerProperties;
import org.sunbird.common.util.Constants;
import org.sunbird.core.exception.ApplicationLogicError;
import org.sunbird.core.logger.CbExtLogger;

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

	private CbExtLogger logger = new CbExtLogger(getClass().getName());

	@Override
	public boolean validateUser(String rootOrg, String userId) throws ApplicationLogicError {

		Map<String, Object> requestMap = new HashMap<String, Object>();

		Map<String, Object> request = new HashMap<String, Object>();

		Map<String, String> filters = new HashMap<String, String>();
		filters.put(Constants.USER_ID, userId);
		request.put(Constants.FILTERS, filters);

		requestMap.put("request", request);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			String reqBodyData = new ObjectMapper().writeValueAsString(requestMap);

			HttpEntity<String> requestEnty = new HttpEntity<>(reqBodyData, headers);

			String serverUrl = props.getSbUrl() + "private/user/v1/search";

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
	public Map<String, Object> getUsersDataFromUserIds(String rootOrg, List<String> userIds, List<String> source) {

		Map<String, Object> result = new HashMap<>();

		Map<String, Object> request = new HashMap<String, Object>();
		Map<String, Object> filters = new HashMap<String, Object>();
		Map<String, Object> idKeyword = new HashMap<String, Object>();
		idKeyword.put("or", userIds);
		filters.put("id.keyword", idKeyword);
		request.put("limit", userIds.size());
		request.put("offset", 0);
		request.put("filters", filters);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			String reqBodyData = new ObjectMapper().writeValueAsString(request);
			HttpEntity<String> requestEnty = new HttpEntity<>(reqBodyData, headers);
			String serverUrl = props.getSbHubGraphServiceUrl() + "/v1/user/search/profile";
			OpenSaberApiResp openSaberApiResp = restTemplate.postForObject(serverUrl, requestEnty,
					OpenSaberApiResp.class);
			if (openSaberApiResp != null && "OK".equalsIgnoreCase(openSaberApiResp.getResponseCode())
					&& openSaberApiResp.getResult().getUserProfile().size() >= 1) {
				for (OpenSaberApiUserProfile userProfile : openSaberApiResp.getResult().getUserProfile()) {
					result.put(userProfile.getUserId(), userProfile);
				}
				return result;
			}
		} catch (Exception e) {
			throw new ApplicationLogicError("Sunbird Service ERROR: ", e);
		}

		return result;
	}
}
