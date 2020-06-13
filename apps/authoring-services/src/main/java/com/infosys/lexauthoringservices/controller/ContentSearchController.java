/*
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lexauthoringservices.controller;

import com.infosys.lexauthoringservices.model.ContentSearchRequest;
import com.infosys.lexauthoringservices.serviceimpl.ContentSearchServiceImpl;
import com.infosys.lexauthoringservices.util.LexLogger;
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
@RequestMapping("/graph/filter")
public class ContentSearchController {

    private LexLogger logger = new LexLogger(ContentSearchController.class.getName());

    @Autowired
    private ContentSearchServiceImpl searchService;

    @PostMapping()
    public ResponseEntity<Map<String,Object>> findContents(@RequestBody ContentSearchRequest request){

        try {
            Map<String, Object> response = searchService.search(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            logger.error(e);
        }

        return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
