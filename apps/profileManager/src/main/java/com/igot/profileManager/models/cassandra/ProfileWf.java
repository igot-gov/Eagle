package com.igot.profileManager.models.cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("profile_work_flow")
public class ProfileWf {

    @PrimaryKey
    private ProfileWfPrimaryKey profileWfPrimarykey;

    @Column("config")
    private String configuration;

    public ProfileWfPrimaryKey getProfileWfPrimarykey() {
        return profileWfPrimarykey;
    }

    public void setProfileWfPrimarykey(ProfileWfPrimaryKey profileWfPrimarykey) {
        this.profileWfPrimarykey = profileWfPrimarykey;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public ProfileWf() {
        super();
    }
    
    public ProfileWf(ProfileWfPrimaryKey profileWfPrimarykey, String configuration) {
        super();
        this.profileWfPrimarykey = profileWfPrimarykey;
        this.configuration = configuration;
    }
}
