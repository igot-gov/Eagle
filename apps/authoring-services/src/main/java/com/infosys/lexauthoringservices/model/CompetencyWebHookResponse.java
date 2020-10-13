package com.infosys.lexauthoringservices.model;

import java.util.HashMap;

public class CompetencyWebHookResponse {
    Boolean responseData;

    HashMap<String, Object> statusInfo;

    public Boolean getResponseData() {
        return responseData;
    }

    public void setResponseData(Boolean responseData) {
        this.responseData = responseData;
    }

    public HashMap<String, Object> getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(HashMap<String, Object> statusInfo) {
        this.statusInfo = statusInfo;
    }
}
