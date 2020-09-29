/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */
package com.infosys.hubservices.network.controller;

import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.serviceimpl.ConnectionService;
import com.infosys.hubservices.serviceimpl.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/connections/profile")
public class ConnectionProfileController {


    @Autowired
    private ProfileService profileService;

    @PostMapping("/find/common")
    public ResponseEntity<Response> findCommon(@RequestHeader String rootOrg, @RequestHeader(required = false) String org,
                                                          @RequestHeader String userId,
                                                          @RequestParam(defaultValue = "10", required = false, name = "pageSize") int pageSize,
                                                          @RequestParam(defaultValue = "0", required = false, name = "pageNo") int pageNo){

        Response response = profileService.findCommonProfile(userId, pageNo, pageSize);
        return new ResponseEntity<Response>(response, HttpStatus.OK);

    }


    @GetMapping("/fetch/requested")
    public ResponseEntity<Response> findRequests(@RequestHeader(required = true) String rootOrg, @RequestHeader(required = false) String org,
                                                        @RequestHeader String userId,
                                                        @RequestParam(defaultValue = "10", required = false, name = "pageSize") int pageSize,
                                                        @RequestParam(defaultValue = "0", required = false, name = "pageNo") int pageNo) {

        Response response = profileService.findProfileRequested(userId, pageNo, pageSize);
        return new ResponseEntity<Response>(response, HttpStatus.OK);

    }

    @GetMapping("/fetch/established")
    public ResponseEntity<Response> findEstablished(@RequestHeader(required = true) String rootOrg, @RequestHeader(required = false) String org,
                                                    @RequestHeader String userId,
                                                    @RequestParam(defaultValue = "10", required = false, name = "pageSize") int pageSize,
                                                    @RequestParam(defaultValue = "0", required = false, name = "pageNo") int pageNo) {

        Response response = profileService.findProfiles(userId, pageNo, pageSize);
        return new ResponseEntity<Response>(response, HttpStatus.OK);

    }

    @GetMapping("/fetch/all")
    public ResponseEntity<Response> findAll(@RequestHeader(required = true) String rootOrg, @RequestHeader(required = false) String org,
                                                    @RequestHeader String userId,
                                                    @RequestParam(defaultValue = "10", required = false, name = "pageSize") int pageSize,
                                                    @RequestParam(defaultValue = "0", required = false, name = "pageNo") int pageNo,
                                                    @RequestParam(required = true, name = "connectionIds") List<String> connectionIds,
                                                    @RequestParam(required = false, name = "includeSources") String[] includeSources) {

        Response response = profileService.findProfiles(connectionIds, includeSources);
        return new ResponseEntity<Response>(response, HttpStatus.OK);

    }

}
