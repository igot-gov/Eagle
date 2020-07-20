package com.infosysit.sdk.persistence.entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Sujeet_Jaiswal on 4/13/2018.
 */

@Entity(tableName = "content")
public class ContentEntity {

    @PrimaryKey
    @NonNull
    private String contentId;

    private long downloadId;
    private String contentType;
    private String requestedDate;
    private String modifiedDate;
    private String expiryDate;
    private String contentMetaJson;
    private String children; // comma separated ids of children
    private String parentId;
    private String extension;



    public ContentEntity(@NonNull String contentId, long downloadId, String contentType, String requestedDate, String modifiedDate, String expiryDate, String contentMetaJson, String children, String parentId, String extension) {
        this.contentId = contentId;
        this.downloadId = downloadId;
        this.contentType = contentType;
        this.requestedDate = requestedDate;
        this.modifiedDate = modifiedDate;
        this.expiryDate = expiryDate;
        this.contentMetaJson = contentMetaJson;
        this.children = children;
        this.parentId = parentId;
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "ContentEntity{" +
                "contentId='" + contentId + '\'' +
                ", downloadId=" + downloadId +
                ", contentType='" + contentType + '\'' +
                ", requestedDate='" + requestedDate + '\'' +
                ", modifiedDate='" + modifiedDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", contentMetaJson='" + contentMetaJson + '\'' +
                ", children='" + children + '\'' +
                ", parentId='" + parentId + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getContentMetaJson() {
        return contentMetaJson;
    }

    public void setContentMetaJson(String contentMetaJson) {
        this.contentMetaJson = contentMetaJson;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
