package com.infosysit.sdk.persistence.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Sujeet_Jaiswal on 4/13/2018.
 */

@Entity(tableName = "download_status")
public class DownloadStatusEntity {

    @PrimaryKey
    @NonNull
    private String contentId;

    private boolean isInitiatedByUser;
    private String downloadUrl;
    private String downloadStatus;
    private double percentCompleted;
    private long downloadId;
    private int numberOfAttempts;


    //adding a column - migration
//    private String user_email;



    public DownloadStatusEntity(@NonNull String contentId, boolean isInitiatedByUser, String downloadUrl, String downloadStatus, double percentCompleted, long downloadId, int numberOfAttempts) {
        this.contentId = contentId;
        this.isInitiatedByUser = isInitiatedByUser;
        this.downloadUrl = downloadUrl;
        this.downloadStatus = downloadStatus;
        this.percentCompleted = percentCompleted;
        this.downloadId = downloadId;
        this.numberOfAttempts = numberOfAttempts;
//        this.user_email = user_email;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public boolean isInitiatedByUser() {
        return isInitiatedByUser;
    }

    public void setInitiatedByUser(boolean initiatedByUser) {
        isInitiatedByUser = initiatedByUser;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public double getPercentCompleted() {
        return percentCompleted;
    }

    public void setPercentCompleted(double percentCompleted) {
        this.percentCompleted = percentCompleted;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(int numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }
//    public String getUser_email() {
//        return user_email;
//    }
//
//    public void setUser_email(String user_email) {
//        this.user_email = user_email;
//    }


    @Override
    public String toString() {
        return "DownloadStatusEntity{" +
                "contentId='" + contentId + '\'' +
                ", isInitiatedByUser=" + isInitiatedByUser +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", downloadStatus='" + downloadStatus + '\'' +
                ", percentCompleted=" + percentCompleted +
                ", downloadId=" + downloadId +
                ", numberOfAttempts=" + numberOfAttempts +
//                ", user_email=" + user_email +
                '}';
    }
}
