/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.serviceimpl;

import com.infosys.hubservices.exception.ApplicationServiceError;
import com.infosys.hubservices.exception.BadRequestException;
import com.infosys.hubservices.model.ConnectionRequest;
import com.infosys.hubservices.model.NotificationEvent;
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.model.cassandra.UserConnectionPrimarykey;
import com.infosys.hubservices.repository.cassandra.bodhi.UserConnectionRepository;
import com.infosys.hubservices.service.IConnectionService;
import com.infosys.hubservices.util.ConnectionProperties;
import com.infosys.hubservices.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConnectionService implements IConnectionService {

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ConnectionProperties connectionProperties;

    @Override
    public Response add(String rootOrg, ConnectionRequest request){
        Response response = new Response();
        try {

            UserConnectionPrimarykey pk = new UserConnectionPrimarykey(rootOrg, request.getUserId(), request.getConnectionId());
            UserConnection userConnection = new UserConnection(pk, Constants.Status.PENDING, "", new Date());
            userConnectionRepository.save(userConnection);

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.CREATED);

            if(connectionProperties.isNotificationEnabled())
                sendNotification(rootOrg, connectionProperties.getNotificationTemplateRequest(), userConnection);



        } catch (Exception e){
            throw new ApplicationServiceError(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }
        return response;

    }

    @Override
    public Response update(String roorOrg, ConnectionRequest request) {
        Response response = new Response();
        try {

            UserConnection userConnection = userConnectionRepository.findByUsersAndConnection(request.getUserId(), request.getConnectionId());
            userConnection.setConnectionStatus(request.getStatus());
            userConnection.setEndOn(request.getEndDate());

            userConnectionRepository.save(userConnection);

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

            if(connectionProperties.isNotificationEnabled())
                sendNotification(roorOrg, connectionProperties.getNotificationTemplateResponse(), userConnection);

        } catch (Exception e){
            throw new ApplicationServiceError(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }

        return response;
    }

    @Override
    public Response delete(String rootOrg, String userId, String connectionId) {
        Response response = new Response();
        try {

            UserConnection userConnection = userConnectionRepository.findByUsersAndConnection(userId, connectionId);
            userConnection.setConnectionStatus(Constants.Status.DELETED);
            userConnection.setEndOn(new Date());
            userConnectionRepository.save(userConnection);

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

            if(connectionProperties.isNotificationEnabled())
                sendNotification(rootOrg, connectionProperties.getNotificationTemplateResponse(), userConnection);


        } catch (Exception e){
            throw new ApplicationServiceError(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }

        return response;
    }

    @Override
    public void sendNotification(String rootOrg, String eventId, UserConnection userConnection) {
        NotificationEvent event = notificationService.buildEvent(eventId, userConnection);
        notificationService.postEvent(rootOrg, event);
    }

    @Override
    public Response findRecommendedConnection(ConnectionRequest request) {
        return null;
    }

    @Override
    public Response findCommonConnection(String userId, int offset, int limit) {

        Response response = new Response();
        try {
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException(Constants.Message.USER_ID_INVALID);
            }

            //get established connections
            List<UserConnection> userApprovedConnections = userConnectionRepository.findByUserAndStatus(userId, Constants.Status.APPROVED);

            //approved the connectionIds of established connection
            List<String> approvedConnectionIds = userApprovedConnections.stream().map(userConnection -> userConnection.getUserConnectionPrimarykey().getConnectionId()).collect(Collectors.toList());

            //get the established related connection
            List<UserConnection> relatedConnections = userConnectionRepository.findByUsersAndStatus(approvedConnectionIds, Constants.Status.APPROVED);

            //find the common new connections that could be established
            List<UserConnection> commonConnections = relatedConnections.stream().filter(userConnection -> !approvedConnectionIds.contains(userConnection.getUserConnectionPrimarykey().getConnectionId())).collect(Collectors.toList());

            if(commonConnections.isEmpty() || commonConnections.size()==0){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, commonConnections);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, commonConnections);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

        }catch (Exception e){
            throw new ApplicationServiceError(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }

        return response;
    }

    @Override
    public Response findConnections(String userId, int offset, int limit) {
        Response response = new Response();

        try{
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException(Constants.Message.USER_ID_INVALID);
            }
            List<UserConnection> userConnectionsEstablished = userConnectionRepository.findByUserAndStatus(userId, Constants.Status.APPROVED);
            if(userConnectionsEstablished.isEmpty() || userConnectionsEstablished.size()==0){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, userConnectionsEstablished);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, userConnectionsEstablished);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

        } catch (Exception e){
            throw new ApplicationServiceError(Constants.Message.FAILED_CONNECTION + e.getMessage());
        }

        return response;
    }

    @Override
    public Response findConnectionsRequested(String userId, int offset, int limit) {
        Response response = new Response();

        try{
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException(Constants.Message.USER_ID_INVALID);
            }

            List<UserConnection> userConnections = userConnectionRepository.findByUserAndStatus(userId,  Constants.Status.PENDING);
            if(userConnections.isEmpty() || userConnections.size()==0){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, userConnections);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, userConnections);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

        } catch (Exception e){
            throw new ApplicationServiceError(Constants.Message.FAILED_CONNECTION + e.getMessage());
        }

        return response;
    }


}
