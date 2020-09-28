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
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.repository.cassandra.bodhi.UserConnectionRepository;
import com.infosys.hubservices.service.IConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionService implements IConnectionService {

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    @Override
    public Response findRecommendedConnection(ConnectionRequest request) {
        return null;
    }

    @Override
    public Response findCommonConnection(ConnectionRequest request) {
        return null;
    }

    @Override
    public Response findConnections(String userId, int offset, int limit) {
        return null;
    }

    @Override
    public Response findConnectionsRequested(String userId, int offset, int limit) {
        Response response = new Response();

        try{
            if(userId==null || userId.isEmpty()){
                throw new BadRequestException("user_id cant be null or empty");
            }
            List<UserConnection> userConnections = userConnectionRepository.findByUserAndTypeAndStatus(userId, "requests", "Pending");
            if(userConnections.size()==0){
                response.put(response.MESSAGE, response.FAILED);
                response.put(response.DATA, userConnections);
                response.put(response.STATUS, HttpStatus.NO_CONTENT);
            }
            response.put(response.MESSAGE, response.SUCCESSFUL);
            response.put(response.DATA, userConnections);
            response.put(response.STATUS, HttpStatus.OK);

        } catch (Exception e){
            throw new ApplicationServiceError("Failed to search contents: "+e.getMessage());
        }


        return response;
    }


}
