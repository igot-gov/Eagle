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
import com.infosys.lex.progress.bodhi.repo.ContentProgressModel;
import com.infosys.lex.progress.bodhi.repo.ContentProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Integer countUserLearningHistory(String rootOrg, String userId) {
        return continueLearningMVRepository.countByRootOrgAndUserId(rootOrg, userId);
    }

    @Override
    public Integer countUserBadges(String rootOrg, String userId) {
        return userBadgeRepository.countForCompleted(rootOrg, userId);
    }

    @Override
    public Double userTimeSpentOnPlatform(String rootOrg, String userId) {
        return userTimeSpentRepository.avgTimeSpentByRootOrgAndUserId(rootOrg, userId);
    }

    @Override
    public Long userTimeSpentOnTraning(String rootOrg, String userId) {

        List<ContinueLearningMV> continueLearningMVS = continueLearningMVRepository.findByRootOrgAndUserId(rootOrg, userId);
        List<String> courseIds = continueLearningMVS.stream().map(mv -> mv.getResourceId()).collect(Collectors.toList());
        List<ContentProgressModel> progress = contentProgressRepository.findProgress(rootOrg, userId, courseIds);

        Long totalDuration = new Long(0);
        for (ContentProgressModel model : progress) {

            long diff = model.getLastAccessedOn().getTime() - model.getFirstAccessedOn().getTime();
            totalDuration = totalDuration + TimeUnit.MILLISECONDS.toHours(diff);

        }
        return totalDuration;
    }
}
