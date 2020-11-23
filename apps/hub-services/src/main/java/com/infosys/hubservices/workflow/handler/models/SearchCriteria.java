package com.infosys.hubservices.workflow.handler.models;

import org.springframework.util.StringUtils;

public class SearchCriteria {

    private String serviceName;

    private String applicationStatus;

    private Integer limit;

    private Integer offset;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public boolean isEmpty() {
        return (StringUtils.isEmpty(this.applicationStatus) && StringUtils.isEmpty(this.serviceName));
    }
}
