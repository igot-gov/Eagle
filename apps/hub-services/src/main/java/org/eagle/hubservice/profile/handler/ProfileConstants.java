/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package org.eagle.hubservice.profile.handler;

public class ProfileConstants {

    public static enum API {
        CREATE("open-saber.registry.create"), READ("open-saber.registry.read"),
        SEARCH("open-saber.registry.search"), UPDATE("open-saber.registry.update");
        private String value;

        private API(String value) { this.value = value; }
        public String getValue() {
            return this.value;
        }
    }

    public static enum URL {
        CREATE("/add"), READ("/read"),
        SEARCH("/search"), UPDATE("/update");
        private String value;

        private URL(String value) { this.value = value; }
        public String getValue() {
            return this.value;
        }
    }

    private static final String UTIL_CLASS = "Utility class";

    public enum STATUS { APPROVED, REJECTED, PENDING }

    public static class Status {
        private Status() {
            throw new IllegalStateException(UTIL_CLASS);
        }
        public static final String APPROVED = "Approved";
        public static final String REJECTED = "Rejected";
        public static final String PENDING = "Pending";
        public static final String DELETED = "Deleted";

    }


    public static class Profile {
        private Profile() {
            throw new IllegalStateException(UTIL_CLASS);
        }
        public static final String FIRST_NAME = "firstname";
        public static final String SUR_NAME = "surname";
        public static final String PERSONAL_DETAILS = "personalDetails";
        public static final String HUB_MEMBER = "Hub member";

    }


}
