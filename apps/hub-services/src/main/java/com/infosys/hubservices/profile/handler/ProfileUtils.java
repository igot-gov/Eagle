/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.profile.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProfileUtils {

    static final RestTemplate restTemplate = new RestTemplate();

    @Value(value = "${user.registry.ip}")
    String baseUrl;

    public static enum API {
        CREATE("open-saber.registry.create"), READ("open-saber.registry.read"),
        SEARCH("open-saber.registry.search"), UPDATE("open-saber.registry.update");
        private String value;

        private API(String value) { this.value = value; }
        public String getValue() {
            return this.value;
        }
    }

    public static enum URL {
        CREATE("/add"), READ("/read"),
        SEARCH("/search"), UPDATE("/update");
        private String value;

        private URL(String value) { this.value = value; }
        public String getValue() {
            return this.value;
        }
    }

    private static final String UTIL_CLASS = "Utility class";

    public enum STATUS { APPROVED, REJECTED, PENDING }

    public static class Status {
        private Status() {
            throw new IllegalStateException(UTIL_CLASS);
        }
        public static final String APPROVED = "Approved";
        public static final String REJECTED = "Rejected";
        public static final String PENDING = "Pending";
        public static final String DELETED = "Deleted";

    }


    public static class Profile {
        private Profile() {
            throw new IllegalStateException(UTIL_CLASS);
        }
        public static final String USER_PROFILE = "UserProfile";
        public static final String ID = "id";
        public static final String AT_ID = "@id";
        public static final String USER_ID = "userId";
        public static final String OSID = "osid";
        public static final String FILTERs = "filters";
        public static final String REQUEST = "request";
        public static final String ENTITY_TYPE = "entityType";

    }

    public static void merge(Map<String,Object> mapLeft, Map<String,Object> mapRight) {
        // go over all the keys of the right map
        for (String key : mapRight.keySet()) {

            Object ml = mapLeft.get(key);
            Object mr = mapRight.get(key);
            // if the left map already has this key, merge the maps that are behind that key
            if (mapLeft.containsKey(key) && ml instanceof HashMap) {
                merge((Map<String, Object>) ml, (Map<String, Object>) mr);

            } else if(mapLeft.containsKey(key) && !(mapLeft.get(key) instanceof HashMap)){
                mapLeft.put(key, mapRight.get(key));
            }else {
                // otherwise just add the map under that key
                mapLeft.put(key, mapRight.get(key));
            }
        }
    }


    public static void mergeLeaf(Map<String,Object> mapLeft, Map<String,Object> mapRight, String leafKey, String id) {
        // go over all the keys of the right map

        for (String key : mapLeft.keySet()) {

            if(key.equalsIgnoreCase(leafKey) && (mapLeft.get(key) instanceof ArrayList)){

                ((ArrayList)mapLeft.get(key)).removeIf(o -> ((Map)o).get("osid").toString().equalsIgnoreCase(id));
                ((ArrayList)mapLeft.get(key)).add(mapRight);

            }
            if(key.equalsIgnoreCase(leafKey) && (mapLeft.get(key) instanceof HashMap)){
                mapLeft.put(key, mapRight);

            }

        }
    }

    public ResponseEntity getResponseEntity(String endPoint, RegistryRequest registryRequest){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<RegistryRequest> requestEntity = new HttpEntity<>(registryRequest, requestHeaders);

        ResponseEntity responseEntity = restTemplate.exchange(
                baseUrl + endPoint,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
    }

    public ResponseEntity getResponseEntity( String baseUrl, String endPoint, Map request, HttpMethod method){
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Map> requestEntity = new HttpEntity<>(request, requestHeaders);

        ResponseEntity responseEntity = restTemplate.exchange(
                baseUrl + endPoint,
                method,
                requestEntity,
                Map.class
        );

        return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
    }


}
