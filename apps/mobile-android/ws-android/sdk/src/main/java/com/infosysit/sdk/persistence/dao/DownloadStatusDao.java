package com.infosysit.sdk.persistence.dao;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;

import java.util.List;

/**
 * Created by Sujeet_Jaiswal on 4/13/2018.
 */

@Dao
public interface DownloadStatusDao {

    @Insert(onConflict = 1)
    void insertAll(DownloadStatusEntity... downloadStatusEntities) throws Exception;

    @Query("SELECT downloadStatus FROM download_status WHERE contentId IN (:contentIds)")
    List<String> getDownloadStatusForIds(String[] contentIds) throws Exception;


//    @Query("SELECT * FROM download_status")
//    List<DownloadStatusEntity> getAllDownloads();

    @Query("UPDATE download_status SET percentCompleted=:downloadPercentage,downloadStatus=:downloadStatus WHERE downloadId=:downloadId") // TODO: change on conflict strategy
    void updateDownloadStatus(long downloadId, double downloadPercentage, String downloadStatus) throws Exception;

    @Query("DELETE FROM download_status")
    void deleteAll() throws Exception;

    @Update
    void updateDownloadStatus(DownloadStatusEntity downloadStatusEntity) throws Exception;

    @Query("SELECT * FROM download_status WHERE contentId=:contentId")
    DownloadStatusEntity getDownloadStatusByContentId(String contentId);

    @Query("SELECT * FROM download_status WHERE downloadId=:downloadId")
    DownloadStatusEntity getDownloadStatusByDownloadId(long downloadId);

//    TODO: status downloaded or ready
    @Query("SELECT contentId FROM download_status WHERE downloadStatus='DOWNLOADED'")
    List<String> getSuccessfulDownloads();

    @Query("DELETE FROM download_status WHERE contentId = :contentId")
    void delete(String contentId);

    @Query("DELETE FROM download_status WHERE contentId IN (:contentIds)")
    void deleteAll(String[] contentIds);

    @Query("SELECT COUNT(*) FROM download_status WHERE isInitiatedByUser=1 AND downloadStatus='INITIATED'")
    int downloadInProgressCount();

//    @Query("SELECT * FROM download_status where downloadUrl like '%openrap%' ")
//    List<DownloadStatusEntity> downloadOpenRap();
}

//    String[] contentIds = new String[] {"abc", "def", "ghi", "jkl"}
