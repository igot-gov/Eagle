package com.infosysit.sdk.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.TelemeteryValidation;
import com.infosysit.sdk.UtilityJava;
import com.infosysit.sdk.persistence.SharedPrefrence;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infosysit.sdk.Constants.baseUrl;
import static com.infosysit.sdk.Constants.contentTelemetry;
import static com.infosysit.sdk.Constants.openRapDownload;
import static com.infosysit.sdk.Constants.telemetryTokenKey;

/**
 * Created by akansha.goyal on 3/14/2018.
 */

public class TelemetryServices {


    public  JsonObject telemetryPlayer(Context context, JsonObject telemetryData, String mode) throws Exception{
        JsonObject telemetryHeader  = new JsonObject();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        telemetryHeader.addProperty("id","lex.telemetry");
        telemetryHeader.addProperty("ver","2.0");
        telemetryHeader.addProperty("ts",strDate);
        JsonObject paramsData = new JsonObject();
        paramsData.addProperty("requesterId","f031165f-d4b0-4d5a-9a9d-61a41ccdebaf");
        paramsData.addProperty("did","mobile");
        paramsData.addProperty("key","13405d54-85b4-341b-da2f-eb6b9e546fff");
        paramsData.addProperty("msgid","DUMMY-UUID");
        telemetryHeader.add("params",paramsData);

        try{
            JsonObject telemetry  = new JsonObject();
            telemetry.addProperty("eid","CP_ACTIVITY");  //session id needs to be updated
            telemetry.addProperty("ver","2.0");
            telemetry.addProperty("uid",SharedPrefrence.getItem(context, "userId", ""));
            telemetry.addProperty("sid",SharedPrefrence.getItem(context,Constants.last_session_id,"undefined"));
            telemetry.addProperty("mid","");
            telemetry.addProperty("ets", System.currentTimeMillis());
            telemetry.addProperty("mode",mode);
//            Log.d("TelemetryPlayerActivity",contentTelemetry.toString());

            if (contentTelemetry.size()>0) {
                if(contentTelemetry.get("courseid").getAsString() != ""){
                    telemetry.addProperty("courseid",contentTelemetry.get("courseid").getAsString()); //need to take a decision
                }
                else{
                    telemetry.add("courseid",null);
                }
                telemetry.addProperty("resid",contentTelemetry.get("resid").getAsString());//value has to be passed
                telemetry.addProperty("restype",contentTelemetry.get("restype").getAsString());

            }
            else{
                telemetry.add("courseid",null);
                telemetry.addProperty("resid","");//value has to be passed
                telemetry.addProperty("restype","");
            }


            telemetry.add("progress",null);
            telemetry.addProperty("duration",0);
            JsonObject bodhiuser = new JsonObject();
            bodhiuser.addProperty("email",SharedPrefrence.getItem(context, "emailId", "")) ; //needs to be passed
            bodhiuser.add("location",null) ; //needs to be paased
            bodhiuser.addProperty("unit","");
            telemetry.add("bodhiuser",bodhiuser);

            JsonObject deviceData = new JsonObject();
            deviceData.addProperty("device","Android");
            deviceData.addProperty("osVersion",android.os.Build.VERSION.SDK_INT);
            deviceData.addProperty("screenResolution",UtilityJava.getScreenResolution(context));
            deviceData.addProperty("deviceName",android.os.Build.MODEL);
            deviceData.addProperty("UA","WebView");
            deviceData.addProperty("mode","offline");

            telemetry.add("devicedata",deviceData);

            telemetry.add("playerdata",telemetryData);
            if (mode.equalsIgnoreCase("offline")){
                String telemetryAppData = SharedPrefrence.getItem(context , telemetryTokenKey , "undefined");
//            Log.d("TelemetryPlayerActivity",SharedPrefrence.getItem(context , telemetryTokenKey , "undefined"));
                if(!telemetryAppData.equals("undefined") && !telemetryAppData.equals("") ){
                    JsonParser parser = new JsonParser();
                    JsonObject telemetryObejct = parser.parse(telemetryAppData).getAsJsonObject();
                    telemetryObejct.get("events").getAsJsonArray().add(telemetry);
                    SharedPrefrence.setItem(context,telemetryTokenKey ,telemetryObejct.toString());
                }
                else{
                    JsonArray eventdata = new JsonArray();

                    telemetryHeader.add("events",eventdata);
                    eventdata.add(telemetry);

                    SharedPrefrence.setItem(context,telemetryTokenKey,telemetryHeader.toString());
                }
            }
            else{

                if(!Constants.externalPlayerTelemetryJson.has("events")){
                    Constants.externalPlayerTelemetryJson = telemetryHeader;
                    Constants.externalPlayerTelemetryJson.add("events",new JsonArray());
                }
                JsonArray eventData  = Constants.externalPlayerTelemetryJson.getAsJsonArray("events");
                eventData.add(telemetry);
                Constants.externalPlayerTelemetryJson.add("events",eventData);
                Log.d("TelemetryExternalPlayer","Reached Here"+System.currentTimeMillis());
                Log.d("TelemetryExternalPlayer","Size "+Constants.externalPlayerTelemetryJson.getAsJsonArray("events").size());

                JsonArray eventdata = new JsonArray();
                telemetryHeader.add("events",eventdata);
                eventdata.add(telemetry);
            }

//            Log.d("TelemetryPlayerActivity",SharedPrefrence.getItem(context , telemetryTokenKey , "undefined"));

        }
        catch (Exception ex){
            Log.e("Exception",ex.getMessage());
            throw ex;
        }
        return telemetryHeader;
    }

    public void telemetryPage (Context context) {
        JsonObject telemetryHeader  = new JsonObject();
        telemetryHeader.addProperty("id","lex.telemetry");
        telemetryHeader.addProperty("ver","2.0");
        telemetryHeader.addProperty("ts","chnage");
        JsonObject paramsData = new JsonObject();
        paramsData.addProperty("requesterId","f031165f-d4b0-4d5a-9a9d-61a41ccdebaf");
        paramsData.addProperty("did","mobile");
        paramsData.addProperty("key","13405d54-85b4-341b-da2f-eb6b9e546fff");
        paramsData.addProperty("msgid","DUMMY-UUID");
        telemetryHeader.add("params",paramsData);

        try{
            JsonObject telemetry  = new JsonObject();
            telemetry.addProperty("eid","CP_IMPRESSION");  //session id needs to be updated
            telemetry.addProperty("ver","1.0");
            telemetry.addProperty("uid","chnage");
            telemetry.addProperty("sid","chnage");
            telemetry.addProperty("mid","");
            telemetry.addProperty("ets", System.currentTimeMillis());
            telemetry.addProperty("channel","");
            telemetry.addProperty("mode","offline");
            telemetry.addProperty("did","");
            JsonObject pData = new JsonObject();
            pData.addProperty("id","lex.portal");
            pData.addProperty("ver","1.0");
            telemetry.add("pdata",pData);
            JsonArray cData = new JsonArray();
            JsonObject objectOfCData = new JsonObject();
            objectOfCData.addProperty("id","");
            objectOfCData.addProperty("type","");
            cData.add(objectOfCData);
            telemetry.add("cdata",cData);
            JsonObject eTags = new JsonObject();
            eTags.add("app",new JsonArray());
            eTags.add("parameter", new JsonArray());
            eTags.add("dims", new JsonArray());
            telemetry.add("etags",eTags);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sid","change");
            telemetry.add("context",jsonObject);
            JsonObject edata = new JsonObject();
            JsonObject eks = new JsonObject();
            eks.addProperty("env","change");
            eks.addProperty("type","change");
            eks.addProperty("pageid","change");
            eks.addProperty("id","change");
            eks.addProperty("name","change");
            eks.addProperty("url","change");
            edata.add("eks",eks);
            telemetry.add("edata",edata);
            JsonObject bodhiuser = new JsonObject();
            bodhiuser.addProperty("email","change") ; //needs to be passed
            bodhiuser.add("location",null) ; //needs to be paased
            bodhiuser.addProperty("unit","");
            telemetry.add("bodhiuser",bodhiuser);
            //change
            JsonObject devicedata = new JsonObject();
            telemetry.add("devicedata",devicedata);
            JsonObject pageInf = new JsonObject();
            pageInf.addProperty("env","change");
            pageInf.addProperty("type","change");
            pageInf.addProperty("pageid","change");
            pageInf.addProperty("id","change");
            pageInf.addProperty("name","change");
            pageInf.addProperty("url","change");
            pageInf.add("pagedata",new JsonArray());
            JsonObject bodhidata = new JsonObject();
            bodhidata.add("pageinf",pageInf);
            telemetry.add("bodhidata",bodhidata);
            JsonObject lastpageinf = new JsonObject();
            lastpageinf.addProperty("pageid","change");
            lastpageinf.addProperty("pagesection","change");
            lastpageinf.addProperty("url","change");
            lastpageinf.add("pagedata", new JsonArray());
            telemetry.add("lastpageinf",lastpageinf);
            String telemetryAppData = SharedPrefrence.getItem(context , telemetryTokenKey , "");

            if(!telemetryAppData.equals("undefined") && !telemetryAppData.equals("") ){
                Gson gsonObject = new Gson();
                JsonElement telemetryJSON = gsonObject.fromJson(telemetryAppData, JsonElement.class);
                JsonObject telemetryJSONObj = telemetryJSON.getAsJsonObject();
                JsonArray playerDataArray = telemetryJSONObj.getAsJsonArray("events");
//                playerDataArray.add(playerTelemetry);
                telemetryJSONObj.add("events",playerDataArray);
                SharedPrefrence.setItem(context,telemetryTokenKey ,telemetryJSONObj.toString());
            }
            else{
                SharedPrefrence.setItem(context,telemetryTokenKey,telemetry.toString());
            }

        }
        catch (Exception ex){
            throw ex;
        }
    }

//    {
//        device: “Android/iOS/Desktop”,
//        osVersion: “”,
//        screenResolution: “” // this tells if the device is a phone or tablet or TV etc
//        deviceName: “iPhone5/iPhone6/In case of Android ,Manufacturer-model name combination”
//        UA:”Browser/WebView User Agent”
//        Mode:Online/offline
//
//    }

    public JsonObject devicedata(){
        JsonObject deviceData = new JsonObject();
        deviceData.addProperty("device","Android");
        return deviceData;
    }


    public JsonObject telemetryDownload(Context context, String resid , String resType , String contentType , String downloadMode, String status){
        JsonObject telemetry = new JsonObject();

            try{
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
                Date now = new Date();
                String strDate = sdfDate.format(now);

                telemetry.addProperty("id","lex.telemetry");
                telemetry.addProperty("version","1.0");
                telemetry.addProperty("ts",strDate);
                JsonObject paramsData = new JsonObject();
                paramsData.addProperty("requesterId","f031165f-d4b0-4d5a-9a9d-61a41ccdebaf");
                paramsData.addProperty("did","portal");
                paramsData.addProperty("key","13405d54-85b4-341b-da2f-eb6b9e546fff");
                paramsData.addProperty("msgid","DUMMY-UUID");
                telemetry.add("params",paramsData);
                JsonArray eventsArray = new JsonArray();

                JsonObject eventData = new JsonObject();

                eventData.addProperty("eid","MB_DOWNLOAD");
                eventData.addProperty("ets",System.currentTimeMillis());
                eventData.addProperty("ver","2.0");
                eventData.addProperty("mid","OE_0e9a9f4a552f4b445f57184f30ba2ac0");
                eventData.addProperty("sid",SharedPrefrence.getItem(context,Constants.last_session_id,""));
                eventData.addProperty("channel","b00bc992ef25f1a9a8d63291e20efc8d");
                eventData.addProperty("uid",SharedPrefrence.getItem(context, "userId", ""));
                eventData.addProperty("did","mobile");
                eventData.addProperty("resid",resid);
                eventData.addProperty("restype",resType);
                if (downloadMode != null && !downloadMode.equals("")) {
                    eventData.addProperty("downloadMode", downloadMode);
                }
                eventData.addProperty("status",status);
                eventData.addProperty("contentType",contentType);
                JsonObject bodhiuser = new JsonObject();
                bodhiuser.addProperty("email", SharedPrefrence.getItem(context, "emailId", "")) ; //needs to be passed
                bodhiuser.addProperty("location","BLR"); ; //needs to be paased
                bodhiuser.addProperty("unit","GDLYEA");
                eventData.add("bodhiuser",bodhiuser);
                JsonObject deviceData = new JsonObject();
                deviceData.addProperty("device","Android");
                deviceData.addProperty("osVersion",android.os.Build.VERSION.SDK_INT);
                deviceData.addProperty("screenResolution",UtilityJava.getScreenResolution(context));
                deviceData.addProperty("deviceName",android.os.Build.MODEL);
                deviceData.addProperty("UA","WebView");
                deviceData.addProperty("mode","Online");

                eventData.add("devicedata",deviceData);
                eventsArray.add(eventData);
                telemetry.add("events",eventsArray);

        }

        catch (Exception ex){
            Log.e("TelemetryException",ex.getMessage());
        }
        return telemetry;
    }


    public JsonObject telemteryDownloadEvent(Context context, String resid , String resType , String contentType , String downloadMode, String status){
        JsonObject eventData = new JsonObject();
        try{
            eventData.addProperty("eid","MB_DOWNLOAD");
            eventData.addProperty("ets",System.currentTimeMillis());
            eventData.addProperty("ver","2.0");
            eventData.addProperty("mid","OE_0e9a9f4a552f4b445f57184f30ba2ac0");
            eventData.addProperty("sid",SharedPrefrence.getItem(context,Constants.last_session_id,""));
            eventData.addProperty("channel","b00bc992ef25f1a9a8d63291e20efc8d");
            eventData.addProperty("uid",SharedPrefrence.getItem(context, "userId", ""));
            eventData.addProperty("did","mobile");
            eventData.addProperty("resid",resid);
            eventData.addProperty("restype",resType);
            if (downloadMode != null && !downloadMode.equals("")) {
                eventData.addProperty("downloadMode", downloadMode);
            }
            eventData.addProperty("status",status);
            eventData.addProperty("contentType",contentType);
            JsonObject bodhiuser = new JsonObject();
            bodhiuser.addProperty("email", SharedPrefrence.getItem(context, "emailId", "")) ; //needs to be passed
            bodhiuser.addProperty("location","BLR"); ; //needs to be paased
            bodhiuser.addProperty("unit","GDLYEA");
            eventData.add("bodhiuser",bodhiuser);
            JsonObject deviceData = new JsonObject();
            deviceData.addProperty("device","Android");
            deviceData.addProperty("osVersion",android.os.Build.VERSION.SDK_INT);
            deviceData.addProperty("screenResolution",UtilityJava.getScreenResolution(context));
            deviceData.addProperty("deviceName",android.os.Build.MODEL);
            deviceData.addProperty("UA","WebView");
            deviceData.addProperty("mode","Online");

            eventData.add("devicedata",deviceData);
        }

        catch (Exception ex){
            Log.e("TelemetryException",ex.getMessage());
        }
        return eventData;
    }
//    public static void telemetryQuiz(String telemetryData){
//        try{
//            JsonObject quizTelemetryData = new JsonObject();
//            quizTelemetryData.addProperty("sid","") ; //needs to be passed
//            quizTelemetryData.addProperty("title",""); //needs to be passed
//            quizTelemetryData.addProperty("mode","offline");
//            quizTelemetryData.addProperty("identifier",""); //needs to be passed
//            quizTelemetryData.addProperty("did",""); //needs to be passed
//            JsonArray telemetryQuizArray = new JsonArray();
//            String telemetryQuizData = SharedPrefrence.getItem(sContext, telemetryQuizTokenKey,"undefined" );
//            //var telemetryQuizArray :JSONArray =JSONArray()
//            if(!telemetryQuizData.equals("undefined") && !telemetryQuizData.equals("") ){
//                Gson gsonObject = new Gson();
//                JsonElement telemetryJSON = gsonObject.fromJson(telemetryQuizData, JsonElement.class);
//                telemetryQuizArray = telemetryJSON.getAsJsonArray();
//                telemetryQuizArray.add(quizTelemetryData);
//                SharedPrefrence.setItem(sContext,telemetryQuizTokenKey,telemetryQuizArray.toString());
//            }
//            else{
//                telemetryQuizArray.add(quizTelemetryData);
//                SharedPrefrence.setItem(sContext,telemetryQuizTokenKey,telemetryQuizArray.toString());
//            }
//
//        }
//        catch(Exception ex){
//            throw ex;
//        }
//
//
//    }

    // coming from openrap
    public static void pushDownloadTelemetryData(final Context context, String identifier, String downloadMode, String status) {
        TelemetryServices telemetryServices = new TelemetryServices();
        String openRapContent = SharedPrefrence.getItem(context,openRapDownload,"");
        JsonParser parser = new JsonParser();
        JsonObject telemetryObject = new JsonObject();
        if(!openRapContent.equalsIgnoreCase("")){
            telemetryObject = parser.parse(openRapContent).getAsJsonObject();
            if(telemetryObject.has("events")){
                JsonObject telemetryDownloadJson = telemetryServices.telemteryDownloadEvent(context, identifier, "", "", downloadMode, status);
                JsonArray telemetryArray = telemetryObject.get("events").getAsJsonArray();
                telemetryArray.add(telemetryDownloadJson);
                telemetryObject.add("events",telemetryArray);
            }
        }
        else{
            telemetryObject = telemetryServices.telemetryDownload(context, identifier, "", "", downloadMode, status);
        }
        Log.d("openRapTesting","JSON: "+telemetryObject);
        SharedPrefrence.setItem(context,openRapDownload,telemetryObject.toString());
//        String valString = SharedPrefrence.getItem(context,openRapDownload,"undefined");
//        Log.d("TelemetryStringOPenrap","Value: "+valString);
//        downloadTelemetry(context, telemetryDownloadJson);
    }
    public static void pushDownloadTelemetryData(final Context context, JsonObject jsonObject , String downloadMode, String status){
        TelemetryServices telemetryServices = new TelemetryServices();
        JsonObject telemetryDownloadJson = telemetryServices.telemetryDownload(context, jsonObject.get("identifier").getAsString(),jsonObject.get("mimeType").getAsString(),jsonObject.get("contentType").getAsString(), downloadMode, status);
//        String telemetryData = telemetryDownloadJson.toString();
        Log.d("TelemetryString",telemetryDownloadJson.toString());
        downloadTelemetry(context, telemetryDownloadJson);
    }

    private static void downloadTelemetry(final Context context, JsonObject telemetryDownloadJson) {

        ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(baseUrl);
        Log.d("TestCases3Service","TestCases3 " +SharedPrefrence.getItem(context,telemetryTokenKey,""));
//        RequestBody body = RequestBody.create(MediaType.parse("application/json"),telemetryData);
        Log.d("jsonObjectDownload", "downloadTelemetry: "+telemetryDownloadJson);
        TelemeteryValidation telemeteryValidation = new TelemeteryValidation();
        JsonObject validatedJson = telemeteryValidation.structureValidation(telemetryDownloadJson,"mb_download");
        Call<JsonObject> call = apiInterface.pushViewTelemetryData(validatedJson, UtilityJava.tokenValue);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Log.d("pushGeneralTelemet1",response.code() + "");
                if(response.isSuccessful()){
                   // SharedPrefrence.setItem(context, telemetryTokenKey,"");
//                    Log.d("pushGeneralTeleme2","Success");
                    SharedPrefrence.setItem(context, "ApiCall","Success "+getClass().getSimpleName());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.d("pushGeneralTeleme3",t.getMessage());

            }
        });
    }




//    public void pushGeneralTelemetryData(){
//        try{
//            String telemetryData = SharedPrefrence.getItem(sContext,telemetryTokenKey,"undefined" );
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            ApiInterfaceJava apiInterface = retrofit.create(ApiInterfaceJava.class);
//            RequestBody body = RequestBody.create(MediaType.parse("application/json"),telemetryData);
////            Call<JsonObject> call = apiInterface.pushViewTelemetryData(body, UtilityJava.tokenValue);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    Log.d("pushGeneralTelemet","response");
//                    if(response.isSuccessful()){
//                        SharedPrefrence.setItem(sContext,telemetryTokenKey,"");
//                        Log.d("pushGeneralTeleme","Success");
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    Log.d("pushGeneralTeleme","failure");
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

//    public void pushQuizTelemetryData(){
//        try{
//            String telemetryQuizData = SharedPrefrence.getItem(sContext,telemetryQuizTokenKey,"undefined" );
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            ApiInterfaceJava apiInterface = retrofit.create(ApiInterfaceJava.class);
//            RequestBody body = RequestBody.create(MediaType.parse("application/json"),telemetryQuizData);
//            Call<JsonObject> call = apiInterface.pushViewTelemetryData(body,UtilityJava.tokenValue);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    if(response.isSuccessful()){
//                        SharedPrefrence.setItem(sContext,telemetryQuizTokenKey,"");
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                }
//            });
//
//        }
//        catch (Exception ex){
//            throw ex;
//        }
//    }
}

