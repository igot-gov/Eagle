/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.service;

import com.infosys.lex.badge.bodhi.repo.UserBadgeRepository;
import com.infosys.lex.badge.bodhi.repo.UserBadgesModel;
import com.infosys.lex.badge.postgredb.projection.BadgeDetailsProjection;
import com.infosys.lex.badge.postgredb.repository.BadgeRepo;
import com.infosys.lex.continuelearning.bodhi.repo.ContinueLearningMVRepository;
import com.infosys.lex.continuelearning.entities.ContinueLearningMV;
import com.infosys.lex.myactivities.bodhi.repo.UserTimeSpentRepository;
import com.infosys.lex.myactivities.utils.Activity;
import com.infosys.lex.myactivities.utils.Constants;
import com.infosys.lex.progress.bodhi.repo.ContentProgressModel;
import com.infosys.lex.progress.bodhi.repo.ContentProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private BadgeRepo badgeRepo;

    @Override
    public Activity countUserLearningHistory(String rootOrg, String userId) {
        Integer noOfContentsInLearning = continueLearningMVRepository.countByRootOrgAndUserId(rootOrg, userId);
        return new Activity(Constants.Unit.NUMBER, noOfContentsInLearning);
    }

    @Override
    public Activity countUserBadges(String rootOrg, String userId) {


        List<BadgeDetailsProjection> badgeData = badgeRepo.fetchBadgeDetailsByRootOrgAndLanguage(rootOrg,
                Arrays.asList("en"));

        List<UserBadgesModel> bm = userBadgeRepository.findByRootOrgAndUserIdAndReceivedCountAndProgress(rootOrg, userId);
        List<String> badgeIds = bm.stream().map(m->m.getPrimaryKey().getBadgeId()).collect(Collectors.toList());
        //System.out.println("badgeIds -> "+badgeIds);
        List<BadgeDetailsProjection> filterBadgeData = badgeData.stream().filter(bd->badgeIds.contains(bd.getbadgeId())).collect(Collectors.toList());
        //System.out.println("filterBadgeIds -> "+filterBadgeData.stream().map(f -> f.getbadgeId()).collect(Collectors.toList()));

        Integer noOfCertificates = filterBadgeData.size();//userBadgeRepository.countForCompleted(rootOrg, userId);
        return new Activity(Constants.Unit.NUMBER, noOfCertificates);
    }

    @Override
    public Activity userTimeSpentOnPlatform(String rootOrg, String userId) {

        Double duration = userTimeSpentRepository.avgTimeSpentByRootOrgAndUserId(rootOrg, userId);
        //System.out.println("Daily duration :: "+duration);
        return new Activity(Constants.Unit.MINUTE, TimeUnit.SECONDS.toMinutes(duration.longValue()));
    }

    @Override
    public Activity userTimeSpentOnTraning(String rootOrg, String userId) {
//        List<ContinueLearningMV> continueLearningMVS = continueLearningMVRepository.findByRootOrgAndUserId(rootOrg, userId);
//        List<String> contentIds = continueLearningMVS.stream().map(mv -> mv.getContextPathId()).collect(Collectors.toList());

        List<ContentProgressModel> progress = contentProgressRepository.findTime(rootOrg, userId);
        System.out.println("userTimeSpentOnTraning progress size:: "+progress.size());

        Long totalDuration = new Long(0);

        for (ContentProgressModel model : progress) {
            /*System.out.println("userTimeSpentOnTraning last :: "+model.getLastAccessedOn().getTime()+ "first::"+model.getFirstAccessedOn().getTime());

            long diff = model.getLastAccessedOn().getTime() - model.getFirstAccessedOn().getTime();
            System.out.println("userTimeSpentOnTraning diff :: "+diff);*/
            //System.out.println("user TimeSpent for content: "+ model.getPrimaryKey().getContentId() + " :: "+model.getTimespent());

            totalDuration = totalDuration + TimeUnit.SECONDS.toHours(model.getTimespent().longValue());

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
