/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.service;

import com.infosys.lex.badge.bodhi.repo.UserBadgeRepository;
import com.infosys.lex.continuelearning.bodhi.repo.ContinueLearningMVRepository;
import com.infosys.lex.myactivities.repo.UserTimeSpentRepository;
import com.infosys.lex.progress.bodhi.repo.ContentProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class MyActivities implements IMyActivities{

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private ContinueLearningMVRepository continueLearningMVRepository;

    @Autowired
    private ContentProgressRepository contentProgressRepository;

    @Autowired
    private UserTimeSpentRepository userTimeSpentRepository;


    @Override
    public Map<String, Object> countUserLearningHistory(String rootOrg, String userId) {
        return null;
    }

    @Override
    public Map<String, Object> countUserBadges(String rootOrg, String userId) {
        return null;
    }

    @Override
    public Map<String, Object> userTimeSpentOnPlatform(String rootOrg, String userId) {
        return null;
    }

    @Override
    public Map<String, Object> userTimeSpentOnTraning(String rootOrg, String userId) {
        return null;
    }
}
