package com.infosysit.sdk.services;

import android.util.Log;

import com.google.gson.JsonObject;
import com.infosysit.sdk.UtilityJava;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jithilprakash.pj on 11/21/2018.
 */

public class TelemeteryApiCall {
    public static boolean status=true;
    public static JsonObject contentJson;

    public static boolean playerService(JsonObject telemetryData,String url){
        ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(url);
        Call<JsonObject> call = apiInterface.pushViewTelemetryData(telemetryData, UtilityJava.tokenValue);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                status = true;
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                status = false;
            }


        });
        return status;
    }

    public JsonObject getHierarchy(String identifier,String mDownloadMode){

//        getCourseCall(identifier,mDownloadMode).enqueue(new Callback<CourseJava>() {
//            @Override
//            public void onResponse(Call<CourseJava> call, Response<CourseJava> response) {
//                if (response != null) {
//                    try {
//                        Log.d("DownloadContent","code "+response.code());
//                        Log.d("DownloadContent","value "+response.body().toString());
//                        Log.d("DownloadContent","value "+String.valueOf(response.body().getResult() == null));
//                        Log.d("DownloadContent", "onResponse: "+response.body().getResult().getAsJsonObject("content"));
//                        setContentJson(response.body().getResult().getAsJsonObject("content"));
//                    } catch (Exception ex) {
//                        Log.d("DownloadContent",ex.getMessage());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CourseJava> call, Throwable t) {
//                Log.d("DownloadContent", "Failure");
//            }
//        });
//        Log.d("TelemetryApiCall", "getHierarchy: "+contentJson);
        return contentJson;
    }

    public void setContentJson(JsonObject json){
        Log.d("DownloadContent", "setContentJson: "+json);
        contentJson = json;

    }


//    public Call<JsonObject> getCourseCall(String identifier,String mDownloadMode) {
//        String apiUrl = mDownloadMode.equals("OPENRAP") ? openRapUrl : baseUrl;
//        ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(apiUrl);
//        Log.d("DownloadContent","Hierarchy fetched");
//        return mDownloadMode.equals("OPENRAP") ? apiInterface.getCourseHierarchyOpenRap() : apiInterface.getCourseHierarchy(identifier, UtilityJava.tokenValue);
//    }


}
