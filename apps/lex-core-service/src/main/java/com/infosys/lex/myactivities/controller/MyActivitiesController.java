/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.controller;

import com.infosys.lex.myactivities.service.MyActivities;
import com.infosys.lex.myactivities.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class MyActivitiesController {

    @Autowired
    private MyActivities myActivities;


    @GetMapping("/v1/activities/user/{userId}")
    public ResponseEntity<Map<String, Object>> getMyActivities(@RequestHeader("rootOrg") String rootOrg,
                                                               @PathVariable("userId") String userId) throws Exception {


        Map<String, Object> response = new HashMap<>();
        response.put(Constants.LearningHistory.CONTENT_COUNT, myActivities.countUserLearningHistory(rootOrg, userId));
        response.put(Constants.Badges.CERTIFICATE_COUNT, myActivities.countUserBadges(rootOrg, userId));
        response.put(Constants.LearningHistory.TOTAL_DURATION, myActivities.userTimeSpentOnTraning(rootOrg, userId));
        response.put(Constants.TimeSpent.DAILY_TIME_SPENT, myActivities.userTimeSpentOnPlatform(rootOrg, userId));

        return new ResponseEntity(response, HttpStatus.OK);
    }
}
