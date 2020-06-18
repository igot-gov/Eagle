package com.infosysit.sdk.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.infosysit.sdk.Constants;
import com.infosysit.sdk.UtilityJava;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.ContentEntity;

import static com.infosysit.sdk.Constants.DOWNLOADED;
import static com.infosysit.sdk.Constants.encryptDecryptContentId;
import static com.infosysit.sdk.Constants.encryptDecryptDownloadId;
//import static com.infosysit.sdk.Constants.encryptDecryptFileName;
import static com.infosysit.sdk.Constants.typeOfEncrptDecrypt;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class EncrpytDecryptService extends IntentService {


    public EncrpytDecryptService() {
        super("EncrpytDecryptService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startMyOwnForeground();
            else
                startForeground(1, new Notification());
            String type = intent.getStringExtra(typeOfEncrptDecrypt);
            long downloadId = intent.getLongExtra(encryptDecryptDownloadId, -1);
            ContentEntity contentEntity = null;
            Log.d("encryptdecrypt","downloadId "+downloadId);
            if (downloadId == -1) {
                String contentId = intent.getStringExtra(encryptDecryptContentId);
                Log.d("encryptdecrypt","downloadId1 "+contentId);
                try {
                    contentEntity = AppDatabase.getDb(this).contentDao().getContentByContentId(contentId);
                } catch (Exception e) {
                    Log.d("Exception","Message: "+e.getMessage());
                }
            } else {
                try {
                    contentEntity = AppDatabase.getDb(this).contentDao().getContentByDownloadId(downloadId);
                } catch (Exception e) {
                    Log.d("Exception","Message: "+e.getMessage());
                }
                Log.d("encryptdecrypt","downloadId2");
            }

            Log.d("encryptdecrypt", "is content entitity null " + (contentEntity == null));

            Log.d("encryptdecrypt", "download id " + type);
            if (type.equalsIgnoreCase("EncryptData")) {
                try {
                    if (contentEntity != null) {
                        Log.d("LexTestingDownload","DownloadId "+downloadId+" Encryption");
                        Log.d("encryptdecrypt","encryption started");
                        EncryptionDecryption.encryptData(
                                UtilityJava.getUniqueNumber(this),
                                Constants.DATA_DIR_PATH,
                                contentEntity.getContentId() + "." + contentEntity.getExtension(),
                                Constants.DATA_DIR_PATH,
                                contentEntity.getContentId() + ".lex",this);
                        Log.d("encryptdecrypt","encryption done");
                        Log.d("LexTestingDownload","DownloadId "+downloadId+" Decryption");
                        long nowInMillis = System.currentTimeMillis();
                        String expiryDate = String.valueOf(nowInMillis + Constants.expiry);
                        contentEntity.setExpiryDate(expiryDate);
                        AppDatabase.getDb(this).contentDao()
                                .updateContent(contentEntity);
                        AppDatabase.getDb(this).downloadStatusDao()
                                .updateDownloadStatus(downloadId, 100, DOWNLOADED);

//                        for (DownloadStatusEntity entity : AppDatabase.getDb(this).downloadStatusDao().getAllDownloads()) {
//                            Log.d("EncrpytDecryptService", "size " + entity);
//                        }
//                    database.parentUpdate(downloadId, "DOWNLOADED");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
//                    EncryptionDecryption.decryptData(fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.infosysit.apps";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

}