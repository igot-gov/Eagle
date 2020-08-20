/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.model.cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Table("scrom_template")
public class ScromModel {

    @PrimaryKey
    private ScromPrimaryKey scromPrimaryKey;

    @Column("type")
    private String type;

    @Column("initialized")
    private boolean initialized;

    @Column("cmi_core_exit")
    private String cmiCoreExit;

    @Column("cmi_core_lesson_status")
    private String cmiCoreLessonStatus;

    @Column("cmi_core_session_time")
    private String cmiCoreSessionTime;

    @Column("cmi_suspend_data")
    private String cmiSuspendData;

    public ScromModel(ScromPrimaryKey scromPrimaryKey){
        super();
        this.scromPrimaryKey = scromPrimaryKey;
    }

    public void setScromPrimaryKey(ScromPrimaryKey scromPrimaryKey){
        this.scromPrimaryKey = scromPrimaryKey;
    }

    public String getRootOrg() {
        return scromPrimaryKey.getRootOrg();
    }

    public String getOrg() {
        return scromPrimaryKey.getOrg();
    }

    public String getContentId() {
        return scromPrimaryKey.getContentId();
    }

    public String getUserId() {
        return scromPrimaryKey.getUserId();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getCmiCoreExit() {
        return cmiCoreExit;
    }

    public void setCmiCoreExit(String cmiCoreExit) {
        this.cmiCoreExit = cmiCoreExit;
    }

    public String getCmiCoreLessonStatus() {
        return cmiCoreLessonStatus;
    }

    public void setCmiCoreLessonStatus(String cmiCoreLessonStatus) {
        this.cmiCoreLessonStatus = cmiCoreLessonStatus;
    }

    public String getCmiCoreSessionTime() {
        return cmiCoreSessionTime;
    }

    public void setCmiCoreSessionTime(String cmiCoreSessionTime) {
        this.cmiCoreSessionTime = cmiCoreSessionTime;
    }

    public String getCmiSuspendData() {
        return cmiSuspendData;
    }

    public void setCmiSuspendData(String cmiSuspendData) {
        this.cmiSuspendData = cmiSuspendData;
    }

}
