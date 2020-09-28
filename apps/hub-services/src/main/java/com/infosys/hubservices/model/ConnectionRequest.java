/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.model;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ConnectionRequest {

    @NotNull
    private String userId;
    private List<String> criterias = new ArrayList<>();


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getCriterias() {
        return criterias;
    }

    public void setCriterias(List<String> criterias) {
        this.criterias = criterias;
    }
}
