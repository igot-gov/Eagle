/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.serviceimpl;

import com.infosys.hubservices.model.NotificationEvent;
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.service.INotificationService;
import com.infosys.hubservices.util.ConnectionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService implements INotificationService {

    @Autowired
    ConnectionProperties connectionProperties;

    @Override
    public NotificationEvent buildEvent(String eventId, UserConnection userConnection) {

        NotificationEvent notificationEvent = new NotificationEvent();

        if(eventId!=null && userConnection!=null){

            String fromUUID = userConnection.getUserConnectionPrimarykey().getUserId();

            Map<String, List<String>> recipients = new HashMap<>();
            List<String> toList = Arrays.asList(userConnection.getUserConnectionPrimarykey().getConnectionId());
            recipients.put(connectionProperties.getNotificationTemplateReciepient(), toList);

            Map<String, Object> tagValues = new HashMap<>();
            tagValues.put(connectionProperties.getNotificationTemplateSender(), fromUUID);
            tagValues.put(connectionProperties.getNotificationTemplateTargetUrl(), connectionProperties.getNotificationTemplateTargetUrlValue());

            notificationEvent.setEventId(eventId);
            notificationEvent.setRecipients(recipients);
            notificationEvent.setTagValues(tagValues);


        }
        return notificationEvent;

    }

    @Override
    public ResponseEntity postEvent(NotificationEvent notificationEvent) {

        ResponseEntity<?> response = null;
        try{
            final String uri = connectionProperties.getNotificationIp().concat(connectionProperties.getNotificationEventEndpoint());
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<NotificationEvent> request = new HttpEntity<>(notificationEvent);
            response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

            System.out.println("Notification event send : "+response.getStatusCode());

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Notification event send error occurred: "+e.getMessage());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return response;


    }
}
