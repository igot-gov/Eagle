package com.infosysit.sdk.services;

import com.google.gson.JsonObject;
import com.infosysit.sdk.models.CourseJava;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by akansha.goyal on 3/12/2018.
 */

public interface ApiInterfaceJava {
    //for fetching course hierarchy



//    @GET("/clientApi/content/hierarchy/{content_id}")
    @GET("/clientApi/v2/content/{content_id}")
    Call<CourseJava> getCourseHierarchy(@Path("content_id") String content_id, @Header("authorization") String bearerToken);

    @Headers({"rootOrg:Infosys","org:Infosys Ltd","locale:en"})
    @GET("/apis/protected/v8/user/details/wtoken")
    Call<JsonObject> getUserDetails (@Header("authorization") String bearerToken);

    @Headers({"rootOrg:Infosys","org:Infosys Ltd","locale:en"})
    @POST("/apis/protected/v8/content/{id}?hierarchyType=detail")
    Call<JsonObject> getCourseHierarchyV2(@Body JsonObject object, @Path(value = "id" , encoded = true) String id, @Header("authorization") String bearerToken,@Header("wid") String widToken);

    //for telemetry events
//    @POST("/clientApi/telemetry/events")
    @POST("/clientApi/v2/telemetry")
    Call<JsonObject>  pushViewTelemetryData(@Body JsonObject object , @Header("authorization") String bearerToken);

    @GET("/clientApi/sunbird/user/me")
    Call<JsonObject>  getUserData(@Header("authorization") String bearerToken);

    @POST("/clientApi/user/history/{id}")
    Call<JsonObject> pushContinueLearning(@Path(value = "id" , encoded = true) String id , @Header("authorization") String bearerToken);


//    @POST("/clientApi/v2/content/view/{id}")
    @POST("/clientApi/v2/content/view/{id}")
    Call<JsonObject> pushViewHistory(@Path(value = "id" , encoded = true) String id , @Header("authorization") String bearerToken);

    @POST("/clientApi/v2/user/history/continue")
    Call<JsonObject> pushCourseDetails(@Body JsonObject object, @Header("authorization") String bearerToken);

    @POST("/clientApi/v2/user/evaluate/assessment/submit")
    Call<JsonObject> pushSubmitQuiz(@Body JsonObject object, @Header("authorization") String bearerToken);

    @GET("/clientApi/v2/user/roles")
    Call<JsonObject> getRoles(@Header("authorization") String bearerToken);

    @GET("/clientApi/v4/user/details/{user_id}")
    Call<JsonObject> getDownloadAllowed(@Header("authorization") String bearerToken, @Path(value = "user_id", encoded = true) String userId, @Query("requiredFields") String requiredFiled);

}
