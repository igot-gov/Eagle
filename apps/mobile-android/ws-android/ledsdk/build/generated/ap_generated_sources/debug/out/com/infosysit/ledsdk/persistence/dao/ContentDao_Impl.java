package com.infosysit.ledsdk.persistence.dao;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.infosysit.ledsdk.persistence.entity.ContentEntity;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ContentDao_Impl implements ContentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ContentEntity> __insertionAdapterOfContentEntity;

  private final EntityDeletionOrUpdateAdapter<ContentEntity> __updateAdapterOfContentEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public ContentDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfContentEntity = new EntityInsertionAdapter<ContentEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `content` (`contentId`,`downloadId`,`contentType`,`requestedDate`,`modifiedDate`,`expiryDate`,`contentMetaJson`,`children`,`parentId`,`extension`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ContentEntity value) {
        if (value.getContentId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getContentId());
        }
        stmt.bindLong(2, value.getDownloadId());
        if (value.getContentType() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getContentType());
        }
        if (value.getRequestedDate() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getRequestedDate());
        }
        if (value.getModifiedDate() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getModifiedDate());
        }
        if (value.getExpiryDate() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getExpiryDate());
        }
        if (value.getContentMetaJson() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getContentMetaJson());
        }
        if (value.getChildren() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getChildren());
        }
        if (value.getParentId() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getParentId());
        }
        if (value.getExtension() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getExtension());
        }
      }
    };
    this.__updateAdapterOfContentEntity = new EntityDeletionOrUpdateAdapter<ContentEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `content` SET `contentId` = ?,`downloadId` = ?,`contentType` = ?,`requestedDate` = ?,`modifiedDate` = ?,`expiryDate` = ?,`contentMetaJson` = ?,`children` = ?,`parentId` = ?,`extension` = ? WHERE `contentId` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ContentEntity value) {
        if (value.getContentId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getContentId());
        }
        stmt.bindLong(2, value.getDownloadId());
        if (value.getContentType() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getContentType());
        }
        if (value.getRequestedDate() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getRequestedDate());
        }
        if (value.getModifiedDate() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getModifiedDate());
        }
        if (value.getExpiryDate() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getExpiryDate());
        }
        if (value.getContentMetaJson() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getContentMetaJson());
        }
        if (value.getChildren() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getChildren());
        }
        if (value.getParentId() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getParentId());
        }
        if (value.getExtension() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getExtension());
        }
        if (value.getContentId() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getContentId());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM content";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM content WHERE contentId = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(final ContentEntity... contentEntities) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfContentEntity.insert(contentEntities);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateContent(final ContentEntity contentEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfContentEntity.handle(contentEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
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
  public void delete(final String contentId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
    int _argIndex = 1;
    if (contentId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, contentId);
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDelete.release(_stmt);
    }
  }

  @Override
  public void deleteAllContent() {
    __db.assertNotSuspendingTransaction();
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
  public List<ContentEntity> getAllDownloadedContents(final String status) {
    final String _sql = "SELECT content.* FROM content INNER JOIN download_status ON content.contentId = download_status.contentId WHERE download_status.downloadStatus =?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfContentId = CursorUtil.getColumnIndexOrThrow(_cursor, "contentId");
      final int _cursorIndexOfDownloadId = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadId");
      final int _cursorIndexOfContentType = CursorUtil.getColumnIndexOrThrow(_cursor, "contentType");
      final int _cursorIndexOfRequestedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "requestedDate");
      final int _cursorIndexOfModifiedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedDate");
      final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
      final int _cursorIndexOfContentMetaJson = CursorUtil.getColumnIndexOrThrow(_cursor, "contentMetaJson");
      final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
      final int _cursorIndexOfParentId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentId");
      final int _cursorIndexOfExtension = CursorUtil.getColumnIndexOrThrow(_cursor, "extension");
      final List<ContentEntity> _result = new ArrayList<ContentEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ContentEntity _item;
        final String _tmpContentId;
        _tmpContentId = _cursor.getString(_cursorIndexOfContentId);
        final long _tmpDownloadId;
        _tmpDownloadId = _cursor.getLong(_cursorIndexOfDownloadId);
        final String _tmpContentType;
        _tmpContentType = _cursor.getString(_cursorIndexOfContentType);
        final String _tmpRequestedDate;
        _tmpRequestedDate = _cursor.getString(_cursorIndexOfRequestedDate);
        final String _tmpModifiedDate;
        _tmpModifiedDate = _cursor.getString(_cursorIndexOfModifiedDate);
        final String _tmpExpiryDate;
        _tmpExpiryDate = _cursor.getString(_cursorIndexOfExpiryDate);
        final String _tmpContentMetaJson;
        _tmpContentMetaJson = _cursor.getString(_cursorIndexOfContentMetaJson);
        final String _tmpChildren;
        _tmpChildren = _cursor.getString(_cursorIndexOfChildren);
        final String _tmpParentId;
        _tmpParentId = _cursor.getString(_cursorIndexOfParentId);
        final String _tmpExtension;
        _tmpExtension = _cursor.getString(_cursorIndexOfExtension);
        _item = new ContentEntity(_tmpContentId,_tmpDownloadId,_tmpContentType,_tmpRequestedDate,_tmpModifiedDate,_tmpExpiryDate,_tmpContentMetaJson,_tmpChildren,_tmpParentId,_tmpExtension);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public ContentEntity getContentByContentId(final String contentId) {
    final String _sql = "SELECT * FROM content WHERE contentId LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (contentId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, contentId);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfContentId = CursorUtil.getColumnIndexOrThrow(_cursor, "contentId");
      final int _cursorIndexOfDownloadId = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadId");
      final int _cursorIndexOfContentType = CursorUtil.getColumnIndexOrThrow(_cursor, "contentType");
      final int _cursorIndexOfRequestedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "requestedDate");
      final int _cursorIndexOfModifiedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedDate");
      final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
      final int _cursorIndexOfContentMetaJson = CursorUtil.getColumnIndexOrThrow(_cursor, "contentMetaJson");
      final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
      final int _cursorIndexOfParentId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentId");
      final int _cursorIndexOfExtension = CursorUtil.getColumnIndexOrThrow(_cursor, "extension");
      final ContentEntity _result;
      if(_cursor.moveToFirst()) {
        final String _tmpContentId;
        _tmpContentId = _cursor.getString(_cursorIndexOfContentId);
        final long _tmpDownloadId;
        _tmpDownloadId = _cursor.getLong(_cursorIndexOfDownloadId);
        final String _tmpContentType;
        _tmpContentType = _cursor.getString(_cursorIndexOfContentType);
        final String _tmpRequestedDate;
        _tmpRequestedDate = _cursor.getString(_cursorIndexOfRequestedDate);
        final String _tmpModifiedDate;
        _tmpModifiedDate = _cursor.getString(_cursorIndexOfModifiedDate);
        final String _tmpExpiryDate;
        _tmpExpiryDate = _cursor.getString(_cursorIndexOfExpiryDate);
        final String _tmpContentMetaJson;
        _tmpContentMetaJson = _cursor.getString(_cursorIndexOfContentMetaJson);
        final String _tmpChildren;
        _tmpChildren = _cursor.getString(_cursorIndexOfChildren);
        final String _tmpParentId;
        _tmpParentId = _cursor.getString(_cursorIndexOfParentId);
        final String _tmpExtension;
        _tmpExtension = _cursor.getString(_cursorIndexOfExtension);
        _result = new ContentEntity(_tmpContentId,_tmpDownloadId,_tmpContentType,_tmpRequestedDate,_tmpModifiedDate,_tmpExpiryDate,_tmpContentMetaJson,_tmpChildren,_tmpParentId,_tmpExtension);
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
  public ContentEntity getContentByDownloadId(final long downloadId) {
    final String _sql = "SELECT * FROM content WHERE downloadId LIKE ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, downloadId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfContentId = CursorUtil.getColumnIndexOrThrow(_cursor, "contentId");
      final int _cursorIndexOfDownloadId = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadId");
      final int _cursorIndexOfContentType = CursorUtil.getColumnIndexOrThrow(_cursor, "contentType");
      final int _cursorIndexOfRequestedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "requestedDate");
      final int _cursorIndexOfModifiedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedDate");
      final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
      final int _cursorIndexOfContentMetaJson = CursorUtil.getColumnIndexOrThrow(_cursor, "contentMetaJson");
      final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
      final int _cursorIndexOfParentId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentId");
      final int _cursorIndexOfExtension = CursorUtil.getColumnIndexOrThrow(_cursor, "extension");
      final ContentEntity _result;
      if(_cursor.moveToFirst()) {
        final String _tmpContentId;
        _tmpContentId = _cursor.getString(_cursorIndexOfContentId);
        final long _tmpDownloadId;
        _tmpDownloadId = _cursor.getLong(_cursorIndexOfDownloadId);
        final String _tmpContentType;
        _tmpContentType = _cursor.getString(_cursorIndexOfContentType);
        final String _tmpRequestedDate;
        _tmpRequestedDate = _cursor.getString(_cursorIndexOfRequestedDate);
        final String _tmpModifiedDate;
        _tmpModifiedDate = _cursor.getString(_cursorIndexOfModifiedDate);
        final String _tmpExpiryDate;
        _tmpExpiryDate = _cursor.getString(_cursorIndexOfExpiryDate);
        final String _tmpContentMetaJson;
        _tmpContentMetaJson = _cursor.getString(_cursorIndexOfContentMetaJson);
        final String _tmpChildren;
        _tmpChildren = _cursor.getString(_cursorIndexOfChildren);
        final String _tmpParentId;
        _tmpParentId = _cursor.getString(_cursorIndexOfParentId);
        final String _tmpExtension;
        _tmpExtension = _cursor.getString(_cursorIndexOfExtension);
        _result = new ContentEntity(_tmpContentId,_tmpDownloadId,_tmpContentType,_tmpRequestedDate,_tmpModifiedDate,_tmpExpiryDate,_tmpContentMetaJson,_tmpChildren,_tmpParentId,_tmpExtension);
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
  public List<ContentEntity> getContentByType(final String contentType) {
    final String _sql = "SELECT * FROM content WHERE contentType LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (contentType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, contentType);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfContentId = CursorUtil.getColumnIndexOrThrow(_cursor, "contentId");
      final int _cursorIndexOfDownloadId = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadId");
      final int _cursorIndexOfContentType = CursorUtil.getColumnIndexOrThrow(_cursor, "contentType");
      final int _cursorIndexOfRequestedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "requestedDate");
      final int _cursorIndexOfModifiedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedDate");
      final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
      final int _cursorIndexOfContentMetaJson = CursorUtil.getColumnIndexOrThrow(_cursor, "contentMetaJson");
      final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
      final int _cursorIndexOfParentId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentId");
      final int _cursorIndexOfExtension = CursorUtil.getColumnIndexOrThrow(_cursor, "extension");
      final List<ContentEntity> _result = new ArrayList<ContentEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ContentEntity _item;
        final String _tmpContentId;
        _tmpContentId = _cursor.getString(_cursorIndexOfContentId);
        final long _tmpDownloadId;
        _tmpDownloadId = _cursor.getLong(_cursorIndexOfDownloadId);
        final String _tmpContentType;
        _tmpContentType = _cursor.getString(_cursorIndexOfContentType);
        final String _tmpRequestedDate;
        _tmpRequestedDate = _cursor.getString(_cursorIndexOfRequestedDate);
        final String _tmpModifiedDate;
        _tmpModifiedDate = _cursor.getString(_cursorIndexOfModifiedDate);
        final String _tmpExpiryDate;
        _tmpExpiryDate = _cursor.getString(_cursorIndexOfExpiryDate);
        final String _tmpContentMetaJson;
        _tmpContentMetaJson = _cursor.getString(_cursorIndexOfContentMetaJson);
        final String _tmpChildren;
        _tmpChildren = _cursor.getString(_cursorIndexOfChildren);
        final String _tmpParentId;
        _tmpParentId = _cursor.getString(_cursorIndexOfParentId);
        final String _tmpExtension;
        _tmpExtension = _cursor.getString(_cursorIndexOfExtension);
        _item = new ContentEntity(_tmpContentId,_tmpDownloadId,_tmpContentType,_tmpRequestedDate,_tmpModifiedDate,_tmpExpiryDate,_tmpContentMetaJson,_tmpChildren,_tmpParentId,_tmpExtension);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ContentEntity> getContentByTypeInitiatedByUser(final String contentType) {
    final String _sql = "SELECT content.* FROM content INNER JOIN download_status ON content.contentId = download_status.contentId WHERE content.contentType=? AND download_status.isInitiatedByUser=1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (contentType == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, contentType);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfContentId = CursorUtil.getColumnIndexOrThrow(_cursor, "contentId");
      final int _cursorIndexOfDownloadId = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadId");
      final int _cursorIndexOfContentType = CursorUtil.getColumnIndexOrThrow(_cursor, "contentType");
      final int _cursorIndexOfRequestedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "requestedDate");
      final int _cursorIndexOfModifiedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "modifiedDate");
      final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
      final int _cursorIndexOfContentMetaJson = CursorUtil.getColumnIndexOrThrow(_cursor, "contentMetaJson");
      final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
      final int _cursorIndexOfParentId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentId");
      final int _cursorIndexOfExtension = CursorUtil.getColumnIndexOrThrow(_cursor, "extension");
      final List<ContentEntity> _result = new ArrayList<ContentEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ContentEntity _item;
        final String _tmpContentId;
        _tmpContentId = _cursor.getString(_cursorIndexOfContentId);
        final long _tmpDownloadId;
        _tmpDownloadId = _cursor.getLong(_cursorIndexOfDownloadId);
        final String _tmpContentType;
        _tmpContentType = _cursor.getString(_cursorIndexOfContentType);
        final String _tmpRequestedDate;
        _tmpRequestedDate = _cursor.getString(_cursorIndexOfRequestedDate);
        final String _tmpModifiedDate;
        _tmpModifiedDate = _cursor.getString(_cursorIndexOfModifiedDate);
        final String _tmpExpiryDate;
        _tmpExpiryDate = _cursor.getString(_cursorIndexOfExpiryDate);
        final String _tmpContentMetaJson;
        _tmpContentMetaJson = _cursor.getString(_cursorIndexOfContentMetaJson);
        final String _tmpChildren;
        _tmpChildren = _cursor.getString(_cursorIndexOfChildren);
        final String _tmpParentId;
        _tmpParentId = _cursor.getString(_cursorIndexOfParentId);
        final String _tmpExtension;
        _tmpExtension = _cursor.getString(_cursorIndexOfExtension);
        _item = new ContentEntity(_tmpContentId,_tmpDownloadId,_tmpContentType,_tmpRequestedDate,_tmpModifiedDate,_tmpExpiryDate,_tmpContentMetaJson,_tmpChildren,_tmpParentId,_tmpExtension);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Long> getDownloadIdsForContentIds(final String[] contentIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT downloadId FROM content WHERE contentId IN (");
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
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final List<Long> _result = new ArrayList<Long>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Long _item_1;
        if (_cursor.isNull(0)) {
          _item_1 = null;
        } else {
          _item_1 = _cursor.getLong(0);
        }
        _result.add(_item_1);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public void deleteAll(final String[] contentId) {
    __db.assertNotSuspendingTransaction();
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("DELETE FROM content WHERE contentId IN (");
    final int _inputSize = contentId.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
    int _argIndex = 1;
    for (String _item : contentId) {
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
