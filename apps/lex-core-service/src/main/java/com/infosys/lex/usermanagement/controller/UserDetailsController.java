/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
 * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version: 1.10
 * <p>
 * Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
 * this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
 * the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
 * by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
 * this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
 * under the law.
 * <p>
 * Highly Confidential
 */

package com.infosys.lex.usermanagement.controller;

import com.infosys.lex.core.exception.NoContentException;
import com.infosys.lex.usermanagement.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@RestController
public class UserDetailsController {

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable("userId") String userId, @RequestParam(value = "requiredFields", required = false) ArrayList<String> requiredFields) {
        try {
            Map<String, Object> serviceData = userDetailsService.getUserDetails(userId, requiredFields);
            return new ResponseEntity<>(serviceData, HttpStatus.OK);
        }catch (NoContentException e){
            return new ResponseEntity<>(Collections.singletonMap("message",e.getMessage()), HttpStatus.NO_CONTENT);
        }
    }
}
