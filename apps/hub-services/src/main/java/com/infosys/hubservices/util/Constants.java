/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.util;

public class Constants {

    public enum STATUS { APPROVED, REJECTED, PENDING }

    public static class Status {
        public static final String APPROVED = "Approved";
        public static final String REJECTED = "Rejected";
        public static final String PENDING = "Pending";
        public static final String DELETED = "Deleted";

    }

    public static class Message {
        public static final String FAILED_CONNECTION = "Failed user connections:-";
        public static final String USER_ID_INVALID = "user_id cant be null or empty";
        public static final String ROOT_ORG_INVALID = "rootOrg cant be null or empty";
        public static final String SENT_NOTIFICATION_ERROR ="Notification event send error occurred: ";
        public static final String SENT_NOTIFICATION_SUCCESS ="Notification event send : ";
    }

    public static class ResponseStatus {
        public static final String SUCCESSFUL = "Successful";
        public static final String FAILED = "Failed";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String STATUS = "status";
    }

    public static class Parmeters {
        public static final String ROOT_ORG = "rootOrg";

    }


}
