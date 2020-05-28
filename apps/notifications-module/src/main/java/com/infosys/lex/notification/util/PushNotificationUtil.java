package com.infosys.lex.notification.util;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class PushNotificationUtil {

	public static String getAPNSSandboxJson(String subject,String body,String url) throws JSONException {
		JSONObject apns = new JSONObject();
		JSONObject alert =  new JSONObject();
		alert.put("title", subject);
		alert.put("body", body);
		apns.put("alert", alert);
		
		apns.put("url", url);
		JSONObject apnsObj = new JSONObject();
		apnsObj.put("aps", apns);
		
		JSONObject jObject = new JSONObject();
		jObject.put("APNS_SANDBOX", apnsObj.toString());
		
		
		return jObject.toString();
	}
	
	public static String getAPNSJson(String subject,String body,String url) throws JSONException {
		JSONObject apns = new JSONObject();
		JSONObject alert =  new JSONObject();
		alert.put("title", subject);
		alert.put("body", body);
		apns.put("alert", alert);
		
		apns.put("url", url);
		JSONObject apnsObj = new JSONObject();
		apnsObj.put("aps", apns);
		
		JSONObject jObject = new JSONObject();
		jObject.put("APNS", apnsObj.toString());
		
		
		return jObject.toString();
	}
	
	public static String getFirebaseJson(String title,String body,String url) throws JSONException
	{
		JSONObject notification = new JSONObject();
		notification.put("body", body);
		notification.put("url", url);
		notification.put("title", title);
		
//		notification.put("image", "https://images.unsplash.com/photo-1495420378468-78588a508652?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1950&q=80");
		
		
		JSONObject gcm = new JSONObject();
		gcm.put("data", notification);
		JSONObject resp = new JSONObject();
		resp.put("GCM", gcm.toString());
		return resp.toString();
		
	}
}
