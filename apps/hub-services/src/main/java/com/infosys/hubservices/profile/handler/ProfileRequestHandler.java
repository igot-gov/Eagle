/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.profile.handler;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.hubservices.model.EagleUserProfileRequest;

@Service
public class ProfileRequestHandler implements IProfileRequestHandler {

	private Logger logger = LoggerFactory.getLogger(ProfileRequestHandler.class);

	@Autowired
	private ProfileUtils profileUtils;
	@Autowired
	private ObjectMapper objectMapper;

	@Value(value = "${eagle.user.service.ip}")
	String eagleBaseUrl;
	@Value(value = "${eagle.user.service.update.endpoint}")
	String eagleUpdateEndPoint;

	@Override
	public RegistryRequest createRequest(String uuid, Map<String, Object> request) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

		request.put(ProfileUtils.Profile.USER_ID, uuid);
		request.put(ProfileUtils.Profile.ID, uuid);
		request.put(ProfileUtils.Profile.AT_ID, uuid);

		RegistryRequest registryRequest = new RegistryRequest();
		registryRequest.setId(ProfileUtils.API.CREATE.getValue());
		registryRequest.getRequest().put(ProfileUtils.Profile.USER_PROFILE, request);
		return registryRequest;
	}

	@Override
	public RegistryRequest updateRequest(String uuid, Map<String, Object> request) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

		// search with user id
		ResponseEntity responseEntity = profileUtils.getResponseEntity(ProfileUtils.URL.SEARCH.getValue(),
				searchRequest(uuid));

		Object searchResult = ((Map<String, Object>) ((Map<String, Object>) responseEntity.getBody()).get("result"))
				.get(ProfileUtils.Profile.USER_PROFILE);

		Map<String, Object> search = ((Map<String, Object>) ((List) searchResult).get(0));
		// merge request and search to add osid(s)
		profileUtils.merge(search, request);

		RegistryRequest registryRequest = new RegistryRequest();
		registryRequest.setId(ProfileUtils.API.UPDATE.getValue());
		registryRequest.getRequest().put(ProfileUtils.Profile.USER_PROFILE, search);

		return registryRequest;
	}

	@Override
	public RegistryRequest updateRequestWithWF(String uuid, List<Map<String, Object>> requests) throws Exception {
//        HttpHeaders requestHeaders = new HttpHeaders();
//        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
//
//
//        List types = Arrays.asList(ProfileUtils.Profile.USER_PROFILE);
//        Map<String, Map<String, Map<String, Object>>> filters = new HashMap<>();
//
//        Map<String, Object> filterItem = new HashMap<>();
//        filterItem.put("eq",  request.get("osid"));
//        filters.put((String)request.get("fieldKey"), Stream.of(new AbstractMap.SimpleEntry<>("osid", filterItem))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
//
//
//        RegistryRequest registryRequest = new RegistryRequest();
//        registryRequest.setId(ProfileUtils.API.SEARCH.getValue());
//        registryRequest.getRequest().put(ProfileUtils.Profile.ENTITY_TYPE, types);
//        registryRequest.getRequest().put(ProfileUtils.Profile.FILTERs, filters);

		// search with user id
		ResponseEntity responseEntity = profileUtils.getResponseEntity(ProfileUtils.URL.SEARCH.getValue(),
				searchRequest(uuid));

		Object searchResult = ((Map<String, Object>) ((Map<String, Object>) responseEntity.getBody()).get("result"))
				.get(ProfileUtils.Profile.USER_PROFILE);

		List<Object> searchResultList = (List) searchResult;
		if (searchResultList.size() == 0) {
			throw new Exception(
					"Failed to process WF update request. User Registry not available for USER_ID: " + uuid);
		}
		Map<String, Object> existingUserProfile = ((Map<String, Object>) ((List) searchResult).get(0));
		// merge request and search to add osid(s)
		for (Map<String, Object> request : requests) {
			String osid = StringUtils.isEmpty(request.get("osid")) == true ? "" : request.get("osid").toString();
			Map<String, Object> toChange = new HashMap<>();

			Object profileObject = existingUserProfile.get(request.get("fieldKey"));
			if (profileObject instanceof ArrayList) {
				List<Map<String, Object>> searchFields = (ArrayList) profileObject;
				if ("professionalDetails".equalsIgnoreCase((String) request.get("fieldKey"))) {
					// We may not have osid value when the update request called from Lex-Core (i.e.
					// SPV / MDO Portal)
					if (osid.equalsIgnoreCase("")) {
						// we are going to get the existing object details in this case
						Map<String, Object> objectToUpdate = searchFields.get(0);
						logger.info("objectToUpdate:; " + objectToUpdate);
						osid = (String) objectToUpdate.get("osid");
						toChange.putAll(objectToUpdate);
						logger.info("OSID is empty... using Object's OSID: " + osid);
						String deptNameValue = (String) ((Map<String, Object>) request.get("toValue")).get("name");
						if (deptNameValue != null) {
							EagleUserProfileRequest profileRequest = new EagleUserProfileRequest();
							profileRequest.setDepartmentName(deptNameValue);
							profileRequest.setUserId(uuid);
//                          profileUtils.getResponseEntity(eagleBaseUrl, eagleUpdateEndPoint,eagleObjectToUpdate, HttpMethod.POST );
							StringBuilder strBuilder = new StringBuilder(eagleBaseUrl);
							strBuilder.append(eagleUpdateEndPoint);
							profileUtils.fetchResult(strBuilder, profileRequest, EagleUserProfileRequest.class);
						}
					} else {
						for (Map<String, Object> obj : searchFields) {
							if (obj.get("osid").toString().equalsIgnoreCase(osid))
								toChange.putAll(obj);
							if (obj.get("name") != null) {
								Map<String, Object> eagleObjectToUpdate = new HashMap<>();
								eagleObjectToUpdate.put("userId", uuid);
								eagleObjectToUpdate.put("departmentName", obj.get("name"));
								// profileUtils.getResponseEntity(eagleBaseUrl,
								// eagleUpdateEndPoint,eagleObjectToUpdate, HttpMethod.PATCH );
							}
						}

					}
				} else {
					for (Map<String, Object> obj : searchFields) {
						if (obj.get("osid").toString().equalsIgnoreCase(osid))
							toChange.putAll(obj);
					}
				}
			}
			if (profileObject instanceof HashMap) {
				Map<String, Object> searchFields = (Map<String, Object>) existingUserProfile
						.get((String) request.get("fieldKey"));
				toChange.putAll(searchFields);
			}

			Map<String, Object> objectMap = (Map<String, Object>) request.get("toValue");
			for (Map.Entry entry : objectMap.entrySet())
				toChange.put((String) entry.getKey(), entry.getValue());

			profileUtils.mergeLeaf(existingUserProfile, toChange, request.get("fieldKey").toString(), osid);
		}

		RegistryRequest registryRequest = new RegistryRequest();
		registryRequest.setId(ProfileUtils.API.UPDATE.getValue());
		registryRequest.getRequest().put(ProfileUtils.Profile.USER_PROFILE, existingUserProfile);

		return registryRequest;
	}

	@Override
	public RegistryRequest searchRequest(String uuid) {
		List types = Arrays.asList(ProfileUtils.Profile.USER_PROFILE);
		Map<String, Map<String, Object>> filters = new HashMap<>();
		filters.put(ProfileUtils.Profile.ID, Stream.of(new AbstractMap.SimpleEntry<>("eq", uuid))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

		RegistryRequest registryRequest = new RegistryRequest();
		registryRequest.setId(ProfileUtils.API.SEARCH.getValue());
		registryRequest.getRequest().put(ProfileUtils.Profile.ENTITY_TYPE, types);
		registryRequest.getRequest().put(ProfileUtils.Profile.FILTERs, filters);
		return registryRequest;
	}

	@Override
	public RegistryRequest searchRequest(Map params) {
		List types = Arrays.asList(ProfileUtils.Profile.USER_PROFILE);
		logger.info("search params -> {}", params);

		RegistryRequest registryRequest = new RegistryRequest();
		registryRequest.setId(ProfileUtils.API.SEARCH.getValue());
		registryRequest.getRequest().put(ProfileUtils.Profile.ENTITY_TYPE, types);
		if (null != params.get("offset") && null != params.get("limit")) {
			registryRequest.getRequest().put("offset", params.get("offset"));
			registryRequest.getRequest().put("limit", params.get("limit"));
		}

		Map<String, Map<String, Object>> filters = (Map<String, Map<String, Object>>) params
				.get(ProfileUtils.Profile.FILTERs);
		registryRequest.getRequest().put(ProfileUtils.Profile.FILTERs, filters);
		return registryRequest;
	}

	@Override
	public RegistryRequest readRequest(String id) {

		Map<String, Object> params = Stream.of(new AbstractMap.SimpleEntry<>(ProfileUtils.Profile.OSID, id))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		RegistryRequest registryRequest = new RegistryRequest();
		registryRequest.setId(ProfileUtils.API.READ.getValue());
		registryRequest.getRequest().put(ProfileUtils.Profile.USER_PROFILE, params);
		return registryRequest;
	}
}
