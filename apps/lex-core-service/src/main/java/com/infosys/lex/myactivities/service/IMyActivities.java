/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.service;

import com.infosys.lex.myactivities.utils.Activity;

import java.util.Map;

public interface IMyActivities {

    public Activity countUserLearningHistory(String rootOrg, String userId);

    public Activity countUserBadges(String rootOrg, String userId);

    public Activity userTimeSpentOnPlatform(String rootOrg, String userId);

    public Activity userTimeSpentOnTraning(String rootOrg, String userId);

    public Activity userKarmaPoints(String rootOrg, String userId);

    public Activity userCoins(String rootOrg, String userId);


}
