package com.infosys.lex.ext.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;

import com.infosys.lex.ext.exception.BadRequestException;
import com.infosys.lex.ext.serviceImpl.UserDataUtilImpl;

public class ProjectCommonUtil {

	private static PropertiesCache properties = PropertiesCache.getInstance();

	public static boolean isStringNullOrEmpty(String text) {
		return text == null || text.isEmpty();
	}

	public static boolean isNull(Object object) {
		return object == null;
	}

	public static boolean isNotNull(Object object) {
		return object != null;
	}

	public static void removeKeysFromListOfMaps(List<Map<String, Object>> maps, List<String> keys) {
		for (Map<String, Object> map : maps) {
			for (String key : keys) {
				map.remove(key);
			}
		}
	}

	public static Map<String, String> getDateTimeFormatForElasticSearch(Date sDate, Date eDate) throws ParseException {
		Map<String, String> timeAndEpochMap = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		String startDateFormat = sdf.format(sDate);
		String endDateFormat = sdf.format(eDate);
		timeAndEpochMap.put("sDate", startDateFormat);
		timeAndEpochMap.put("eDate", endDateFormat);
		return timeAndEpochMap;
	}

	public static List<Date> getDatesBetweenStartAndEndDate(Date startDate, Date endDate) throws ParseException {
		startDate = UserDataUtilImpl.formatter.parse(UserDataUtilImpl.formatter.format(startDate));
		endDate = UserDataUtilImpl.formatter.parse(UserDataUtilImpl.formatter.format(endDate));
		List<Date> dates = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		while (startDate.before(endDate)) {
			dates.add(startDate);
			calendar.setTime(startDate);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			startDate = calendar.getTime();
		}
		dates.add(startDate);
		return dates;
	}

	public static void elasticSearchPageSizeValidations(String pageSize) {
		try {
			if (Integer.parseInt(pageSize) > LexConstants.MAX_PAGE_SIZE_ES || Integer.parseInt(pageSize) <= 0) {
				throw new BadRequestException("Page size should be greater than 0 and less than 10000");
			}
		} catch (NumberFormatException numberFormatException) {
			throw new BadRequestException("Page size should be an integer");
		}
	}

	public static void cassandraPageSizeValidations(String pageSize) {
		try {
			if (Integer.parseInt(pageSize) > LexConstants.MAX_PAGE_SIZE || Integer.parseInt(pageSize) <= 0) {
				throw new BadRequestException("Page size should be greater than 0 and less than 25000");
			}
		} catch (NumberFormatException numberFormatException) {
			throw new BadRequestException("Page size should be an integer");
		}
	}
	
	public static void postgresPageSizeValidations(Integer pageSize) {
		try {
			if (pageSize > LexConstants.MAX_PAGE_SIZE || pageSize <= 0) {
				throw new BadRequestException("Page size should be greater than 0 and less than 25000");
			}
		} catch (NumberFormatException numberFormatException) {
			throw new BadRequestException("Page size should be an integer");
		}
	}

	public static void dumpTypeValidations(String dumpType, String startDate, String endDate) {
		if (dumpType.equals(LexConstants.DUMP_TYPE_FULL) && (!startDate.equals("0") || !endDate.equals("0"))) {
			throw new BadRequestException("Start Date and End Date are not required for full dumps");
		}
	}

	public static String getIpAndPort(String hostName, String portName) {
		String host = System.getenv(hostName);
		String port = System.getenv(portName);
		if (ProjectUtil.isStringNullOREmpty(host) || ProjectUtil.isStringNullOREmpty(port)) {
			host = properties.getProperty(hostName);
			port = properties.getProperty(portName);
		}
		if (ProjectUtil.isStringNullOREmpty(host) || ProjectUtil.isStringNullOREmpty(port)) {
			throw new BadRequestException("host or port not found in env or properties file");
		}
		return "http://" + host + ":" + port;
	}
}
