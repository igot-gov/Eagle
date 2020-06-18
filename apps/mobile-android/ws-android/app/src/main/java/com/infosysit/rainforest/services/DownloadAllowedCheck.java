package com.infosysit.rainforest.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.UtilityJava;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.services.ApiInterfaceJava;
import com.infosysit.sdk.services.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infosysit.sdk.Constants.baseUrl;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DownloadAllowedCheck extends IntentService {


    public DownloadAllowedCheck() {
        super("DownloadAllowedCheck");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("DownloadCheck","Reached here");
            try{
                ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(baseUrl);
                Call<JsonObject> downloadAllowedJson = apiInterface.getDownloadAllowed(UtilityJava.tokenValue, Constants.userId, "downloadAllowed");
                downloadAllowedJson.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.code()==200 && response.body() != null && response.body().isJsonObject()){
                            try {
                                if(!response.body().getAsJsonObject().get("downloadAllowed").getAsBoolean()){
                                    Log.d("DownloadCheck","JsonObject: "+response.body().getAsJsonObject());
                                    Constants.downloadAllowed = false;
                                    Intent intent = new Intent(DownloadAllowedCheck.this, DeleteDecryptedService.class);
                                    intent.putExtra("folderToDelete", Constants.APP_DIR_PATH);
                                    startService(intent);
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                AppDatabase.getDb(getApplicationContext()).contentDao().deleteAll();
                                                AppDatabase.getDb(getApplicationContext()).downloadStatusDao().deleteAll();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                } else{
                                    Log.d("DownloadCheck","JsonObject: "+response.body());
                                    Constants.downloadAllowed = true;
                                }
                            } catch (Exception e) {
                                Log.d("Exception", "Message: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }catch (Exception ex){
                Log.e("DownloadAllowedCheck","Msg: "+ex.getMessage());
            }

        }
    }
}
