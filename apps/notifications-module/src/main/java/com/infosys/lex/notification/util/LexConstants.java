package com.infosys.lex.notification.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LexConstants {

	public static final String ROOT_ORG = "root_org";
	public static final String ORG = "org";
	public static final String USERID = "user_uuid";
	public static final String LANGUAGE_HEADER = "Accept-Language";
	public static final String DEFAULT_lANGUAGE_CODE = "en";
	public static final String FIREBASE = "fcm";
	public static final String APNS = "apns";

	public static final String DEFAULT_TEMPLATE_LANGUAGE_PREFIX  = "DEFAULT_";
	public static final String DEFAULT_TEMPLATE_ORG = "default";
	public static final String APNS_SANDBOX = "apns_sandbox";
	public static final List<String> VALID_NOTFICATION_PLATFORMS = new ArrayList<String>(Arrays.asList(FIREBASE,APNS,APNS_SANDBOX)) ;
	
	public static final String MANAGER = "manager";
	
}
