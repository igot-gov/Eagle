/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.service;

import com.infosys.lex.badge.bodhi.repo.UserBadgeRepository;
import com.infosys.lex.continuelearning.bodhi.repo.ContinueLearningMVRepository;
import com.infosys.lex.continuelearning.entities.ContinueLearningMV;
import com.infosys.lex.myactivities.bodhi.repo.UserTimeSpentRepository;
import com.infosys.lex.myactivities.utils.Activity;
import com.infosys.lex.myactivities.utils.Constants;
import com.infosys.lex.progress.bodhi.repo.ContentProgressModel;
import com.infosys.lex.progress.bodhi.repo.ContentProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MyActivities implements IMyActivities {

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private ContinueLearningMVRepository continueLearningMVRepository;

    @Autowired
    private ContentProgressRepository contentProgressRepository;

    @Autowired
    private UserTimeSpentRepository userTimeSpentRepository;

    @Override
    public Activity countUserLearningHistory(String rootOrg, String userId) {
        Integer noOfContentsInLearning = continueLearningMVRepository.countByRootOrgAndUserId(rootOrg, userId);
        return new Activity(Constants.Unit.NUMBER, noOfContentsInLearning);
    }

    @Override
    public Activity countUserBadges(String rootOrg, String userId) {
        Integer noOfCertificates = userBadgeRepository.countForCompleted(rootOrg, userId);
        return new Activity(Constants.Unit.NUMBER, noOfCertificates);
    }

    @Override
    public Activity userTimeSpentOnPlatform(String rootOrg, String userId) {

        Double duration = userTimeSpentRepository.avgTimeSpentByRootOrgAndUserId(rootOrg, userId);
        return new Activity(Constants.Unit.MINUTE, TimeUnit.SECONDS.toHours(duration.longValue()));
    }

    @Override
    public Activity userTimeSpentOnTraning(String rootOrg, String userId) {
        List<ContinueLearningMV> continueLearningMVS = continueLearningMVRepository.findByRootOrgAndUserId(rootOrg, userId);
        List<String> contentIds = continueLearningMVS.stream().map(mv -> mv.getResourceId()).collect(Collectors.toList());
        List<ContentProgressModel> progress = contentProgressRepository.findProgress(rootOrg, userId, contentIds);

        Long totalDuration = new Long(0);

        for (ContentProgressModel model : progress) {

            long diff = model.getLastAccessedOn().getTime() - model.getFirstAccessedOn().getTime();
            totalDuration = totalDuration + TimeUnit.MILLISECONDS.toHours(diff);

        }
        return new Activity( Constants.Unit.HOUR, totalDuration);
    }

    @Override
    public Activity userKarmaPoints(String rootOrg, String userId) {
        return new Activity( Constants.Unit.NUMBER, 0);
    }

    @Override
    public Activity userCoins(String rootOrg, String userId) {
        return new Activity( Constants.Unit.NUMBER, 0);
    }
}
