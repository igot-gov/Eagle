package com.infosysit.sdk.services;

import com.infosysit.sdk.models.TokenJava;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akansha.goyal on 3/12/2018.
 */

public class GetTokenServiceJava {
    public TokenJava readJSON(String tokenString) throws JSONException {
        TokenJava token = new TokenJava();
        try{
            JSONObject tokenJSON = new JSONObject(tokenString);
            token.setIdToken(tokenJSON.getString("idToken"));
            token.setAuthServerUrl(tokenJSON.getString("authServerUrl"));
            token.setRealm(tokenJSON.getString("realm"));
            token.setRefreshToken(tokenJSON.getString("refreshToken"));
            token.setResponseType(tokenJSON.getString("responseType"));
            token.setSessionId(tokenJSON.getString("sessionId"));
            token.setToken(tokenJSON.getString("token"));

        }
        catch(Exception ex){throw ex;}

        return token;



    }
}
