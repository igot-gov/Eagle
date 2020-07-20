package com.infosysit.ledsdk.persistence;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import com.infosysit.ledsdk.persistence.dao.ContentDao;
import com.infosysit.ledsdk.persistence.dao.ContentDao_Impl;
import com.infosysit.ledsdk.persistence.dao.DownloadStatusDao;
import com.infosysit.ledsdk.persistence.dao.DownloadStatusDao_Impl;
import com.infosysit.ledsdk.persistence.dao.RequestedStatusDao;
import com.infosysit.ledsdk.persistence.dao.RequestedStatusDao_Impl;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ContentDao _contentDao;

  private volatile DownloadStatusDao _downloadStatusDao;

  private volatile RequestedStatusDao _requestedStatusDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `content` (`contentId` TEXT NOT NULL, `downloadId` INTEGER NOT NULL, `contentType` TEXT, `requestedDate` TEXT, `modifiedDate` TEXT, `expiryDate` TEXT, `contentMetaJson` TEXT, `children` TEXT, `parentId` TEXT, `extension` TEXT, PRIMARY KEY(`contentId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `download_status` (`contentId` TEXT NOT NULL, `isInitiatedByUser` INTEGER NOT NULL, `downloadUrl` TEXT, `downloadStatus` TEXT, `percentCompleted` REAL NOT NULL, `downloadId` INTEGER NOT NULL, `numberOfAttempts` INTEGER NOT NULL, PRIMARY KEY(`contentId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `requested_status` (`contentId` TEXT NOT NULL, `requestedStatus` TEXT, PRIMARY KEY(`contentId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '31c2289cdcebea9847c3e01b0fad72f0')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `content`");
        _db.execSQL("DROP TABLE IF EXISTS `download_status`");
        _db.execSQL("DROP TABLE IF EXISTS `requested_status`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsContent = new HashMap<String, TableInfo.Column>(10);
        _columnsContent.put("contentId", new TableInfo.Column("contentId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContent.put("downloadId", new TableInfo.Column("downloadId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContent.put("contentType", new TableInfo.Column("contentType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContent.put("requestedDate", new TableInfo.Column("requestedDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContent.put("modifiedDate", new TableInfo.Column("modifiedDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContent.put("expiryDate", new TableInfo.Column("expiryDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContent.put("contentMetaJson", new TableInfo.Column("contentMetaJson", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContent.put("children", new TableInfo.Column("children", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContent.put("parentId", new TableInfo.Column("parentId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContent.put("extension", new TableInfo.Column("extension", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysContent = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesContent = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoContent = new TableInfo("content", _columnsContent, _foreignKeysContent, _indicesContent);
        final TableInfo _existingContent = TableInfo.read(_db, "content");
        if (! _infoContent.equals(_existingContent)) {
          return new RoomOpenHelper.ValidationResult(false, "content(com.infosysit.ledsdk.persistence.entity.ContentEntity).\n"
                  + " Expected:\n" + _infoContent + "\n"
                  + " Found:\n" + _existingContent);
        }
        final HashMap<String, TableInfo.Column> _columnsDownloadStatus = new HashMap<String, TableInfo.Column>(7);
        _columnsDownloadStatus.put("contentId", new TableInfo.Column("contentId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadStatus.put("isInitiatedByUser", new TableInfo.Column("isInitiatedByUser", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadStatus.put("downloadUrl", new TableInfo.Column("downloadUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadStatus.put("downloadStatus", new TableInfo.Column("downloadStatus", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadStatus.put("percentCompleted", new TableInfo.Column("percentCompleted", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadStatus.put("downloadId", new TableInfo.Column("downloadId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownloadStatus.put("numberOfAttempts", new TableInfo.Column("numberOfAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDownloadStatus = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDownloadStatus = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDownloadStatus = new TableInfo("download_status", _columnsDownloadStatus, _foreignKeysDownloadStatus, _indicesDownloadStatus);
        final TableInfo _existingDownloadStatus = TableInfo.read(_db, "download_status");
        if (! _infoDownloadStatus.equals(_existingDownloadStatus)) {
          return new RoomOpenHelper.ValidationResult(false, "download_status(com.infosysit.ledsdk.persistence.entity.DownloadStatusEntity).\n"
                  + " Expected:\n" + _infoDownloadStatus + "\n"
                  + " Found:\n" + _existingDownloadStatus);
        }
        final HashMap<String, TableInfo.Column> _columnsRequestedStatus = new HashMap<String, TableInfo.Column>(2);
        _columnsRequestedStatus.put("contentId", new TableInfo.Column("contentId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRequestedStatus.put("requestedStatus", new TableInfo.Column("requestedStatus", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRequestedStatus = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRequestedStatus = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRequestedStatus = new TableInfo("requested_status", _columnsRequestedStatus, _foreignKeysRequestedStatus, _indicesRequestedStatus);
        final TableInfo _existingRequestedStatus = TableInfo.read(_db, "requested_status");
        if (! _infoRequestedStatus.equals(_existingRequestedStatus)) {
          return new RoomOpenHelper.ValidationResult(false, "requested_status(com.infosysit.ledsdk.persistence.entity.RequestedStatusEntity).\n"
                  + " Expected:\n" + _infoRequestedStatus + "\n"
                  + " Found:\n" + _existingRequestedStatus);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "31c2289cdcebea9847c3e01b0fad72f0", "fa4bc0fa31abc0a43645d948e522ce59");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "content","download_status","requested_status");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `content`");
      _db.execSQL("DELETE FROM `download_status`");
      _db.execSQL("DELETE FROM `requested_status`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public ContentDao contentDao() {
    if (_contentDao != null) {
      return _contentDao;
    } else {
      synchronized(this) {
        if(_contentDao == null) {
          _contentDao = new ContentDao_Impl(this);
        }
        return _contentDao;
      }
    }
  }

  @Override
  public DownloadStatusDao downloadStatusDao() {
    if (_downloadStatusDao != null) {
      return _downloadStatusDao;
    } else {
      synchronized(this) {
        if(_downloadStatusDao == null) {
          _downloadStatusDao = new DownloadStatusDao_Impl(this);
        }
        return _downloadStatusDao;
      }
    }
  }

  @Override
  public RequestedStatusDao requestedStatusDao() {
    if (_requestedStatusDao != null) {
      return _requestedStatusDao;
    } else {
      synchronized(this) {
        if(_requestedStatusDao == null) {
          _requestedStatusDao = new RequestedStatusDao_Impl(this);
        }
        return _requestedStatusDao;
      }
    }
  }
}
