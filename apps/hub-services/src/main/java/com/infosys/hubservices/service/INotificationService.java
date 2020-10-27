/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.service;

import com.infosys.hubservices.model.NotificationEvent;
import com.infosys.hubservices.model.cassandra.UserConnection;
import org.springframework.http.ResponseEntity;


public interface INotificationService {


    /**
     * Build the notification request
     * @param eventId
     * @param userConnection
     */
    NotificationEvent buildEvent(String eventId, String sender, String reciepient, String status);

    /**
     * Sends notifications
     *
     * @param notificationEvent
     * @return
     */
    ResponseEntity postEvent(String rootOrg,NotificationEvent notificationEvent);


}
