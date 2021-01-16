

/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.profile.handler;


import java.util.HashMap;
import java.util.Map;

public class RegistryRequest {

    private String id;

    private Map<String, Object> request = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public void setRequest(Map<String,Object> request) {
        this.request = request;
    }
}
