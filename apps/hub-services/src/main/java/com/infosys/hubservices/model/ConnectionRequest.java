/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class ConnectionRequest {

    @NotNull
    //@JsonProperty("userIdTo")
    private String userId;
    @NotNull
    //@JsonProperty("userIdFrom")
    private String connectionId;
    // name - department = label
    //@JsonProperty("userNameTo")
    private String userName;
    //
    private String userDepartment;

    public Integer getUserNodeId() {
        return userNodeId;
    }

    public void setUserNodeId(Integer userNodeId) {
        this.userNodeId = userNodeId;
    }

    public Integer getConnectionNodeId() {
        return connectionNodeId;
    }

    public void setConnectionNodeId(Integer connectionNodeId) {
        this.connectionNodeId = connectionNodeId;
    }

    // name - department = label
    private String connectionName;
    //
    private String connectionDepartment;
    private String status;
    private String type;
    private Date endDate;
    private Integer userNodeId;
    private Integer connectionNodeId;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionDepartment() {
        return connectionDepartment;
    }

    public void setConnectionDepartment(String connectionDepartment) {
        this.connectionDepartment = connectionDepartment;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
