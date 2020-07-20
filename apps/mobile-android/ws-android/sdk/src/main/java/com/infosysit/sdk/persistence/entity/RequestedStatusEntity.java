package com.infosysit.sdk.persistence.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Sujeet_Jaiswal on 4/13/2018.
 */

@Entity(tableName = "requested_status")
public class RequestedStatusEntity {

    @PrimaryKey
    @NonNull
    private String contentId;

    private String requestedStatus;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getRequestedStatus() {
        return requestedStatus;
    }

    public void setRequestedStatus(String requestedStatus) {
        this.requestedStatus = requestedStatus;
    }
}
