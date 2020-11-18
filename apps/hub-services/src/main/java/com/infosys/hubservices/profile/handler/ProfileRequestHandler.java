/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.profile.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProfileRequestHandler implements IProfileRequestHandler {

    @Autowired
    private ProfileUtils profileUtils;
    @Autowired
    private ObjectMapper objectMapper;

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

        //search with user id
        ResponseEntity responseEntity = profileUtils.getResponseEntity(ProfileUtils.URL.SEARCH.getValue(), searchRequest(uuid));

        Object searchResult = ((Map<String,Object>)((Map<String,Object>)responseEntity.getBody()).get("result")).get(ProfileUtils.Profile.USER_PROFILE);

        Map<String,Object> search = ((Map<String,Object>)((List)searchResult).get(0));
        //merge request and search to add osid(s)
        profileUtils.merge(search, request);

        RegistryRequest registryRequest = new RegistryRequest();
        registryRequest.setId(ProfileUtils.API.UPDATE.getValue());
        registryRequest.getRequest().put(ProfileUtils.Profile.USER_PROFILE, search);

        return registryRequest;
    }

    @Override
    public RegistryRequest searchRequest(String uuid) {
        List types = Arrays.asList(ProfileUtils.Profile.USER_PROFILE);
        Map<String, Map<String, Object>> filters = new HashMap<>();
        filters.put(ProfileUtils.Profile.ID, Stream.of(new AbstractMap.SimpleEntry<>("eq", uuid)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));


        RegistryRequest registryRequest = new RegistryRequest();
        registryRequest.setId(ProfileUtils.API.SEARCH.getValue());
        registryRequest.getRequest().put(ProfileUtils.Profile.ENTITY_TYPE, types);
        registryRequest.getRequest().put(ProfileUtils.Profile.FILTERs, filters);
        return registryRequest;
    }

    @Override
    public RegistryRequest readRequest(String id) {

        Map<String, Object> params = Stream.of(new AbstractMap.SimpleEntry<>(ProfileUtils.Profile.OSID, id)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        RegistryRequest registryRequest = new RegistryRequest();
        registryRequest.setId(ProfileUtils.API.READ.getValue());
        registryRequest.getRequest().put(ProfileUtils.Profile.USER_PROFILE, params);
        return registryRequest;
    }
}
