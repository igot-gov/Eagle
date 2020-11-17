package com.igot.profileManager.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WfStatus {

    private String state;

    private Boolean isStartState;

    private Boolean islastState;

    private List<WfAction> actions;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getIslastState() {
        return islastState;
    }

    public void setIslastState(Boolean islastState) {
        this.islastState = islastState;
    }

    public List<WfAction> getActions() {
        return actions;
    }

    public void setActions(List<WfAction> actions) {
        this.actions = actions;
    }

    public Boolean getStartState() {
        return isStartState;
    }

    public void setStartState(Boolean startState) {
        isStartState = startState;
    }
}
