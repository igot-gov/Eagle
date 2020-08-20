/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ScromData {

    @NotNull @NotBlank
    private String userId;
    @NotNull @NotBlank
    private String contentId;
    private String type;
    @NotNull
    @JsonProperty("Initialized")
    private boolean initialized;
    @JsonProperty("cmi.core.exit")
    private String cmiCoreExit;
    @JsonProperty("cmi.core.lesson_status")
    private String cmiCoreLessonStatus;
    @JsonProperty("cmi.core.session_time")
    private String cmiCoreSessionTime;
    @JsonProperty("cmi.suspend_data")
    private String cmiSuspendData;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
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
