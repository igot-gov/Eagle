package com.infosysit.ledsdk.persistence.dao;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.arch.persistence.room.util.StringUtil;
import android.database.Cursor;
import com.infosysit.ledsdk.persistence.entity.DownloadStatusEntity;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class DownloadStatusDao_Impl implements DownloadStatusDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfDownloadStatusEntity;

  private final EntityDeletionOrUpdateAdapter __updateAdapterOfDownloadStatusEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDownloadStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public DownloadStatusDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDownloadStatusEntity = new EntityInsertionAdapter<DownloadStatusEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `download_status`(`contentId`,`isInitiatedByUser`,`downloadUrl`,`downloadStatus`,`percentCompleted`,`downloadId`,`numberOfAttempts`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DownloadStatusEntity value) {
        if (value.getContentId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getContentId());
        }
        final int _tmp;
        _tmp = value.isInitiatedByUser() ? 1 : 0;
        stmt.bindLong(2, _tmp);
        if (value.getDownloadUrl() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDownloadUrl());
        }
        if (value.getDownloadStatus() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDownloadStatus());
        }
        stmt.bindDouble(5, value.getPercentCompleted());
        stmt.bindLong(6, value.getDownloadId());
        stmt.bindLong(7, value.getNumberOfAttempts());
      }
    };
    this.__updateAdapterOfDownloadStatusEntity = new EntityDeletionOrUpdateAdapter<DownloadStatusEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `download_status` SET `contentId` = ?,`isInitiatedByUser` = ?,`downloadUrl` = ?,`downloadStatus` = ?,`percentCompleted` = ?,`downloadId` = ?,`numberOfAttempts` = ? WHERE `contentId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DownloadStatusEntity value) {
        if (value.getContentId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getContentId());
        }
        final int _tmp;
        _tmp = value.isInitiatedByUser() ? 1 : 0;
        stmt.bindLong(2, _tmp);
        if (value.getDownloadUrl() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDownloadUrl());
        }
        if (value.getDownloadStatus() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDownloadStatus());
        }
        stmt.bindDouble(5, value.getPercentCompleted());
        stmt.bindLong(6, value.getDownloadId());
        stmt.bindLong(7, value.getNumberOfAttempts());
        if (value.getContentId() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getContentId());
        }
      }
    };
    this.__preparedStmtOfUpdateDownloadStatus = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE download_status SET percentCompleted=?,downloadStatus=? WHERE downloadId=?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM download_status";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM download_status WHERE contentId = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(DownloadStatusEntity... downloadStatusEntities) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfDownloadStatusEntity.insert(downloadStatusEntities);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateDownloadStatus(DownloadStatusEntity downloadStatusEntity) {
    __db.beginTransaction();
    try {
      __updateAdapterOfDownloadStatusEntity.handle(downloadStatusEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateDownloadStatus(long downloadId, double downloadPercentage,
      String downloadStatus) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDownloadStatus.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      _stmt.bindDouble(_argIndex, downloadPercentage);
      _argIndex = 2;
      if (downloadStatus == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, downloadStatus);
      }
      _argIndex = 3;
      _stmt.bindLong(_argIndex, downloadId);
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateDownloadStatus.release(_stmt);
    }
  }

  @Override
  public void deleteAll() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public void delete(String contentId) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (contentId == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, contentId);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDelete.release(_stmt);
    }
  }

  @Override
  public void deleteAllContent() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public List<String> getDownloadStatusForIds(String[] contentIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT downloadStatus FROM download_status WHERE contentId IN (");
    final int _inputSize = contentIds.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : contentIds) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindString(_argIndex, _item);
      }
      _argIndex ++;
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final List<String> _result = new ArrayList<String>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final String _item_1;
        _item_1 = _cursor.getString(0);
        _result.add(_item_1);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DownloadStatusEntity> getAllDownloads() {
    final String _sql = "SELECT * FROM download_status";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfContentId = _cursor.getColumnIndexOrThrow("contentId");
      final int _cursorIndexOfIsInitiatedByUser = _cursor.getColumnIndexOrThrow("isInitiatedByUser");
      final int _cursorIndexOfDownloadUrl = _cursor.getColumnIndexOrThrow("downloadUrl");
      final int _cursorIndexOfDownloadStatus = _cursor.getColumnIndexOrThrow("downloadStatus");
      final int _cursorIndexOfPercentCompleted = _cursor.getColumnIndexOrThrow("percentCompleted");
      final int _cursorIndexOfDownloadId = _cursor.getColumnIndexOrThrow("downloadId");
      final int _cursorIndexOfNumberOfAttempts = _cursor.getColumnIndexOrThrow("numberOfAttempts");
      final List<DownloadStatusEntity> _result = new ArrayList<DownloadStatusEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DownloadStatusEntity _item;
        final String _tmpContentId;
        _tmpContentId = _cursor.getString(_cursorIndexOfContentId);
        final boolean _tmpIsInitiatedByUser;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsInitiatedByUser);
        _tmpIsInitiatedByUser = _tmp != 0;
        final String _tmpDownloadUrl;
        _tmpDownloadUrl = _cursor.getString(_cursorIndexOfDownloadUrl);
        final String _tmpDownloadStatus;
        _tmpDownloadStatus = _cursor.getString(_cursorIndexOfDownloadStatus);
        final double _tmpPercentCompleted;
        _tmpPercentCompleted = _cursor.getDouble(_cursorIndexOfPercentCompleted);
        final long _tmpDownloadId;
        _tmpDownloadId = _cursor.getLong(_cursorIndexOfDownloadId);
        final int _tmpNumberOfAttempts;
        _tmpNumberOfAttempts = _cursor.getInt(_cursorIndexOfNumberOfAttempts);
        _item = new DownloadStatusEntity(_tmpContentId,_tmpIsInitiatedByUser,_tmpDownloadUrl,_tmpDownloadStatus,_tmpPercentCompleted,_tmpDownloadId,_tmpNumberOfAttempts);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public DownloadStatusEntity getDownloadStatusByContentId(String contentId) {
    final String _sql = "SELECT * FROM download_status WHERE contentId=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (contentId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, contentId);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfContentId = _cursor.getColumnIndexOrThrow("contentId");
      final int _cursorIndexOfIsInitiatedByUser = _cursor.getColumnIndexOrThrow("isInitiatedByUser");
      final int _cursorIndexOfDownloadUrl = _cursor.getColumnIndexOrThrow("downloadUrl");
      final int _cursorIndexOfDownloadStatus = _cursor.getColumnIndexOrThrow("downloadStatus");
      final int _cursorIndexOfPercentCompleted = _cursor.getColumnIndexOrThrow("percentCompleted");
      final int _cursorIndexOfDownloadId = _cursor.getColumnIndexOrThrow("downloadId");
      final int _cursorIndexOfNumberOfAttempts = _cursor.getColumnIndexOrThrow("numberOfAttempts");
      final DownloadStatusEntity _result;
      if(_cursor.moveToFirst()) {
        final String _tmpContentId;
        _tmpContentId = _cursor.getString(_cursorIndexOfContentId);
        final boolean _tmpIsInitiatedByUser;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsInitiatedByUser);
        _tmpIsInitiatedByUser = _tmp != 0;
        final String _tmpDownloadUrl;
        _tmpDownloadUrl = _cursor.getString(_cursorIndexOfDownloadUrl);
        final String _tmpDownloadStatus;
        _tmpDownloadStatus = _cursor.getString(_cursorIndexOfDownloadStatus);
        final double _tmpPercentCompleted;
        _tmpPercentCompleted = _cursor.getDouble(_cursorIndexOfPercentCompleted);
        final long _tmpDownloadId;
        _tmpDownloadId = _cursor.getLong(_cursorIndexOfDownloadId);
        final int _tmpNumberOfAttempts;
        _tmpNumberOfAttempts = _cursor.getInt(_cursorIndexOfNumberOfAttempts);
        _result = new DownloadStatusEntity(_tmpContentId,_tmpIsInitiatedByUser,_tmpDownloadUrl,_tmpDownloadStatus,_tmpPercentCompleted,_tmpDownloadId,_tmpNumberOfAttempts);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public DownloadStatusEntity getDownloadStatusByDownloadId(long downloadId) {
    final String _sql = "SELECT * FROM download_status WHERE downloadId=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, downloadId);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfContentId = _cursor.getColumnIndexOrThrow("contentId");
      final int _cursorIndexOfIsInitiatedByUser = _cursor.getColumnIndexOrThrow("isInitiatedByUser");
      final int _cursorIndexOfDownloadUrl = _cursor.getColumnIndexOrThrow("downloadUrl");
      final int _cursorIndexOfDownloadStatus = _cursor.getColumnIndexOrThrow("downloadStatus");
      final int _cursorIndexOfPercentCompleted = _cursor.getColumnIndexOrThrow("percentCompleted");
      final int _cursorIndexOfDownloadId = _cursor.getColumnIndexOrThrow("downloadId");
      final int _cursorIndexOfNumberOfAttempts = _cursor.getColumnIndexOrThrow("numberOfAttempts");
      final DownloadStatusEntity _result;
      if(_cursor.moveToFirst()) {
        final String _tmpContentId;
        _tmpContentId = _cursor.getString(_cursorIndexOfContentId);
        final boolean _tmpIsInitiatedByUser;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsInitiatedByUser);
        _tmpIsInitiatedByUser = _tmp != 0;
        final String _tmpDownloadUrl;
        _tmpDownloadUrl = _cursor.getString(_cursorIndexOfDownloadUrl);
        final String _tmpDownloadStatus;
        _tmpDownloadStatus = _cursor.getString(_cursorIndexOfDownloadStatus);
        final double _tmpPercentCompleted;
        _tmpPercentCompleted = _cursor.getDouble(_cursorIndexOfPercentCompleted);
        final long _tmpDownloadId;
        _tmpDownloadId = _cursor.getLong(_cursorIndexOfDownloadId);
        final int _tmpNumberOfAttempts;
        _tmpNumberOfAttempts = _cursor.getInt(_cursorIndexOfNumberOfAttempts);
        _result = new DownloadStatusEntity(_tmpContentId,_tmpIsInitiatedByUser,_tmpDownloadUrl,_tmpDownloadStatus,_tmpPercentCompleted,_tmpDownloadId,_tmpNumberOfAttempts);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<String> getSuccessfulDownloads() {
    final String _sql = "SELECT contentId FROM download_status WHERE downloadStatus='DOWNLOADED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final List<String> _result = new ArrayList<String>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final String _item;
        _item = _cursor.getString(0);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public void deleteAll(String[] contentIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("DELETE FROM download_status WHERE contentId IN (");
    final int _inputSize = contentIds.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
    int _argIndex = 1;
    for (String _item : contentIds) {
      if (_item == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, _item);
      }
      _argIndex ++;
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }
}
