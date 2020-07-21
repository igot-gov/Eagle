/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */
package com.infosys.recommendationservice.controller;

import com.infosys.recommendationservice.model.Response;
import com.infosys.recommendationservice.serviceimpl.ContentSearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    @Autowired
    private ContentSearchServiceImpl searchService;

    @PostMapping("/contents")
    public ResponseEntity<Response> findContents(@RequestHeader String rootOrg, @RequestHeader String org, @RequestBody Map<String,Object> request) throws Exception{

        Response response = searchService.search(request, rootOrg, org);
        return new ResponseEntity<Response>(response, HttpStatus.OK);

    }

}
