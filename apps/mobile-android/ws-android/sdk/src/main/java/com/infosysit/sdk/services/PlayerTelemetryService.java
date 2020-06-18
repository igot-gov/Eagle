package com.infosysit.sdk.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.UtilityJava;
import com.infosysit.sdk.persistence.SharedPrefrence;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infosysit.sdk.Constants.baseUrl;


public class PlayerTelemetryService extends IntentService {
    private Context mContext;
    public PlayerTelemetryService() {
        super("PlayerTelemetryService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startMyOwnForeground();
            else
                startForeground(1, new Notification());
            mContext = this;
            String id = intent.getStringExtra(Constants.TelemetryContentID);
            viewerHistory(id);
            viewerCount(id);
        }
    }
    // Continue Learning
    private void viewerHistory(String id){

        ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(baseUrl);
        Call<JsonObject> call = apiInterface.pushContinueLearning(id, UtilityJava.tokenValue);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            Log.d("TelemetryService","Status Code Learn"+String.valueOf(response.code()));
                            Log.d("TelemetrygService","successsfull");
                            SharedPrefrence.setItem(mContext,"ApiCall","1");

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d("TelemetryService","Failure learn");

            }
        });


    }

    // Viewer count
    private void viewerCount(String id){

        ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(baseUrl);

        Call<JsonObject> call = apiInterface.pushViewHistory(id, UtilityJava.tokenValue);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            Log.d("TelemetryService","Status Code Count "+String.valueOf(response.code()));
                            Log.d("TelemetrygService","successsfull");
                            SharedPrefrence.setItem(mContext,"ApiCall","1"+SharedPrefrence.getItem(mContext,"ApiCall",""));
                            Log.d("TestCases3view",SharedPrefrence.getItem(mContext,"ApiCall",""));

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d("TelemetryService","Failure count");

            }
        });



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
