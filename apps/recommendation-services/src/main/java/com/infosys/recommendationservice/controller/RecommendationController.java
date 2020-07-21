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
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private ContentSearchServiceImpl searchService;

    @PostMapping("/contents")
    public ResponseEntity<Response> findContents(@RequestHeader String rootOrg, @RequestHeader String org,
                                                 @RequestParam(defaultValue = "5", required = false, name = "pageSize") int pageSize,
                                                 @RequestParam(defaultValue = "0", required = false, name = "pageNo") int pageNo,
                                                 @RequestBody Map<String,Object> request){

        Response response = searchService.search(request, rootOrg, org, pageSize, pageNo);
        return new ResponseEntity<Response>(response, HttpStatus.OK);

    }

}
