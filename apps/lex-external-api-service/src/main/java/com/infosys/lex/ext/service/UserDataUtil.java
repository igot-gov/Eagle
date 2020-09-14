package com.infosys.lex.ext.service;

import java.util.Date;
import java.util.Map;

public interface UserDataUtil {

	public Map<String, Date> toGMTLimits(String startDate, String endDate) throws Exception;

	public byte[] getBadgeImageFromContentStore(String badgeName) throws Exception;

	public String getAuthorization(String client,String key, String url);

}
