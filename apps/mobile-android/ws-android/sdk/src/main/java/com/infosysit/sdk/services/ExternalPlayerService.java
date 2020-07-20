package com.infosysit.sdk.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.UtilityJava;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infosysit.sdk.Constants.baseUrl;


public class ExternalPlayerService extends IntentService {
    private Context mContext;

    public ExternalPlayerService() {
        super("ExternalPlayerService");
        mContext =this;

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String data = intent.getStringExtra(Constants.playerData);
            try {

                ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(baseUrl);
                JsonParser parser = new JsonParser();
                Call<JsonObject> call = apiInterface.pushViewTelemetryData(parser.parse(data).getAsJsonObject()
                        , UtilityJava.tokenValue);
                call.enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d("TelemetryExternalPlayer","Status Code "+String.valueOf(response.code()));
                        Log.d("TelemetryExternalPlayer","successsfull");

//                            SharedPrefrence.setItem(mContext,telemetryTokenKey ,"undefined");

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d("TelemetryExternalPlayer","Failure");

                    }
                });






            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }


}
