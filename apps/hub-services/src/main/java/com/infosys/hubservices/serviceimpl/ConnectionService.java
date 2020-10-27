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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ConnectionService implements IConnectionService {

    private Logger logger = LoggerFactory.getLogger(ConnectionService.class);
    @Autowired
    private ObjectMapper mapper;

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

            logger.info("Add request: {}", mapper.writeValueAsString(request));
            UserConnectionPrimarykey pk = new UserConnectionPrimarykey(rootOrg, request.getUserId(), request.getConnectionId());
            UserConnection userConnection = new UserConnection(pk, Constants.Status.PENDING, "", new Date());
            userConnectionRepository.save(userConnection);

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.CREATED);

            if(connectionProperties.isNotificationEnabled())
                sendNotification(rootOrg, connectionProperties.getNotificationTemplateRequest(),request.getUserId(), request.getConnectionId(),Constants.Status.PENDING);



        } catch (Exception e){
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }
        return response;

    }

    @Override
    public Response update(String rootOrg, ConnectionRequest request) {
        Response response = new Response();
        try {
            logger.info("update request: {}", mapper.writeValueAsString(request));

            UserConnection userConnection = userConnectionRepository.findByUsersAndConnection(rootOrg, request.getUserId(), request.getConnectionId());
            userConnection.setConnectionStatus(request.getStatus());
            //as updated date
            userConnection.setEndOn(request.getEndDate()==null? new Date() : request.getEndDate());

            userConnectionRepository.save(userConnection);

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

            if(connectionProperties.isNotificationEnabled())
                sendNotification(rootOrg, connectionProperties.getNotificationTemplateResponse(), request.getConnectionId(), request.getUserId(),request.getStatus());

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


        } catch (Exception e){
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }

        return response;
    }

    @Override
    public void sendNotification(String rootOrg, String eventId, String sender, String reciepient, String status) {
        NotificationEvent event = notificationService.buildEvent(eventId, sender, reciepient, status);
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

            //OUT
            Pageable pageable = PageRequest.of(offset, limit);
            Slice<UserConnection> sliceUserConnections = userConnectionRepository.findByUserConnectionPrimarykeyRootOrgAndUserConnectionPrimarykeyUserId(rootOrg, userId,  pageable);
            List<String> connectionIds = sliceUserConnections.getContent().stream().map(userConnection -> userConnection.getUserConnectionPrimarykey().getConnectionId()).collect(Collectors.toList());
            //List<UserConnection> relatedConnections = userConnectionRepository.findByUsersAndRootOrg(rootOrg, connectionIds).stream().filter(c->c.getConnectionStatus().equals(Constants.Status.APPROVED)).collect(Collectors.toList());
            //List<UserConnection> commonConnections = relatedConnections.stream().filter(userConnection -> !connectionIds.contains(userConnection.getUserConnectionPrimarykey().getConnectionId())).collect(Collectors.toList());


            //IN
            List<UserConnection> userConnectionsIn = userConnectionRepository.findByConnectionAndRootOrg(rootOrg, userId);
            List<String> connectedUserIds = userConnectionsIn.stream().map(userConnection -> userConnection.getUserConnectionPrimarykey().getUserId()).collect(Collectors.toList());
            connectionIds.addAll(connectedUserIds);
            connectionIds.add(userId);

            List<UserConnection> relatedConnections = userConnectionRepository.findByUsersAndRootOrg(rootOrg, connectionIds).stream().filter(c->c.getConnectionStatus().equals(Constants.Status.APPROVED)).collect(Collectors.toList());
            List<UserConnection> commonConnections = relatedConnections.stream().filter(userConnection -> !connectionIds.contains(userConnection.getUserConnectionPrimarykey().getConnectionId())).collect(Collectors.toList());


            commonConnections.sort(Comparator.comparing(UserConnection::getStartedOn).reversed());

            //System.out.println("commons ->"+new ObjectMapper().writeValueAsString(commonConnections));
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

            //for direction OUT
            Pageable pageable = PageRequest.of(offset, limit);
            Slice<UserConnection> sliceUserConnections = userConnectionRepository.findByUserConnectionPrimarykeyRootOrgAndUserConnectionPrimarykeyUserId(rootOrg, userId,  pageable);
            List<UserConnection> userConnectionsEstablished = sliceUserConnections.getContent().stream().filter(c -> c.getConnectionStatus().equals(Constants.Status.APPROVED)).collect(Collectors.toList());

            //for direction IN
            userConnectionsEstablished.addAll(userConnectionRepository.findByConnection(rootOrg, userId, Constants.Status.APPROVED));

            response.put(Constants.ResponseStatus.PAGENO, offset);
            response.put(Constants.ResponseStatus.HASPAGENEXT, sliceUserConnections.hasNext());
            response.put(Constants.ResponseStatus.TOTALHIT, userConnectionRepository.countByUserAndStatus(userId, Constants.Status.APPROVED));

            userConnectionsEstablished.sort(Comparator.comparing(UserConnection::getStartedOn).reversed());

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
    public Response findAllConnectionsIdsByStatus(String rootOrg, String userId, String status, int offset, int limit) {
        Response response = new Response();

        try{
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException(Constants.Message.USER_ID_INVALID);
            }

            List<String> connectionIds = new ArrayList<>();

            //for direction OUT
            Pageable pageable = PageRequest.of(offset, limit);
            Slice<UserConnection> sliceUserConnections = userConnectionRepository.findByUserConnectionPrimarykeyRootOrgAndUserConnectionPrimarykeyUserId(rootOrg, userId,  pageable);
            List<UserConnection> userConnectionsEstablishedIn = sliceUserConnections.getContent().stream().filter(c -> c.getConnectionStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
            //connectionIds.addAll(userConnectionsEstablishedIn.stream().map(uc -> uc.getUserConnectionPrimarykey().getConnectionId()).collect(Collectors.toList()));


            //for direction IN
            List<UserConnection> userConnectionsEstablishedOut = userConnectionRepository.findByConnection(rootOrg, userId, status);

            //Merge in and out
            userConnectionsEstablishedIn.addAll(userConnectionsEstablishedOut);

            // sort all
            userConnectionsEstablishedIn.sort(Comparator.comparing(UserConnection::getEndOn, Comparator.nullsFirst(
                    Comparator.naturalOrder())).reversed());

            //filter all ids
            connectionIds = userConnectionsEstablishedIn.stream().map(uc -> uc.getUserConnectionPrimarykey().getUserId()).collect(Collectors.toList());

            logger.info("established sorted connectionIds "+connectionIds);

            response.put(Constants.ResponseStatus.PAGENO, offset);
            response.put(Constants.ResponseStatus.HASPAGENEXT, sliceUserConnections.hasNext());

            response.put(Constants.ResponseStatus.TOTALHIT, userConnectionRepository.countByUserAndStatus(userId, status)+userConnectionRepository.countByConnectionAndStatus(userId, status));

            if(connectionIds.isEmpty()){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, connectionIds);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, connectionIds);
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
                userConnections = userConnectionRepository.findByConnection(rootOrg, userId, Constants.Status.PENDING);


            userConnections.sort(Comparator.comparing(UserConnection::getStartedOn).reversed());

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

        List<String> connectionIds = new ArrayList<>();
        connectionIds = userConnectionRepository.findByConnectionAndRootOrg(rootOrg, userId).stream().map(c->c.getUserConnectionPrimarykey().getUserId()).collect(Collectors.toList());
        connectionIds.addAll(userConnectionRepository.findByUsersAndRootOrg(rootOrg, Arrays.asList(userId)).stream().map(c->c.getUserConnectionPrimarykey().getConnectionId()).collect(Collectors.toList()));

        return connectionIds;

    }
}
