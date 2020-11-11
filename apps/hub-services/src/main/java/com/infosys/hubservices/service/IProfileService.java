/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.service;

import com.infosys.hubservices.model.MultiSearch;
import com.infosys.hubservices.model.Response;
import com.infosys.hubservices.util.Constants;

import java.util.List;

public interface IProfileService {


    /**
     * Find related profile from existing connections
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    Response findCommonProfile(String rootOrg, String userId, int offset, int limit);

    /**
     * Find profile for which connections are established
     * Accepted connection
     * @param userId
     * @return
     */
    Response findProfiles(String rootOrg, String userId, int offset, int limit);

    /**
     * Find profiles for which is not established/pending for approval
     * @param userId
     * @return
     */
    Response findProfileRequested(String rootOrg, String userId, int offset, int limit, Constants.DIRECTION direction);


    Response multiSearchProfiles(String rootOrg, String userId, MultiSearch multiSearchRequest, String[] sourceFields);



}
