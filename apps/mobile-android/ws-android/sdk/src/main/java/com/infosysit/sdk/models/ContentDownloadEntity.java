package com.infosysit.sdk.models;

import com.infosysit.sdk.persistence.entity.ContentEntity;
import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;

import java.util.ArrayList;

public class ContentDownloadEntity {
    public ArrayList<ContentEntity> contentEntities;
    public ArrayList<DownloadStatusEntity> downloadStatusEntities;

    public ContentDownloadEntity() {
        this.contentEntities = new ArrayList<>();
        this.downloadStatusEntities = new ArrayList<>();
    }
}
