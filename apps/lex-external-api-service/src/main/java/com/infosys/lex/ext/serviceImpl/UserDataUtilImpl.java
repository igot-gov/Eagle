package com.infosys.lex.ext.serviceImpl;

import java.io.BufferedInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;

import com.infosys.lex.ext.Models.Cassandra.AuthorizeData;
import com.infosys.lex.ext.ServiceRepository.bodhi.AuthorizationRepository;
import com.infosys.lex.ext.service.UserDataUtil;
import com.infosys.lex.ext.util.ExternalServicesJsonKey;

@Service
public class UserDataUtilImpl implements UserDataUtil {

	private PropertiesCache properties = PropertiesCache.getInstance();
	public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat formatterCass = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
	public static SimpleDateFormat formatterElastic = ProjectUtil.getDateFormatter();
	public static SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static SimpleDateFormat inputFormatterDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	AuthorizationRepository authRepo;

	@Override
	public Map<String, Date> toGMTLimits(String startDate, String endDate) throws Exception {
		Map<String, Date> ret = new HashMap<String, Date>();
		Date eDate = new Date();
		Date sDate = new Date();
		if (!startDate.equals("0") && !endDate.equals("0")) {
			if (inputFormatterDate.parse(endDate).after(inputFormatterDate.parse(startDate))) {
				if (((inputFormatterDate.parse(endDate).getTime() - inputFormatterDate.parse(startDate).getTime())
						/ (1000f * 60 * 60 * 24)) <= 5) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(inputFormatterDate.parse(endDate));
					cal.add(Calendar.HOUR, -5);
					cal.add(Calendar.MINUTE, -30);
					eDate = cal.getTime();
					if (eDate.compareTo((Calendar.getInstance().getTime())) > 0) {
						throw new Exception("End Date cannot be greater than todays's Date.");
					}

					cal.setTime(inputFormatterDate.parse(startDate));
					cal.add(Calendar.HOUR, -5);
					cal.add(Calendar.MINUTE, -30);
					sDate = cal.getTime();
				} else {
					throw new Exception("The differenct in dates should be samller than 5 days.");
				}
			} else {
				throw new Exception("The start date should be smaller than the end date.");
			}
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(formatter.parse(formatter.format(new Date())));
			cal.add(Calendar.HOUR, 13);
			cal.add(Calendar.MINUTE, 30);
			eDate = cal.getTime();
			cal.add(Calendar.DAY_OF_YEAR, -1);
			sDate = cal.getTime();
		}
		ret.put("sDate", sDate);
		ret.put("eDate", eDate);
		return ret;
	}

	@Override
	public byte[] getBadgeImageFromContentStore(String badgeName) throws Exception {
		byte byteArray[] = null;
		try {
			HttpClient httpClient = HttpClients.createDefault();
			// HttpClient httpClient = this.getAuthorizedClient();
			String contentHost = System.getenv(ExternalServicesJsonKey.SERVER_IP);
			String contentPort = System.getenv(ExternalServicesJsonKey.Content_Port);

			if (ProjectUtil.isStringNullOREmpty(contentHost) || ProjectUtil.isStringNullOREmpty(contentPort)) {
				contentHost = properties.getProperty(ExternalServicesJsonKey.SERVER_IP);
				contentPort = properties.getProperty(ExternalServicesJsonKey.Content_Port);
			}
			HttpGet getRequest = new HttpGet("http://" + contentHost + ":" + contentPort
					+ "/content/Achievements/Badges/" + badgeName.replaceAll(" ", "%20") + ".png?type=assets");
			HttpResponse response = httpClient.execute(getRequest);
			BufferedInputStream bin = new BufferedInputStream(response.getEntity().getContent());
			byteArray = IOUtils.toByteArray(bin);
			bin.close();
			String output = "";
			for (byte b : byteArray) {
				output += (char) b;
				if (output.contains("\"error\"")) {
					byteArray = "".getBytes();
					break;
				}
				if (output.length() > 20) {
					break;
				}
			}
		} catch (Exception e) {
			ProjectLogger.log("Error : Image fetch failed :" + e.getMessage(), e);
		}
		return byteArray;
	}

	@Override
	public String getAuthorization(String client, String key, String url) {
		String ret = "unauthorized";
		try {
			AuthorizeData result = authRepo.findById(client).orElse(null);
			if (result != null && result.getValue().equals(key)) {
				for (String st : result.getUrls()) {
					if (url.contains(st)) {
						ret = "valid";
						break;
					}
				}
				if (ret.equals("unauthorized"))
					ret = "invalidUrl";
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return ret;
	}

}