package com.infosysit.rainforest.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.sdk.TelemeteryValidation;
import com.infosysit.sdk.UtilityJava;
import com.infosysit.sdk.persistence.SharedPrefrence;
import com.infosysit.sdk.services.ApiInterfaceJava;
import com.infosysit.sdk.services.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infosysit.sdk.Constants.baseUrl;
import static com.infosysit.sdk.Constants.openRapDownload;
import static com.infosysit.sdk.Constants.telemetryTokenKey;


public class TelemetryPlayerService extends IntentService {

    Context mContext;
    ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(baseUrl);

    public TelemetryPlayerService() {
        super("TelemetryPlayerService");
        mContext = this;
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                JsonParser parser = new JsonParser();
                String resourcePlayed = SharedPrefrence.getItem(this, telemetryTokenKey,"undefined");
                String downloadedContent = SharedPrefrence.getItem(this,openRapDownload,"undefined");;
                Log.d("TelemetryStringOPenrap","TestCases3 TelemetryPlayer 1"+resourcePlayed);
                Log.d("TelemetryStringOPenrap","TestCases3 TelemetryPlayer 1"+downloadedContent);
                if(!resourcePlayed.equalsIgnoreCase("undefined") && !resourcePlayed.equals("")){
                    TelemeteryValidation telemeteryValidation = new TelemeteryValidation();
                    JsonObject validatedResource = telemeteryValidation.structureValidation(parser.parse(resourcePlayed).getAsJsonObject(),"cp_activity");
                    sendData(validatedResource);
                }
                if(!resourcePlayed.equalsIgnoreCase("undefined") && !downloadedContent.equals("")){
                    JsonObject openRapDownload = parser.parse(downloadedContent).getAsJsonObject();
                    try{
                        Log.d("TelemetryStringOPenrap","Reached here: "+openRapDownload);
                    }
                    catch(Exception ex){
                        Log.d("TelemetryStringOPenrap","Reached here: "+ex.getMessage());
                    }
                    sendData(openRapDownload);
                }
                //Log.d(getClass().getSimpleName(),"TestCases3 TelemetryPlayer 2"+SharedPrefrence.getItem(this, telemetryTokenKey,"undefined"));




        } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendData(JsonObject dataToSend){
        Call<JsonObject> call = apiInterface.pushViewTelemetryData(dataToSend
                , UtilityJava.tokenValue);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("TelemetryStringOPenrap","Status Code "+String.valueOf(response.code()));
                Log.d("TelemetryStringOPenrap","successsfull");
                SharedPrefrence.setItem(mContext,telemetryTokenKey ,"");
                SharedPrefrence.setItem(mContext,openRapDownload,"");

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("TelemetryStringOPenrap","Failure: "+t.getMessage());

            }
        });
    }

}
