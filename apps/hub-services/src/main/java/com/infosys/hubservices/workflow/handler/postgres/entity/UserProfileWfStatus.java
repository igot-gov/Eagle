package com.infosys.hubservices.workflow.handler.postgres.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_profile_wf_status", schema = "wingspan")
public class UserProfileWfStatus {

    @Id
    @Column(name = "wf_id", nullable = false)
    private String wfId;

    @Column(name = "userid", nullable = false)
    private String userId;

    @Column(name = "rootOrg")
    private String rootOrg;

    @Column(name = "org")
    private String org;

    @Column(name = "actor_uuid")
    private String actorUUID;

    @Column(name = "current_status")
    private String currentStatus;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "lastupdated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedOn;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRootOrg() {
        return rootOrg;
    }

    public void setRootOrg(String rootOrg) {
        this.rootOrg = rootOrg;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getActorUUID() {
        return actorUUID;
    }

    public void setActorUUID(String actorUUID) {
        this.actorUUID = actorUUID;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(Date lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }
}
