/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.service;

import java.util.Map;

public interface IMyActivities {

    public Integer countUserLearningHistory(String rootOrg, String userId);

    public Integer countUserBadges(String rootOrg, String userId);

    public Double userTimeSpentOnPlatform(String rootOrg, String userId);

    public Long userTimeSpentOnTraning(String rootOrg, String userId);

}
