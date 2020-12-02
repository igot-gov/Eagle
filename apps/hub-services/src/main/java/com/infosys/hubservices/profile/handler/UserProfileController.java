

/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.profile.handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/user")
public class UserProfileController {

/*    @Autowired
    RestTemplate restTemplate;

    @Value(value = "${user.registry.ip}")
    String baseUrl;*/

    @Autowired
    private ProfileUtils profileUtils;

    @Autowired
    IProfileRequestHandler profileRequestHandler;


    @PostMapping("/create/profile")
    public ResponseEntity createProfile(@RequestParam String userId, @RequestBody Map<String,Object> request) {

        RegistryRequest registryRequest = profileRequestHandler.createRequest(userId, request);
        return profileUtils.getResponseEntity(ProfileUtils.URL.CREATE.getValue(), registryRequest);
    }

    @PostMapping("/update/profile")
    public ResponseEntity updateProfile(@RequestParam String userId, @RequestBody Map<String,Object> request) {

        RegistryRequest registryRequest = profileRequestHandler.updateRequest(userId, request);
        return profileUtils.getResponseEntity(ProfileUtils.URL.UPDATE.getValue(), registryRequest);
    }

    @PostMapping("/update/workflow/profile")
    public ResponseEntity updateProfileWithWF(@RequestParam String userId, @RequestBody List<Map<String,Object>> requests) throws Exception{

        ResponseEntity response = null;
        RegistryRequest registryRequest = profileRequestHandler.updateRequestWithWF(userId, requests);
        System.out.println("request:; "+new ObjectMapper().writeValueAsString(registryRequest));
        response = profileUtils.getResponseEntity(ProfileUtils.URL.UPDATE.getValue(), registryRequest);
        return response;
    }


    @GetMapping("/search/profile")
    public ResponseEntity searchProfile(@RequestParam String userId) {

        RegistryRequest registryRequest = profileRequestHandler.searchRequest(userId);
        return profileUtils.getResponseEntity(ProfileUtils.URL.SEARCH.getValue(), registryRequest);
    }

    @GetMapping("/get/profile")
    public ResponseEntity getProfile(@RequestParam String osid) {

        RegistryRequest registryRequest = profileRequestHandler.readRequest(osid);
        return profileUtils.getResponseEntity(ProfileUtils.URL.READ.getValue(), registryRequest);

    }

}
