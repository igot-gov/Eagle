package com.infosysit.sdk.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

//import org.json.JSONObject;

/**
 * Created by akansha.goyal on 3/12/2018.
 */

public class CourseJava {
    @SerializedName("result")
    private JsonObject result;
    @SerializedName("id")
    private String id;
    @SerializedName("ver")
    private String ver;


    public JsonObject getResult() {
        return result;
    }

    public void setResult(JsonObject result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }
}
