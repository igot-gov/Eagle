package com.infosysit.sdk;

import android.util.Log;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class TelemeteryValidation {

    private String[] CP_ACTIVITY = new String[]{"uid", "sid", "ets", "cid", "mode", "resid", "restype", "devicedata", "playerdata", "bodhiuser"};
    private String[] MB_DOWNLOAD = new String[]{"uid", "sid", "ets", "channel", "did", "resid", "restype", "devicedata", "playerdata", "bodhiuser"};
    private String[] deviceData = new String[]{"device", "osVersion", "screenResolution", "deviceName", "UA", "mode"};
    private String[] bodhiuser = new String[]{"email"};
    private String[] quiz = new String[]{"questions", "isAssessment", "userEmail", "identifier", "title"};

    public JsonObject structureValidation(JsonObject telemeteryData, String action) {
        try {
            if (telemeteryData.has("params") && telemeteryData.has("events")) {
                JsonArray events = new JsonArray();
                if (action.equalsIgnoreCase("cp_activity")) {
                    events = eventValidation(telemeteryData.get("events").getAsJsonArray(), CP_ACTIVITY);
                } else if (action.equalsIgnoreCase("mb_download")) {
                    events = eventValidation(telemeteryData.get("events").getAsJsonArray(), MB_DOWNLOAD);
                }
                telemeteryData.add("events", events);
                return telemeteryData;
            }
            return null;
        } catch (Exception e) {
            Log.d("telemeteryvalidation", "structureValidation: Failed ");
            return null;
        }


    }

    private JsonArray eventValidation(JsonArray events, String[] arrayName) {
        boolean structureVerification = false;
        JsonArray finalArray = new JsonArray();

        for (JsonElement eventElement : events) {
            JsonObject event = eventElement.getAsJsonObject();
            for (String key : arrayName) {
                if (event.has(key)) {
                    structureVerification = true;
                    if (key.equalsIgnoreCase("devicedata")) {
                        if (!deviceJsonValidation(event.get(key).getAsJsonObject())) {
                            structureVerification = false;
                            continue;
                        }
                        ;
                    }
                    if (key.equalsIgnoreCase("bodhiuser")) {
                        if (!bodhiuserValidation(event.get(key).getAsJsonObject())) {
                            structureVerification = false;
                            continue;
                        }
                        ;
                    }
                } else {
                    structureVerification = false;
                    continue;
                }
            }
            if ((structureVerification)) {
                finalArray.add(event);
            }
        }


        return finalArray;

    }

    private boolean bodhiuserValidation(JsonObject asJsonObject) {
        for (String user : bodhiuser
                ) {
            if (!asJsonObject.has(user)) {
                return false;
            }
        }
        return true;
    }

    public boolean deviceJsonValidation(JsonObject deviceJson) {

        for (String device : deviceData
                ) {
            if (!deviceJson.has(device)) {
                return false;
            }
        }
        return true;
    }


    public boolean continueLearningValidation(JsonObject continueLearningJson) {
        String something = continueLearningJson.get("resourceId").toString();
        Log.d("continue", "continueLearningValidation: " + continueLearningJson.get("resourceId").toString() + " " + continueLearningJson.get("percentComplete").toString());
        Log.d("continue", "continueLearningValidation: " + continueLearningJson.get("resourceId").toString() + " " + continueLearningJson.get("percentComplete").toString());
        if (continueLearningJson.get("resourceId").toString().length() > 0 && continueLearningJson.get("percentComplete").toString().length() > 0) {
            return true;
        }
        return false;
    }


    public boolean quizSubmissionValidation(JsonObject quizSubmissionJson) {
        try {
            JsonObject data = quizSubmissionJson.get("request").getAsJsonObject();
            boolean isValidated = true;
            for (String key : quiz
                    ) {
                if (!data.has(key)) {
                    isValidated = false;
                    break;
                }
            }
            if (isValidated) {
                if (data.get("userEmail").toString().length() > 0 && data.get("identifier").toString().length() > 0) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            Log.d("TelemetryValidation", "quizSubmissionValidation: " + e);
            return false;
        }


    }
}
