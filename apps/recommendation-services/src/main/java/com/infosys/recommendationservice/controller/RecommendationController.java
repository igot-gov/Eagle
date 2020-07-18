/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */
package com.infosys.recommendationservice.controller;

import com.infosys.recommendationservice.serviceimpl.ContentSearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/recommendation/contents")
public class RecommendationController {

    //private LexLogger logger = new LexLogger(recommendationservice.class.getName());

    @Autowired
    private ContentSearchServiceImpl searchService;

    @PostMapping()
    public ResponseEntity<Map<String,Object>> findContents(@RequestBody Map<String,Object> request){

        try {
            //Map<String, Object> response = searchService.search(request);
            return null;//new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            //logger.error(e);
        }

        return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
