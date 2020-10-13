/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.gson.JsonObject;
import com.infosys.lex.myactivities.service.MyActivities;
import com.infosys.lex.myactivities.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class MyActivitiesController {

    @Autowired
    private MyActivities myActivities;


    @GetMapping("/v1/activities/user/{userId}")
    public ResponseEntity<Map<String, Object>> getMyActivities(@RequestHeader("rootOrg") String rootOrg,
                                                               @PathVariable("userId") String userId) throws Exception {


//        Map<String, Object> response = new HashMap<>();
//        response.put(Constants.LearningHistory.CONTENT_COUNT, myActivities.countUserLearningHistory(rootOrg, userId));
//        response.put(Constants.Achieved.CERTIFICATE_COUNT, myActivities.countUserBadges(rootOrg, userId));
//        response.put(Constants.LearningHistory.TOTAL_DURATION, myActivities.userTimeSpentOnTraning(rootOrg, userId));
//        response.put(Constants.TimeSpent.DAILY_TIME_SPENT, myActivities.userTimeSpentOnPlatform(rootOrg, userId));
//        response.put(Constants.Achieved.KARMA_POINTS, myActivities.userKarmaPoints(rootOrg, userId));
//        response.put(Constants.Achieved.COINS, myActivities.userCoins(rootOrg, userId));
//


        List<Object> response = buildResponse(rootOrg, userId);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    private List<Object> buildResponse(String rootOrg, String userId){

        Map<String, Object> contentCount = new HashMap<>();
        contentCount.put(Constants.LearningHistory.CONTENT_COUNT, myActivities.countUserLearningHistory(rootOrg, userId));

        Map<String, Object> certCount = new HashMap<>();
        certCount.put(Constants.Achieved.CERTIFICATE_COUNT, myActivities.countUserBadges(rootOrg, userId));

        Map<String, Object> totDuration = new HashMap<>();
        totDuration.put(Constants.LearningHistory.TOTAL_DURATION, myActivities.userTimeSpentOnTraning(rootOrg, userId));

        Map<String, Object> dailyts = new HashMap<>();
        dailyts.put(Constants.TimeSpent.DAILY_TIME_SPENT, myActivities.userTimeSpentOnPlatform(rootOrg, userId));

        Map<String, Object> karmaPoints = new HashMap<>();
        karmaPoints.put(Constants.Achieved.KARMA_POINTS, myActivities.userKarmaPoints(rootOrg, userId));

        Map<String, Object> coins = new HashMap<>();
        coins.put(Constants.Achieved.COINS, myActivities.userCoins(rootOrg, userId));

        List<Object> response = new ArrayList<>();
        response.add(contentCount);
        response.add(certCount);
        response.add(totDuration);
        response.add(dailyts);
        //response.add(karmaPoints);
        //response.add(coins);

        return response;
    }
}
