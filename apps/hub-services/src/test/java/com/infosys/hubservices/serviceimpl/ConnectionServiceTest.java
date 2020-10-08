
package com.infosys.hubservices.serviceimpl;

import com.infosys.hubservices.exception.ApplicationException;
import com.infosys.hubservices.model.ConnectionRequest;
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.repository.cassandra.bodhi.UserConnectionRepository;
import com.infosys.hubservices.util.ConnectionProperties;
import com.infosys.hubservices.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
class ConnectionServiceTest {

    @InjectMocks
    ConnectionService connectionService;
    @Mock
    ConnectionProperties connectionProperties;
    @Mock
    UserConnectionRepository userConnectionRepository;
    @Mock
    NotificationService notificationService;

    final static String rootOrg = "rootOrg";
    final static String user_id = "user_id";
    final static String connect_id = "connect_id";



    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void add() {

        ConnectionRequest request = mock(ConnectionRequest.class);
        Response res =connectionService.add(rootOrg,request);
        assertTrue(!res.getResult().isEmpty());
        assertTrue(res.getResult().get(Constants.ResponseStatus.MESSAGE).equals(Constants.ResponseStatus.SUCCESSFUL));
        assertTrue(res.getResult().get(Constants.ResponseStatus.STATUS).equals(HttpStatus.CREATED));

    }

    @Test
    void update() {
        ConnectionRequest request = mock(ConnectionRequest.class);
        when(request.getUserId()).thenReturn(user_id);
        when(request.getConnectionId()).thenReturn(connect_id);

        UserConnection connection = mock(UserConnection.class);
        when(userConnectionRepository.findByUsersAndConnection(user_id,connect_id)).thenReturn(connection);
        Response res =connectionService.update(rootOrg,request);
        assertTrue(!res.getResult().isEmpty());
        assertTrue(res.getResult().get(Constants.ResponseStatus.MESSAGE).equals(Constants.ResponseStatus.SUCCESSFUL));
        assertTrue(res.getResult().get(Constants.ResponseStatus.STATUS).equals(HttpStatus.OK));
    }

    @Test
    void delete() {

        ConnectionRequest request = mock(ConnectionRequest.class);
        when(request.getUserId()).thenReturn(user_id);
        when(request.getConnectionId()).thenReturn(connect_id);

        UserConnection connection = mock(UserConnection.class);
        when(userConnectionRepository.findByUsersAndConnection(user_id,connect_id)).thenReturn(connection);
        Response res =connectionService.update(rootOrg,request);
        assertTrue(!res.getResult().isEmpty());
        assertTrue(res.getResult().get(Constants.ResponseStatus.MESSAGE).equals(Constants.ResponseStatus.SUCCESSFUL));
        assertTrue(res.getResult().get(Constants.ResponseStatus.STATUS).equals(HttpStatus.OK));
    }


    @Test
    void findSuggestedConnectionsThrowsApplicationException() {
        assertThatThrownBy(() -> connectionService.findSuggestedConnections(null,0, 1))
                .isInstanceOf(ApplicationException.class);

        assertThatThrownBy(() -> connectionService.findSuggestedConnections("",0, 1))
                .isInstanceOf(ApplicationException.class);

    }

    @Test
    void findConnections() {

        List<UserConnection> connectionList = new ArrayList<>();
        UserConnection mockConnection = mock(UserConnection.class);
        connectionList.add(mockConnection);
        when(userConnectionRepository.findByUserAndStatus(user_id, Constants.Status.APPROVED)).thenReturn(connectionList);

        Response res =connectionService.findConnections(user_id, 0, 1);
        assertTrue(!res.getResult().isEmpty());
        assertTrue(res.getResult().get(Constants.ResponseStatus.MESSAGE).equals(Constants.ResponseStatus.SUCCESSFUL));
        assertTrue(res.getResult().get(Constants.ResponseStatus.STATUS).equals(HttpStatus.OK));

    }


    @Test
    void findConnectionsRequested() {
        List<UserConnection> connectionList = new ArrayList<>();
        UserConnection mockConnection = mock(UserConnection.class);
        connectionList.add(mockConnection);
        when(userConnectionRepository.findByUserAndStatus(user_id, Constants.Status.PENDING)).thenReturn(connectionList);

        Response res =connectionService.findConnectionsRequested(user_id, 0, 1);
        assertTrue(!res.getResult().isEmpty());
        assertTrue(res.getResult().get(Constants.ResponseStatus.MESSAGE).equals(Constants.ResponseStatus.SUCCESSFUL));
        assertTrue(res.getResult().get(Constants.ResponseStatus.STATUS).equals(HttpStatus.OK));
    }
}