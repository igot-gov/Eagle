package com.infosysit.sdk.persistence.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.infosysit.sdk.persistence.entity.ContentEntity;

import java.util.List;

/**
 * Created by Sujeet_Jaiswal on 4/13/2018.
 */

@Dao
public interface ContentDao {

    @Insert(onConflict = 1)
    void insertAll(ContentEntity... contentEntities) throws Exception;

    @Query("SELECT content.* FROM content INNER JOIN download_status ON content.contentId = download_status.contentId WHERE download_status.downloadStatus =:status")
    List<ContentEntity> getAllDownloadedContents(String status) throws Exception;

    @Query("SELECT * FROM content WHERE contentId LIKE :contentId")
    ContentEntity getContentByContentId(String contentId) throws Exception;

    @Query("SELECT * FROM content WHERE downloadId LIKE :downloadId LIMIT 1")
    ContentEntity getContentByDownloadId(long downloadId) throws Exception;

//    @Query("SELECT * FROM content WHERE contentType LIKE :contentType")
//    List<ContentEntity> getContentByType(String contentType);

    @Query("SELECT content.* FROM content INNER JOIN download_status ON content.contentId = download_status.contentId WHERE content.contentType=:contentType AND download_status.isInitiatedByUser=1")
    List<ContentEntity> getContentByTypeInitiatedByUser(String contentType) throws Exception;


    @Query("SELECT COUNT(*) FROM content INNER JOIN download_status ON content.contentId = download_status.contentId WHERE content.contentType=:contentType AND download_status.isInitiatedByUser=1")
    int getContentByTypeInitiatedByUserCount(String contentType);

    @Query("SELECT downloadId FROM content WHERE contentId IN (:contentIds)")
    List<Long> getDownloadIdsForContentIds(String[] contentIds) throws Exception;

    @Query("DELETE FROM content")
    void deleteAll() throws Exception;

    @Update
    void updateContent(ContentEntity contentEntity) throws Exception;

    @Query("DELETE FROM content WHERE contentId = :contentId")
    void delete(String contentId) throws Exception;

    @Query("DELETE FROM content WHERE contentId IN (:contentId)")
    void deleteAll(String[] contentId) throws Exception;
}
