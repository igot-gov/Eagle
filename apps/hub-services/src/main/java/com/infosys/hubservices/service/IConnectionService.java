/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.service;

import com.infosys.hubservices.model.ConnectionRequest;
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.model.cassandra.UserConnection;
import com.infosys.hubservices.util.Constants;

import java.util.List;

public interface IConnectionService {

    /**
     * Creates a connection
     * @param rootOrg
     * @param request
     * @return
     */
    Response add(String rootOrg, List<ConnectionRequest> requests) throws Exception;

    /**
     * To update the status and dates of connection
     * @param rootOrg
     * @param request
     * @return
     */
    Response update(String rootOrg, List<ConnectionRequest> requests) throws Exception;

    /**
     * Find related connections from existing connections
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    Response findSuggestedConnections(String rootOrg, String userId, int offset, int limit);


    public Response findAllConnectionsIdsByStatus(String rootOrg, String userId, String status, int offset, int limit);


        /**
         * Find connections which is not established/pending for approval
         * @param userId
         * @return
         */
    Response findConnectionsRequested(String rootOrg, String userId, int offset, int limit, Constants.DIRECTION direction);

    /**
     * Send notification
     *
     * @param rootOrg
     * @param eventId
     * @param userConnection
     */
    void sendNotification(String rootOrg, String eventId, String sender, String reciepient, String status);

    List<String> findUserConnections(String rootOrg, String userId) throws Exception;




}
