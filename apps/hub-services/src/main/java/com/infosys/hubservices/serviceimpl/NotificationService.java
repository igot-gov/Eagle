/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.serviceimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.infosys.hubservices.exception.ApplicationServiceError;
import com.infosys.hubservices.model.NotificationEvent;
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.service.INotificationService;
import com.infosys.hubservices.util.ConnectionProperties;
import com.infosys.hubservices.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService implements INotificationService {

    private Logger logger = LoggerFactory.getLogger(NotificationService.class);
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    ConnectionProperties connectionProperties;

    @Autowired
    private ProfileService profileService;

    @Override
    public NotificationEvent buildEvent(String eventId, UserConnection userConnection) {

        NotificationEvent notificationEvent = new NotificationEvent();

        if (eventId != null && userConnection != null) {

            String fromUUID = userConnection.getUserConnectionPrimarykey().getUserId();

            Map<String, List<String>> recipients = new HashMap<>();
            List<String> toList = Arrays.asList(userConnection.getUserConnectionPrimarykey().getConnectionId());
            recipients.put(connectionProperties.getNotificationTemplateReciepient(), toList);

            //values in body of notification template
            Map<String, Object> tagValues = new HashMap<>();
            tagValues.put(connectionProperties.getNotificationTemplateSender(), getUserName(fromUUID));
            tagValues.put(connectionProperties.getNotificationTemplateTargetUrl(), connectionProperties.getNotificationTemplateTargetUrlValue());

            notificationEvent.setEventId(eventId);
            notificationEvent.setRecipients(recipients);
            notificationEvent.setTagValues(tagValues);


        }
        return notificationEvent;

    }

    private String getUserName(String uuid){

        String fromName = null;
        try{
            Map<String, Object> profiles = profileService.findProfiles(Arrays.asList(uuid),null).getResult();
            if(profiles.size()>0){
                JsonNode profilePersonalDetails =((ArrayNode)profiles.get(Constants.ResponseStatus.DATA)).get(0).get(Constants.Profile.PERSONAL_DETAILS);
                fromName = profilePersonalDetails.get(Constants.Profile.FIRST_NAME).asText().concat(" ").concat(profilePersonalDetails.get(Constants.Profile.SUR_NAME).asText());

            } else {
                fromName = Constants.Profile.HUB_MEMBER;
            }
        }catch (Exception e){
            logger.error("Profile name could not be extracted :-{}",e.getMessage());
            fromName = Constants.Profile.HUB_MEMBER;

        }

        return fromName;
    }

    @Override
    public ResponseEntity postEvent(String rootOrg, NotificationEvent notificationEvent) {
        if(rootOrg == null || rootOrg.isEmpty()){
            throw new ApplicationServiceError(Constants.Message.ROOT_ORG_INVALID);
        }

        ResponseEntity<?> response = null;
        try {
            logger.info("notification event -> {}", mapper.writeValueAsString(notificationEvent));

            final String uri = connectionProperties.getNotificationIp().concat(connectionProperties.getNotificationEventEndpoint());
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set(Constants.Parmeters.ROOT_ORG, rootOrg);
            HttpEntity request = new HttpEntity<>(notificationEvent, headers);
            response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

            logger.info(Constants.Message.SENT_NOTIFICATION_SUCCESS, response.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(Constants.Message.SENT_NOTIFICATION_ERROR + e.getMessage());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return response;


    }
}
