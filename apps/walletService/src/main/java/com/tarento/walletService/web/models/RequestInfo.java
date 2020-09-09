package com.tarento.walletService.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Request Information which contains details of the User who is sending the request
 */
public class RequestInfo {
    @JsonProperty("wid")
    private String wid = null;

    public RequestInfo wid(String wid) {
        this.wid = wid;
        return this;
    }

    /**
     * Logged In User&#x27;s Unique ID
     *
     * @return wid
     **/
    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestInfo requestInfo = (RequestInfo) o;
        return Objects.equals(this.wid, requestInfo.wid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wid);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestInfo {\n");

        sb.append("    wid: ").append(toIndentedString(wid)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}