package com.infosysit.sdk.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import static com.infosysit.sdk.Constants.persistenceFile;

/**
 * Created by akansha.goyal on 3/15/2018.
 */

public class SharedPrefrence {
    public static String getItem(Context context , String key , String  defaultValue) {
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(persistenceFile , Context.MODE_PRIVATE);
            return sharedPreferences.getString(key,defaultValue.toString());
        }
        catch(Exception ex){
            return defaultValue;
        }


    }



    public static boolean setItem(Context context , String key , String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(persistenceFile , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
        return true;
    }

    public static boolean getBooleanItem(Context context , String key , boolean  defaultValue){
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(persistenceFile , Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(key,defaultValue);
        }
        catch(Exception ex){
            return defaultValue;
        }
    }

    public static boolean setBooleanItem(Context context , String key , boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(persistenceFile , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
        return true;
    }
}
