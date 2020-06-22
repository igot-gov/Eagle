package com.infosysit.ledsdk.persistence.dao;

import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.infosysit.ledsdk.persistence.entity.RequestedStatusEntity;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;

@SuppressWarnings({"unchecked", "deprecation"})
public final class RequestedStatusDao_Impl implements RequestedStatusDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RequestedStatusEntity> __insertionAdapterOfRequestedStatusEntity;

  public RequestedStatusDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRequestedStatusEntity = new EntityInsertionAdapter<RequestedStatusEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `requested_status` (`contentId`,`requestedStatus`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, RequestedStatusEntity value) {
        if (value.getContentId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getContentId());
        }
        if (value.getRequestedStatus() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getRequestedStatus());
        }
      }
    };
  }

  @Override
  public void insertAll(final RequestedStatusEntity... requestedStatusEntities) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfRequestedStatusEntity.insert(requestedStatusEntities);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }
}
