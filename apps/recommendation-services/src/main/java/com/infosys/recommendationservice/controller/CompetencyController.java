/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */
package com.infosys.recommendationservice.controller;

import com.infosys.recommendationservice.model.Response;
import com.infosys.recommendationservice.model.UserCompetencyRequest;
import com.infosys.recommendationservice.service.UserCompentancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/competency")
public class CompetencyController {

    @Autowired
    private UserCompentancyService userCompentancyService;

    @PostMapping("/add")
    public ResponseEntity<Response> add(@RequestHeader String rootOrg, @RequestHeader String org, @RequestBody UserCompetencyRequest request) throws Exception{

        Response response = userCompentancyService.upsert(request, rootOrg, org);
        return new ResponseEntity<Response>(response, HttpStatus.OK);

    }

}
