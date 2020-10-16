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

public interface IConnectionService {

    /**
     * Creates a connection
     * @param rootOrg
     * @param request
     * @return
     */
    Response add(String rootOrg, ConnectionRequest request);

    /**
     * To update the status and dates of connection
     * @param rootOrg
     * @param request
     * @return
     */
    Response update(String rootOrg, ConnectionRequest request);

    /**
     * Marked as rejected - deletes the connection
     * @param userId
     * @param connectionId
     * @return
     */
    Response delete(String rootOrg, String userId, String connectionId);


    Response findRecommendedConnection(ConnectionRequest request);

    /**
     * Find related connections from existing connections
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    Response findSuggestedConnections(String rootOrg, String userId, int offset, int limit);

    /**
     * Find connections which are established
     * Accepted connection
     * @param userId
     * @return
     */
    Response findConnections(String rootOrg, String userId, int offset, int limit);

    /**
     * Find connections which is not established/pending for approval
     * @param userId
     * @return
     */
    Response findConnectionsRequested(String rootOrg, String userId, int offset, int limit);

    /**
     * Send notification
     *
     * @param rootOrg
     * @param eventId
     * @param userConnection
     */
    void sendNotification(String rootOrg, String eventId, UserConnection userConnection);



}
