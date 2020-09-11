package com.tarento.walletService.web.models.communication;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserCoinResponseInfo {

    @JsonProperty("wid")
    private String wid = null;

    @JsonProperty("responseCode")
    private Integer responseCode = null;

    @JsonProperty("responseData")
    private Object responseData = null;

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Object getResponseData() {
        return responseData;
    }

    public void setResponseData(Object responseData) {
        this.responseData = responseData;
    }

}
