/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */
package com.infosys.scoringengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.scoringengine.models.EvaluatorModel;
import com.infosys.scoringengine.models.Response;
import com.infosys.scoringengine.service.ScoringEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/action/scoring")
public class ScoringController {

    @Autowired
    ScoringEngineService scoringEngineService;

    @PostMapping("/add")
    public ResponseEntity<Response> add(@RequestBody EvaluatorModel evaluatorModel, @RequestHeader String rootOrg,
                                        @RequestHeader String org) throws Exception {
        evaluatorModel.setRootOrg(rootOrg);
        evaluatorModel.setOrg(org);
        System.out.println("request : "+new ObjectMapper().writeValueAsString(evaluatorModel));
        return new ResponseEntity<>(scoringEngineService.addV3(evaluatorModel), HttpStatus.OK);
    }

    @PostMapping("/fetch")
    public ResponseEntity<Response> search(@RequestBody EvaluatorModel evaluatorModel, @RequestHeader String rootOrg,
                                           @RequestHeader String org) throws Exception {
        evaluatorModel.setRootOrg(rootOrg);
        evaluatorModel.setOrg(org);
        System.out.println("request : "+new ObjectMapper().writeValueAsString(evaluatorModel));
        return new ResponseEntity<>(scoringEngineService.searchV2(evaluatorModel), HttpStatus.OK);
    }

}
