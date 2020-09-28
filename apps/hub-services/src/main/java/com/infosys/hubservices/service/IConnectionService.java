/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.service;

import com.infosys.hubservices.model.ConnectionRequest;
import com.infosys.hubservices.model.Response;

public interface IConnectionService {

    Response findRecommendedConnection(ConnectionRequest request);
    Response findCommonConnection(ConnectionRequest request);

    /**
     * Find connections which are established
     * Accepted connection
     * @param userId
     * @return
     */
    Response findConnections(String userId, int offset, int limit);

    /**
     * Find connections which is not established/pending for approval
     * @param userId
     * @return
     */
    Response findConnectionsRequested(String userId, int offset, int limit);



}
