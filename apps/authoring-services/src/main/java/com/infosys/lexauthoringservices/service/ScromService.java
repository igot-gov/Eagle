/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.service;

import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.ScromRequest;

import java.util.Map;

public interface ScromService {

    public Response upsert(ScromRequest scromData,  String rootOrg, String org) throws Exception;
    public Response fetch(Map<String,Object> scromData) throws Exception;
    public Response delete(Map<String,Object> scromData) throws Exception;

}
