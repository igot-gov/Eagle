package com.infosysit.rainforest.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import static com.infosysit.sdk.Constants.continueLearningTelemetry;
import static com.infosysit.sdk.Constants.continueLearningTelemetryJson;

public class TelemetryLearningService extends IntentService {

    public TelemetryLearningService() {
        super("TelemetryLearningService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(baseUrl);
            //Continue Learning
            String continueLearning = SharedPrefrence.getItem(this, continueLearningTelemetryJson,"undefined");

            if( !continueLearning.equalsIgnoreCase("undefined")) {
                apiCallContinueLearning(apiInterface, continueLearning);
            }
            String resourceplayed = SharedPrefrence.getItem(this, continueLearningTelemetry,"undefined");
            String[] playedContentIds = new String[]{};
            if(!resourceplayed.equalsIgnoreCase("undefined")){
                playedContentIds = resourceplayed.split("\\,");
            }
            for (String contentId : playedContentIds){
                Call<JsonObject> call = apiInterface.pushContinueLearning(contentId, UtilityJava.tokenValue);
                call.enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                            Log.d("TelemetryService","Status Code "+String.valueOf(response.code()));
                            Log.d("TelemetryService","successsfull");
                        SharedPrefrence.setItem(getApplicationContext(), continueLearningTelemetry,"");

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d("TelemetryService","Failure");

                    }
                });

            }
        }








    }

    private void apiCallContinueLearning(ApiInterfaceJava apiInterface, String continueLearning) {
        try {

            Log.d("continuelearning", continueLearning);
            JsonParser parser = new JsonParser();
//        JsonElement continueLearningElement = parser.parse(continueLearning);
//        JsonArray continueLearningJsonArray = continueLearningElement.getAsJsonArray();
            final JsonArray continueLearningJsonArray = parser.parse(continueLearning).getAsJsonArray();
            for (JsonElement element : continueLearningJsonArray) {
                final JsonObject jsonObject = element.getAsJsonObject();
                TelemeteryValidation telemeteryValidation = new TelemeteryValidation();
                if(!telemeteryValidation.continueLearningValidation(jsonObject)){continue;}
                Call<JsonObject> call = apiInterface.pushCourseDetails(jsonObject, UtilityJava.tokenValue);
                call.enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d("TelemetryService", " apiCallContinueLearning Status Code " + String.valueOf(response.code()));
//                            Log.d("TelemetrygService","successsfull");
                        if(response.code()/100==2){
                                continueLearningJsonArray.remove(jsonObject);
                                SharedPrefrence.setItem(getApplicationContext(),continueLearningTelemetryJson,continueLearningJsonArray.toString());
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                            Log.d("TelemetryService","Failure");

                    }
                });


            }

        }
        catch (Exception e){
            Log.d(getClass().getSimpleName(),"Testcase3"+e.getMessage());
        }

    }


}
