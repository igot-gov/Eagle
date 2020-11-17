package com.igot.profileManager.models.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class ProfileWfStatusPrimarykey {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(name = "root_org", type = PrimaryKeyType.PARTITIONED)
    private String rootOrg;

    @PrimaryKeyColumn(name = "org", type = PrimaryKeyType.PARTITIONED)
    private String org;

    @PrimaryKeyColumn("current_status")
    private String currentStatus;

    @PrimaryKeyColumn(name = "user_id")
    private String userId;


    public ProfileWfStatusPrimarykey(String rootOrg, String userId, String currentStatus) {
        this.rootOrg = rootOrg;
        this.userId = userId;
        this.currentStatus = currentStatus;
    }

    public void setRootOrg(String rootOrg) {
        this.rootOrg = rootOrg;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRootOrg() {
        return rootOrg;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}

