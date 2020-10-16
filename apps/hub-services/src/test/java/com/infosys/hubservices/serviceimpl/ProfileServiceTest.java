

package com.infosys.hubservices.serviceimpl;

import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.util.ConnectionProperties;

import com.infosys.hubservices.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ProfileServiceTest {

/*
    @InjectMocks
    ProfileService profileService;
    @Mock
    ConnectionService connectionService;
    @Mock
    ConnectionProperties connectionProperties;

    final static String MOCK_UUID =  "user_id";

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void findCommonProfile() {

        List<UserConnection> mockConList = new ArrayList<>();

        Response mockresponse = new Response();
        mockresponse.put(Constants.ResponseStatus.DATA, mockConList);
        when(connectionService.findSuggestedConnections(MOCK_UUID, 0, 1)).thenReturn(mockresponse);

        Response res = profileService.findCommonProfile(MOCK_UUID, 0, 1);
        assertTrue(((List)res.getResult().get(Constants.ResponseStatus.DATA)).size()==0);
    }

    @Test
    void findProfileRequested() {

        List<UserConnection> mockConList = new ArrayList<>();

        Response mockresponse = new Response();
        mockresponse.put(Constants.ResponseStatus.DATA, mockConList);
        when(connectionService.findConnectionsRequested(MOCK_UUID, 0, 1)).thenReturn(mockresponse);

        Response res = profileService.findProfileRequested(MOCK_UUID, 0, 1);
        assertTrue(((List)res.getResult().get(Constants.ResponseStatus.DATA)).size()==0);
    }
*/

}