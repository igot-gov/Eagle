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
    private ProfileService profileService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ConnectionProperties connectionProperties;

    @Autowired
    public Response add(String roorOrg, ConnectionRequest request){
        Response response = new Response();
        try {

            UserConnectionPrimarykey pk = new UserConnectionPrimarykey(roorOrg, request.getUserId(), request.getConnectionId());
            UserConnection userConnection = new UserConnection(pk, "pending", "", new Date());
            userConnectionRepository.save(userConnection);

            response.put(response.MESSAGE, response.SUCCESSFUL);
            response.put(response.STATUS, HttpStatus.CREATED);

            sendNotification(connectionProperties.getNotificationTemplateRequest(), userConnection);



        } catch (Exception e){
            throw new ApplicationServiceError("Failed to find connections: "+e.getMessage());

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

            response.put(response.MESSAGE, response.SUCCESSFUL);
            response.put(response.STATUS, HttpStatus.CREATED);

            sendNotification(connectionProperties.getNotificationTemplateResponse(), userConnection);

        } catch (Exception e){
            throw new ApplicationServiceError("Failed to find connections: "+e.getMessage());

        }

        return response;
    }

    @Override
    public Response delete(String userId, String connectionId) {
        Response response = new Response();
        try {

            UserConnection userConnection = userConnectionRepository.findByUsersAndConnection(userId, connectionId);
            userConnection.setConnectionStatus("Rejected");
            userConnectionRepository.save(userConnection);

            response.put(response.MESSAGE, response.SUCCESSFUL);
            response.put(response.STATUS, HttpStatus.OK);

            sendNotification(connectionProperties.getNotificationTemplateResponse(), userConnection);


        } catch (Exception e){
            throw new ApplicationServiceError("Failed to find connections: "+e.getMessage());

        }

        return response;
    }

    @Override
    public void sendNotification(String eventId, UserConnection userConnection) {
        NotificationEvent event = notificationService.buildEvent(eventId, userConnection);
        notificationService.postEvent(event);
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
                throw new BadRequestException("user_id cant be null or empty");
            }

            //get established connections
            List<UserConnection> userApprovedConnections = userConnectionRepository.findByUserAndStatus(userId,  "Approved");

            //approved the connectionIds of established connection
            List<String> approvedConnectionIds = userApprovedConnections.stream().map(userConnection -> userConnection.getUserConnectionPrimarykey().getConnectionId()).collect(Collectors.toList());

            //get the established related connection
            List<UserConnection> relatedConnections = userConnectionRepository.findByUsersAndStatus(approvedConnectionIds, "Approved");

            //find the common new connections that could be established
            List<UserConnection> commonConnections = relatedConnections.stream().filter(userConnection -> !approvedConnectionIds.contains(userConnection.getUserConnectionPrimarykey().getConnectionId())).collect(Collectors.toList());

            if(commonConnections.size()==0){
                response.put(response.MESSAGE, response.FAILED);
                response.put(response.DATA, commonConnections);
                response.put(response.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(response.MESSAGE, response.SUCCESSFUL);
            response.put(response.DATA, commonConnections);
            response.put(response.STATUS, HttpStatus.OK);

        }catch (Exception e){
            throw new ApplicationServiceError("Failed to find connections: "+e.getMessage());

        }

        return response;
    }

    @Override
    public Response findConnections(String userId, int offset, int limit) {
        Response response = new Response();

        try{
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException("user_id cant be null or empty");
            }
            List<UserConnection> userConnectionsEstablished = userConnectionRepository.findByUserAndStatus(userId, "Approved");
            if(userConnectionsEstablished.size()==0){
                response.put(response.MESSAGE, response.FAILED);
                response.put(response.DATA, userConnectionsEstablished);
                response.put(response.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(response.MESSAGE, response.SUCCESSFUL);
            response.put(response.DATA, userConnectionsEstablished);
            response.put(response.STATUS, HttpStatus.OK);

        } catch (Exception e){
            throw new ApplicationServiceError("Failed to find connections: "+e.getMessage());
        }

        return response;
    }

    @Override
    public Response findConnectionsRequested(String userId, int offset, int limit) {
        Response response = new Response();

        try{
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException("user_id cant be null or empty");
            }
            //List<UserConnection> userConnections = userConnectionRepository.findByUserAndTypeAndStatus(userId, "requests", "Pending");

            List<UserConnection> userConnections = userConnectionRepository.findByConnectionAndStatus(userId,  "Pending");
            if(userConnections.size()==0){
                response.put(response.MESSAGE, response.FAILED);
                response.put(response.DATA, userConnections);
                response.put(response.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(response.MESSAGE, response.SUCCESSFUL);
            response.put(response.DATA, userConnections);
            response.put(response.STATUS, HttpStatus.OK);

        } catch (Exception e){
            throw new ApplicationServiceError("Failed to find connections: "+e.getMessage());
        }

        return response;
    }


}
