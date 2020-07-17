package com.infosysit.rainforest.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.TelemeteryValidation;
import com.infosysit.sdk.UtilityJava;
import com.infosysit.sdk.persistence.SharedPrefrence;
import com.infosysit.sdk.services.ApiInterfaceJava;
import com.infosysit.sdk.services.RetrofitSingleton;
import com.infosysit.sdk.services.TelemetryServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infosysit.sdk.Constants.baseUrl;
import static com.infosysit.sdk.Constants.quizSubmitTelemetry;


public class TelemetryQuizSubmission extends IntentService {
    String typeData;
    public TelemetryQuizSubmission() {
        super("TelemetryQuizSubmission");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(baseUrl);
            String resourceplayed = SharedPrefrence.getItem(this, quizSubmitTelemetry,"undefined");
            if(!resourceplayed.equalsIgnoreCase("undefined") && !resourceplayed.equals("")){
                JsonParser parser = new JsonParser();
                JsonArray submittedQuizes = parser.parse(resourceplayed).getAsJsonArray();
                for (final JsonElement submittedQuiz : submittedQuizes) {
                    Log.d("TelemetryQuizService","data: "+submittedQuiz);
                    if (submittedQuiz.getAsJsonObject().get("request").getAsJsonObject().has("type")) {
                        typeData = submittedQuiz.getAsJsonObject().get("request").getAsJsonObject().get("type").getAsString();
                        Log.d("TelemetryQuizService","typeData inside" );
                    }
                    Log.d("TelemetryQuizService","typeData"+typeData);

                    submittedQuiz.getAsJsonObject().get("request").getAsJsonObject().remove("type");
                    JsonObject validatedQuizJson = submittedQuiz.getAsJsonObject();
                    TelemeteryValidation telemeteryValidation = new TelemeteryValidation();

                    if(!telemeteryValidation.quizSubmissionValidation(validatedQuizJson)){continue;}
                    Call<JsonObject> call = apiInterface.pushSubmitQuiz((JsonObject) submittedQuiz, UtilityJava.tokenValue);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            Log.d("TelemetryQuiz", "onResponse: ");
                            TelemetryServices services = new TelemetryServices();
                            JsonObject cpActivity =  new JsonObject();
                            try {
                                if(response.body() != null){
//                                    Log.d("TelemetryQuizService","JSON: "+response.body());
                                    JsonObject telemetryData  = new JsonObject();
                                    JsonObject quizData = new JsonObject();
                                    telemetryData.add("data",((JsonObject) submittedQuiz).get("request").getAsJsonObject());
                                    Log.d("CheckTeleValue","Value: "+String.valueOf(response.body().get("result").getAsJsonObject().get("response").getAsJsonObject().get("result").getAsInt() >= response.body().get("result").getAsJsonObject().get("response").getAsJsonObject().get("passPercent").getAsInt()));
                                    if(response.body().get("result").getAsJsonObject().get("response").getAsJsonObject().get("result").getAsInt() >= response.body().get("result").getAsJsonObject().get("response").getAsJsonObject().get("passPercent").getAsInt()){
//                                        telemetryData.get("data").getAsJsonObject().addProperty("isCompleted",true);
                                       // telemetryData.add("data","");
                                        quizData.addProperty("isCompleted",true);
                                    }
                                    else{
                                        quizData.addProperty("isCompleted",false);

                                    }
                                    telemetryData.addProperty("type",typeData);
                                    telemetryData.addProperty("plugin","quiz");
                                    quizData.addProperty("identifier",((JsonObject) submittedQuiz).get("request").getAsJsonObject().get("identifier").getAsString());
                                    quizData.addProperty("mimeType","application/quiz");

                                    quizData.add("details",response.body().get("result").getAsJsonObject().get("response"));
                                    telemetryData.add("data",quizData);

//                                    Log.d("TelemetryQuizService", "telemetry data in: "+telemetryData);
                                      cpActivity =  services.telemetryPlayer(getApplicationContext(),telemetryData,"offline");
//                                    Log.d("TelemetryQuizService", "onResponse: "+cpActivity);
                                Log.d("TelemetryQuizService", "identifier: "+((JsonObject) submittedQuiz).get("request").getAsJsonObject().get("identifier").getAsString());
                                cpActivity.get("events").getAsJsonArray().get(0).getAsJsonObject().addProperty("resid",((JsonObject) submittedQuiz).get("request").getAsJsonObject().get("identifier").getAsString());
                                    cpActivity.get("events").getAsJsonArray().get(0).getAsJsonObject().addProperty("restype","application/quiz");
//                                    Log.d("TelemetryQuizService", "onResponse: resid "+cpActivity.get("events").getAsJsonArray().get(0).getAsJsonObject().get("resid"));
                                    Log.d("TelemetryQuizService", "onResponse: object "+cpActivity);
                                    Call<JsonObject> callApi = apiInterface.pushViewTelemetryData(cpActivity
                                            , UtilityJava.tokenValue);
                                    callApi.enqueue(new Callback<JsonObject>() {
                                        @Override
                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                            Log.d("TelemetryQuizService", "onResponse:Telemetry call ");

                                        }

                                        @Override
                                        public void onFailure(Call<JsonObject> call, Throwable t) {
//                            Log.d("TelemetryService","Failure");

                                        }
                                    });

                                }
                            }catch (Exception e) {
                                Log.d("TelemetryQuizService","Exception: "+e.getMessage());
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d("TelemetryQuizService","Failure");

                        }
                    });
                }
                SharedPrefrence.setItem(this, Constants.quizSubmitTelemetry ,"");

            }
        }
    }


}
