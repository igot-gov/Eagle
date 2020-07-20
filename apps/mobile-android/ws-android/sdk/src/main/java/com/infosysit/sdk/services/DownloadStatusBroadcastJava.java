package com.infosysit.sdk.services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;

import static com.infosysit.sdk.Constants.FAILED;
import static com.infosysit.sdk.Constants.encryptDecryptDownloadId;
import static com.infosysit.sdk.Constants.typeOfEncrptDecrypt;
//import static com.infosysit.sdk.services.DownloadContentJava.mappingIdentifierDownloadId;

/**
 * Created by akansha.goyal on 3/19/2018.
 */

public class DownloadStatusBroadcastJava extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SQLiteHelperJava","Reached here1");
        String action = intent.getAction();
        Log.d("SQLiteHelperJava","Reached here");

        //Todo 45

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            Log.d("SQLiteHelperJava", String.valueOf(downloadId));
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            DownloadManager mDownloadManager =
                    (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor c = mDownloadManager.query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if(DownloadManager.ERROR_UNHANDLED_HTTP_CODE == c.getInt(columnIndex)){
                    Log.d("SQLiteHelperJava","downloadId "+downloadId+" Reached here UNHANDLED ERROR");
                }

                if (DownloadManager.ERROR_UNKNOWN == c.getInt(columnIndex)){
                    Log.d("SQLiteHelperJava","downloadId "+downloadId+"Reached here UNKNOWN ERROR");
                }
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)){
                    Log.d("SQLiteHelperJava","STATUS_SUCCESSFUL "+downloadId);
                    DownloadStatusEntity downloadStatusEntity = AppDatabase.getDb(context).downloadStatusDao().getDownloadStatusByDownloadId(downloadId);
                    if(downloadStatusEntity != null && downloadStatusEntity.getDownloadUrl() != null){
                        Log.d("LexTestingDownload","DownloadId "+downloadId+" Successfull");
                    }

//                    if (downloadStatusEntity != null && downloadStatusEntity.getDownloadUrl() != null && downloadStatusEntity.getDownloadUrl().contains(openRapUrl)) {
//                        Intent openRapService = new Intent(context, DownloadOpenRapService.class);
//                        openRapService.putExtra(EXTRA_DOWNLOAD_RES_ID, downloadStatusEntity.getContentId());
//                        context.startService(openRapService);
//                    }

                    Intent encryptDecyptService = new Intent(context, EncrpytDecryptService.class);
                    encryptDecyptService.putExtra(typeOfEncrptDecrypt,"EncryptData");
                    encryptDecyptService.putExtra(encryptDecryptDownloadId, downloadId);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(encryptDecyptService);
                    } else {
                        context.startService(encryptDecyptService);
                    }

//                    context.startService(encryptDecyptService);
//                    EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
//                    try {
//                        encryptionDecryption.encryptData(contentRetreive.getId()+"."+contentRetreive.getExtension());
//                        contentToUpdate.setDownloadId(String.valueOf(downloadId));
//                        contentToUpdate.setExpDate(String.valueOf(System.currentTimeMillis()/1000 + 3600*24*90));
//                        contentToUpdate.setPerCompleted("100");
//                        contentToUpdate.setStatus("DOWNLOADED");
//                        database.parentUpdate(String.valueOf(downloadId) , "DOWNLOADED");
//                        database.updateByDownloadId(contentToUpdate);
//                    } catch (Exception e) {
//                        Log.e("Exception",e.getMessage());
//                        e.printStackTrace();
//                    }


//                    String downloadedPackageUriString =
//                            c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
//                    Log.d("DownloadStatusBroadcast",downloadedPackageUriString);
//                    Log.d("DownloadStatusBroadcast","Dowload Successfull");

//                    //notify your app that download was completed
//                    //with local broadcast receiver
//                    Intent localReceiver = new Intent();
//                    localReceiver.putExtra("ID", id);
//                    LocalBroadcastManager
//                            .getInstance(context)
//                            .sendBroadcast(localReceiver);
//                DownloadManager.STATUS_RUNNING

                }else if (DownloadManager.STATUS_FAILED == c.getInt(columnIndex)){
//                    Log.d("SQLiteHelperJava","STATUS_FAILED "+downloadId);

                    Log.d("SQLiteHelperJava", "Reason: " + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));
//                    Log.d("DownloadStatusBroadcast","Download Failed");
                    Log.d("SQLiteHelperJava","downloadId "+downloadId+" STATUS_FAILED "+downloadId);
                    try {
                        AppDatabase.getDb(context).downloadStatusDao().updateDownloadStatus(downloadId, 0, FAILED);
                    } catch (Exception e) {
                        Log.d("Exception","Message: "+e.getMessage());
                    }
                }
                else if(DownloadManager.STATUS_RUNNING == c.getInt(columnIndex)){
                    Log.d("SQLiteHelperJava","downloadId "+downloadId+" STATUS_RUNNING "+downloadId);
                }
                else if(DownloadManager.STATUS_PAUSED == c.getInt(columnIndex)){
                    Log.d("SQLiteHelperJava","downloadId "+downloadId+" STATUS_PAUSED "+downloadId);
                }
                else if(DownloadManager.PAUSED_QUEUED_FOR_WIFI == c.getInt(columnIndex)){
                    Log.d("SQLiteHelperJava","downloadId "+downloadId+" STATUS_PAUSED_WIFI "+downloadId);
                }
                else if(DownloadManager.PAUSED_WAITING_FOR_NETWORK == c.getInt(columnIndex)){
                    Log.d("SQLiteHelperJava","downloadId "+downloadId+" STATUS_PAUSED_NETWORK "+downloadId);
                }
                else if(DownloadManager.STATUS_PENDING == c.getInt(columnIndex)){
                    Log.d("SQLiteHelperJava","downloadId "+downloadId+" STATUS_PENDING "+downloadId);
                }
//                    //if failed you can make a retry or whatever
//                    //I just delete my id from sqllite
//                }
            }
        }
        else{
            Log.d("SQLiteHelperJava","else part");
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            Log.d("SQLiteHelperJava", String.valueOf(downloadId));
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            DownloadManager mDownloadManager =
                    (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor c = mDownloadManager.query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if(DownloadManager.ERROR_UNHANDLED_HTTP_CODE == c.getInt(columnIndex)){
//                    Log.d("DOWNLOAD_ERROR","Reached here UNHANDLED ERROR");
                }
            }
        }
    }
}