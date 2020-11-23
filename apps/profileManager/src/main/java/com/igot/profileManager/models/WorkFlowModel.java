package com.igot.profileManager.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkFlowModel {

    private List<WfStatus> wfstates;

    public List<WfStatus> getWfstates() {
        return wfstates;
    }

    public void setWfstates(List<WfStatus> wfstates) {
        this.wfstates = wfstates;
    }
}
