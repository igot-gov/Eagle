package com.igot.profileManager.models.cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

//rename user
@Table("profile_work_flow_status")
public class ProfileWfStatus {

    @PrimaryKey
    private ProfileWfStatusPrimarykey profileWfStatusPrimarykey;

    @Column("actor_uuid")
    private String actor_uuid;

    @Column("next_actions")
    private String next_actions;

    @Column("created_on")
    private Date startedOn;

    @Column("lastUpdated_on")
    private Date lastUpdateOn;

    public ProfileWfStatusPrimarykey getProfileWfStatusPrimarykey() {
        return profileWfStatusPrimarykey;
    }

    public void setProfileWfStatusPrimarykey(ProfileWfStatusPrimarykey profileWfStatusPrimarykey) {
        this.profileWfStatusPrimarykey = profileWfStatusPrimarykey;
    }

    public String getActor_uuid() {
        return actor_uuid;
    }

    public void setActor_uuid(String actor_uuid) {
        this.actor_uuid = actor_uuid;
    }

    public String getNext_actions() {
        return next_actions;
    }

    public void setNext_actions(String next_actions) {
        this.next_actions = next_actions;
    }

    public Date getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }

    public Date getLastUpdateOn() {
        return lastUpdateOn;
    }

    public void setLastUpdateOn(Date lastUpdateOn) {
        this.lastUpdateOn = lastUpdateOn;
    }
}
