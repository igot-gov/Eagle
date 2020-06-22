package com.infosysit.sdk.services;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonParser;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.persistence.SharedPrefrence;

import java.io.UnsupportedEncodingException;


/**
 * Created by akansha.goyal on 3/26/2018.
 */

public class JWTUtils {
    public static void decoded(Context context, String JWTEncoded) throws Exception {
        try {
            String[] tokenValue = JWTEncoded.split("\\.");
//            Log.d("JWT_DECODED", "Header: " + getJson(tokenValue[0]));
            JsonParser jsonParser = new JsonParser();
            Log.d("JWT_DECODED",getJson(tokenValue[1]).toString());
            Constants.UserEmail = jsonParser.parse(getJson(tokenValue[1])).getAsJsonObject().get("email").getAsString();
            Constants.userId = jsonParser.parse(getJson(tokenValue[1])).getAsJsonObject().get("sub").getAsString();
            SharedPrefrence.setItem(context, "userId", Constants.userId);
            SharedPrefrence.setItem(context, "emailId", Constants.UserEmail);
            Log.d("JWT_DECODED", "user id " + Constants.userId);
//            Log.d("JWT_DECODED",jsonParser.parse(getJson(tokenValue[1])).getAsJsonObject().toString());
//            Log.d("JWT_DECODED", "Body: " + getJson(tokenValue[1]));
//            Log.d("JWT_DECODED", "Body: " + Constants.UserEmail);
        } catch (UnsupportedEncodingException e) {
            //Error
        }
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
