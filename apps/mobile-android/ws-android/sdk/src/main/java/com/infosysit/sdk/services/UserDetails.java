package com.infosysit.sdk.services;

import com.google.gson.JsonObject;
import com.infosysit.sdk.UtilityJava;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.infosysit.sdk.Constants.baseUrl;

/**
 * Created by akansha.goyal on 3/26/2018.
 */

public class UserDetails {
    public static void getDetails(){
        try{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterfaceJava apiInterface = retrofit.create(ApiInterfaceJava.class);
            Call<JsonObject> call = apiInterface.getUserData(UtilityJava.tokenValue);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    Log.d("pushGeneralTelemet",response.body().toString());
                    JsonObject userDetails = response.body().getAsJsonObject();
                    String name = userDetails.get("result").getAsJsonObject().get("response").getAsJsonObject().get("encEmail").getAsString();
//                    Log.d("pushGeneralTelemet",name);
                    if(response.isSuccessful()){
//                        Log.d("pushGeneralTelemet","Successfull");

                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    Log.d("pushGeneralTeleme","failure");

                }
            });
        }
        // debugs log
        //mainitain log file in case of bugs check that
        catch (Exception ex){
            throw ex;
        }
    }

//    public static void getRoles(){
//        try{
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            ApiInterfaceJava apiInterface = retrofit.create(ApiInterfaceJava.class);
//            Call<JsonObject> call = apiInterface.getUserRoles(UtilityJava.tokenValue);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
////                    Log.d("pushGeneralTelemet",response.body().toString());
//                    JsonObject userDetails = response.body().getAsJsonObject();
////                    String name = userDetails.get("result").getAsJsonObject().get("response").getAsJsonObject().get("encEmail").getAsString();
////                    Log.d("pushGeneralTelemet",name);
//                    if(response.isSuccessful()){
//
//                        Log.d("UserDetails","Successfull");
//                        JsonArray userRoles = userDetails.get("result").getAsJsonObject().get("response").getAsJsonArray();
////                        JsonElement chatBotelement = ;
////                        Log.d("",String.valueOf(userRoles.contains("chatbot"));
//                        Log.d("UserDetails","User "+userDetails.get("result").getAsJsonObject().get("response").getAsJsonArray().toString());
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    Log.d("UserDetails","failure");
//
//                }
//            });
//        }
//        // debugs log
//        //mainitain log file in case of bugs check that
//        catch (Exception ex){
//            throw ex;
//        }
//    }
}
