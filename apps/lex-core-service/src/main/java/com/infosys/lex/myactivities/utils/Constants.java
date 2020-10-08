/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lex.myactivities.utils;

public class Constants {

    public enum Unit {NUMBER, HOUR, MINUTE}

    public static class MyActivity {
        public static final String ROOT_ORG = "rootOrg";
        public static final String USER_ID = "userId";

    }

    public static class LearningHistory {
        public static final String CONTENT_COUNT = "contentCount";
        public static final String CONTENT_IDS = "contentIds";
        public static final String TOTAL_DURATION = "totalDuration";

    }

    public static class Achieved {
        public static final String EARNED_COUNT = "earnedCount";
        public static final String CAN_EARN_COUNT = "canEarnCount";
        public static final String CERTIFICATE_COUNT = "certificateCount";
        public static final String KARMA_POINTS = "karmaPoints";
        public static final String COINS = "coins";
    }

    public static class TimeSpent {
        public static final String DAILY_TIME_SPENT = "dailyTimeSpent";

    }
}
