package com.infosysit.sdk.persistence.dao;



import androidx.room.Dao;
import androidx.room.Insert;

import com.infosysit.sdk.persistence.entity.RequestedStatusEntity;

/**
 * Created by Sujeet_Jaiswal on 4/13/2018.
 */

@Dao
public interface RequestedStatusDao {

    @Insert
    void insertAll(RequestedStatusEntity... requestedStatusEntities);
}
