package com.igot.profileManager.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WfStatus {

    private String state;

    @JsonProperty(value = "isStartState")
    private Boolean isStartState;

    private Boolean isLastState;

    private List<WfAction> actions;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getIsLastState() {
        return isLastState;
    }

    public void setIsLastState(Boolean isLastState) {
        this.isLastState = isLastState;
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
