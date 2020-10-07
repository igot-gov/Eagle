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
import com.infosys.hubservices.util.ConnectionProperties;
import com.infosys.hubservices.util.Constants;

import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;
    @Mock
    ProfileService profileService;

    @Mock
    ConnectionProperties connectionProperties;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void buildEvent_all_values_set() throws Exception{

        Map<String, Object> mockProfiles = new HashMap<>();
        Response response = new Response();
        response.put(Constants.ResponseStatus.DATA, mockProfiles);

        when(profileService.findProfiles(Arrays.asList("abc"),null)).thenReturn(response);

        when(connectionProperties.getNotificationTemplateSender()).thenReturn("#sender");
        when(connectionProperties.getNotificationTemplateTargetUrl()).thenReturn("#targetUrl");
        when(connectionProperties.getNotificationTemplateTargetUrlValue()).thenReturn("#urlValues");
        when(connectionProperties.getNotificationTemplateStatus()).thenReturn("#status");
        when(connectionProperties.getNotificationTemplateReciepient()).thenReturn("#reciepient");

        UserConnection userConnection = mock(UserConnection.class, Mockito.RETURNS_DEEP_STUBS);
        when(userConnection.getUserConnectionPrimarykey().getUserId()).thenReturn("uuid");
        when(userConnection.getUserConnectionPrimarykey().getConnectionId()).thenReturn("connect_id");
        when(userConnection.getConnectionStatus()).thenReturn("status");

        NotificationEvent notificationEvent = notificationService.buildEvent("abc", userConnection);

        assertTrue(notificationEvent.getEventId().equalsIgnoreCase("abc"));
        assertTrue(!notificationEvent.getRecipients().isEmpty());
        assertTrue(!notificationEvent.getTagValues().isEmpty());
        assertTrue(notificationEvent.getTagValues().get("#sender")!=null);
        assertTrue(notificationEvent.getTagValues().get("#status")!=null);
        assertTrue(notificationEvent.getTagValues().get("#targetUrl")!=null);

    }

    @Test
    void postEvent()  {
        when(connectionProperties.getNotificationIp()).thenReturn("ipaddress");
        when(connectionProperties.getNotificationEventEndpoint()).thenReturn("endpoint");

        NotificationEvent notificationEvent = mock(NotificationEvent.class, Mockito.RETURNS_DEEP_STUBS);

        ResponseEntity entity  = notificationService.postEvent(any(String.class), notificationEvent);
        assertTrue(entity!=null);
        assertTrue(entity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);

    }
}