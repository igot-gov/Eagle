/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.model;

import java.util.ArrayList;
import java.util.List;

public class UserCompetencyRequest {

    private String userId;
    private String userRole;

    private List<CompetencyRequest> competencyRequests = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public List<CompetencyRequest> getCompetencyRequests() {
        return competencyRequests;
    }

    public void setCompetencyRequests(List<CompetencyRequest> competencyRequests) {
        this.competencyRequests = competencyRequests;
    }
}
