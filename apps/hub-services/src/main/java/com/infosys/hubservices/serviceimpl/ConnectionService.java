/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.hubservices.exception.ApplicationException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }
        return response;

    }

    @Override
    public Response update(String rootOrg, ConnectionRequest request) {
        Response response = new Response();
        try {

            UserConnection userConnection = userConnectionRepository.findByUsersAndConnection(rootOrg, request.getUserId(), request.getConnectionId());
            userConnection.setConnectionStatus(request.getStatus());
            userConnection.setEndOn(request.getEndDate());

            userConnectionRepository.save(userConnection);

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

            if(connectionProperties.isNotificationEnabled())
                sendNotification(rootOrg, connectionProperties.getNotificationTemplateResponse(), userConnection);

        } catch (Exception e){
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }

        return response;
    }

    @Override
    public Response delete(String rootOrg, String userId, String connectionId) {
        Response response = new Response();
        try {

            UserConnection userConnection = userConnectionRepository.findByUsersAndConnection(rootOrg, userId, connectionId);
            userConnection.setConnectionStatus(Constants.Status.DELETED);
            userConnection.setEndOn(new Date());
            userConnectionRepository.save(userConnection);

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

            if(connectionProperties.isNotificationEnabled())
                sendNotification(rootOrg, connectionProperties.getNotificationTemplateResponse(), userConnection);


        } catch (Exception e){
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());

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
    public Response findSuggestedConnections(String rootOrg, String userId, int offset, int limit) {

        Response response = new Response();
        try {
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException(Constants.Message.USER_ID_INVALID);
            }
            Pageable pageable = PageRequest.of(offset, limit);
            Slice<UserConnection> sliceUserConnections = userConnectionRepository.findByUserConnectionPrimarykeyRootOrgAndUserConnectionPrimarykeyUserId(rootOrg, userId,  pageable);

            //get established connections
            List<UserConnection> userApprovedConnections = sliceUserConnections.getContent().stream().filter(c->c.getConnectionStatus().equals(Constants.Status.APPROVED)).collect(Collectors.toList());

            //approved the connectionIds of established connection
            List<String> approvedConnectionIds = userApprovedConnections.stream().map(userConnection -> userConnection.getUserConnectionPrimarykey().getConnectionId()).collect(Collectors.toList());

            //get the established related connection
            List<UserConnection> relatedConnections = userConnectionRepository.findByUsersAndRootOrg(rootOrg, approvedConnectionIds).stream().filter(c->c.getConnectionStatus().equals(Constants.Status.APPROVED)).collect(Collectors.toList());

            //find the common new connections that could be established
            List<UserConnection> commonConnections = relatedConnections.stream().filter(userConnection -> !approvedConnectionIds.contains(userConnection.getUserConnectionPrimarykey().getConnectionId())).collect(Collectors.toList());


            if(commonConnections.isEmpty()){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, commonConnections);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, commonConnections);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

        }catch (Exception e){
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }

        return response;
    }

    @Override
    public Response findConnections(String rootOrg, String userId, int offset, int limit) {
        Response response = new Response();

        try{
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException(Constants.Message.USER_ID_INVALID);
            }

            Pageable pageable = PageRequest.of(offset, limit);
            Slice<UserConnection> sliceUserConnections = userConnectionRepository.findByUserConnectionPrimarykeyRootOrgAndUserConnectionPrimarykeyUserId(rootOrg, userId,  pageable);
            List<UserConnection> userConnectionsEstablished = sliceUserConnections.getContent().stream().filter(c -> c.getConnectionStatus().equals(Constants.Status.APPROVED)).collect(Collectors.toList());


            response.put(Constants.ResponseStatus.PAGENO, offset);
            response.put(Constants.ResponseStatus.HASPAGENEXT, sliceUserConnections.hasNext());
            response.put(Constants.ResponseStatus.TOTALHIT, userConnectionRepository.countByUserAndStatus(userId, Constants.Status.APPROVED));

            if(userConnectionsEstablished.isEmpty()){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, userConnectionsEstablished);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, userConnectionsEstablished);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

        } catch (Exception e){
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());
        }

        return response;
    }

    @Override
    public Response findConnectionsRequested(String rootOrg, String userId, int offset, int limit, Constants.DIRECTION direction) {
        Response response = new Response();

        try{
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException(Constants.Message.USER_ID_INVALID);
            }

            Pageable pageable = PageRequest.of(offset, limit);

            List<UserConnection> userConnections = Collections.emptyList();
            if(direction.equals(Constants.DIRECTION.OUT)){
                Slice<UserConnection> sliceUserConnections = userConnectionRepository.findByUserConnectionPrimarykeyRootOrgAndUserConnectionPrimarykeyUserId(rootOrg, userId,  pageable);
                userConnections = sliceUserConnections.getContent().stream().filter(c -> c.getConnectionStatus().equals(Constants.Status.PENDING)).collect(Collectors.toList());
                response.put(Constants.ResponseStatus.PAGENO, offset);
                response.put(Constants.ResponseStatus.HASPAGENEXT, sliceUserConnections.hasNext());
            }
            System.out.println("rootOrg:"+rootOrg+" userId:"+userId);

            if(direction.equals(Constants.DIRECTION.IN))
                userConnections = userConnectionRepository.findByConnection(rootOrg, userId);


            response.put(Constants.ResponseStatus.TOTALHIT, userConnectionRepository.countByUserAndStatus(userId, Constants.Status.PENDING));

            if(userConnections.isEmpty()){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, userConnections);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, userConnections);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

        } catch (Exception e){
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());
        }

        return response;
    }

    @Override
    public List<String> findUserConnections(String rootOrg, String userId) {

        int count = userConnectionRepository.countByUser(rootOrg, userId);

        Pageable pageable = PageRequest.of(0, count);
        Slice<UserConnection> sliceUserConnections = userConnectionRepository.findByUserConnectionPrimarykeyRootOrgAndUserConnectionPrimarykeyUserId(rootOrg, userId, pageable);

        return sliceUserConnections.getContent().stream().map(uc -> uc.getUserConnectionPrimarykey().getConnectionId()).collect(Collectors.toList());
    }
}
