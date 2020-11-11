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
import com.infosys.hubservices.repository.cassandra.bodhi.UserConnectionRepository;
import com.infosys.hubservices.service.IConnectionService;
import com.infosys.hubservices.service.IGraphService;
import com.infosys.hubservices.util.ConnectionProperties;
import com.infosys.hubservices.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import com.infosys.hubservices.model.Node;

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

    @Autowired
    IGraphService graphService;


    @Override
    public Response add(String rootOrg, List<ConnectionRequest> requests) throws Exception{

        Response response = new Response();
        try {

            for(ConnectionRequest request :requests){
                Node from = new Node(request.getUserId(), request.getUserName(), request.getUserDepartment());
                //from.setCreatedAt(new Date());
                from.setUpdatedAt(new Date());

                Node to = new Node(request.getConnectionId(), request.getConnectionName(), request.getConnectionDepartment());
                to.setCreatedAt(new Date());
                //to.setUpdatedAt(to.getCreatedAt());

                graphService.createNodeWithRelation(from, to, Constants.Status.PENDING);
//              if(connectionProperties.isNotificationEnabled())
//                sendNotification(rootOrg, connectionProperties.getNotificationTemplateRequest(),request.getUserId(), request.getConnectionId(),Constants.Status.PENDING);

            }

            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());

        }

        return response;

    }

    @Override
    public Response update(String rootOrg, List<ConnectionRequest> requests) throws Exception{
        Response response = new Response();

        try {

            for(ConnectionRequest request: requests){
                Node from = new Node(request.getUserId(), request.getUserName(), request.getUserDepartment());
                from.setUpdatedAt(new Date());
                Node to = new Node(request.getConnectionId(), request.getConnectionName(), request.getConnectionDepartment());
                to.setUpdatedAt(new Date());

                graphService.deleteRelation(from, to, null);
                graphService.createNodeWithRelation(to, from, request.getStatus());

//            if(connectionProperties.isNotificationEnabled())
//                sendNotification(rootOrg, connectionProperties.getNotificationTemplateResponse(), request.getConnectionId(), request.getUserId(),request.getStatus());

            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
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
    public Response findSuggestedConnections(String rootOrg, String userId, int offset, int limit) {

        Response response = new Response();
        try {
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException(Constants.Message.USER_ID_INVALID);
            }
            List<Node> nodes = graphService.getNodesNextLevel(userId, Constants.Status.APPROVED, offset, limit );

            List<String> allNodesIds = findUserConnections(rootOrg, userId);
            List<Node> detachedNodes = nodes.stream().filter(node -> !allNodesIds.contains(node.getIdentifier())).collect(Collectors.toList());

            //System.out.println("commons ->"+new ObjectMapper().writeValueAsString(commonConnections));
            if(detachedNodes.isEmpty()){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, detachedNodes);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, detachedNodes);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

        }catch (Exception e){
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

            List<Node> nodes = graphService.getNodesInAndOutEdge(userId, status, offset, limit);
            int count = graphService.getAllNodeCount(userId, status);

            response.put(Constants.ResponseStatus.PAGENO, offset);
            //response.put(Constants.ResponseStatus.HASPAGENEXT, sliceUserConnections.hasNext());
            response.put(Constants.ResponseStatus.TOTALHIT, count);

            if(nodes.isEmpty()){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, nodes);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, nodes);
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

            List<Node> nodes = new ArrayList<>();
            if(direction.equals(Constants.DIRECTION.IN))
                 nodes = graphService.getNodesInEdge(userId, Constants.Status.PENDING, offset, limit);

            else
                nodes = graphService.getNodesOutEdge(userId, Constants.Status.PENDING, offset, limit);

            if(nodes.isEmpty()){
                response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.FAILED);
                response.put(Constants.ResponseStatus.DATA, nodes);
                response.put(Constants.ResponseStatus.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(Constants.ResponseStatus.MESSAGE, Constants.ResponseStatus.SUCCESSFUL);
            response.put(Constants.ResponseStatus.DATA, nodes);
            response.put(Constants.ResponseStatus.STATUS, HttpStatus.OK);

        } catch (Exception e){
            throw new ApplicationException(Constants.Message.FAILED_CONNECTION + e.getMessage());
        }

        return response;
    }

    @Override
    public List<String> findUserConnections(String rootOrg, String userId) throws Exception{
        return graphService.getAllNodes(userId).stream().map(node -> node.getIdentifier()).collect(Collectors.toList());

    }
}
