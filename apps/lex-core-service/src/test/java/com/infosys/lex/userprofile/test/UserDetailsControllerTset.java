package com.infosys.lex.userprofile.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.model.SunbirdApiResp;
import com.infosys.lex.common.model.SunbirdApiRespContent;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.ApplicationLogicError;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDetailsControllerTset {

	private MockMvc mockMvc;

	private HttpHeaders headers;

	@Autowired
	RestTemplate restTemplate;

	String url = "https://igot-sunbird.idc.tarento.com/" + "api/user/v1/search";

	@Before
	public void setUp() {
		headers = new HttpHeaders();
		headers.add("Authorization",
				"bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJRekw4VVA1dUtqUFdaZVpMd1ZtTFJvNHdqWTg2a2FrcSJ9.TPjV0xLacSbp3FbJ7XeqHoKFN35Rl4YHx3DZNN9pm0o");
		headers.add("X-Authenticated-User-Token",
				"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJVMkpUdlpERFY4eG83ZmtfNHd1Yy1kNVJmNjRPTG1oemlRRUhjR25Vc2hNIn0.eyJqdGkiOiI1NWVmYjI3OC1iYjc0LTQ5NzUtYTQwNS04NGM3NDljY2YyOTgiLCJleHAiOjE2MDcxNDUxOTAsIm5iZiI6MCwiaWF0IjoxNjA3MDU4NzkwLCJpc3MiOiJodHRwczovL2lnb3Qtc3VuYmlyZC5pZGMudGFyZW50by5jb20vYXV0aC9yZWFsbXMvc3VuYmlyZCIsImF1ZCI6ImFkbWluLWNsaSIsInN1YiI6ImY6OTIzYmRjMTgtNTIwZC00OGQ0LWE4NGUtM2NkZTFlNjU1ZWJkOjdiMTNlMjJkLTkzMzgtNDM4MC1hYTk3LTI0MjQ1YTMxOWI5MSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImFkbWluLWNsaSIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6IjhjY2Q1ZGY4LTlhZjUtNDA0Yi1hYTdmLTJjOThmZGQ0MWRmMCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOltdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6e30sIm5hbWUiOiJpZ290IGRlbW8xIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic3VicmF0IiwiZ2l2ZW5fbmFtZSI6Imlnb3QiLCJmYW1pbHlfbmFtZSI6ImRlbW8xIn0.EztxO1g7aCMx-IIuAQpDJEJYYF9MKhypItdt05CviTgAC7edg73T04Ybo0I0_6BkEhuhdxYqWtyiX9Cm0kH5LpLleJGJysIlwOcs-ItIvjAz8KmFLtZHlhDH6U5_XzfxxtFuTWehjyvWTVsy2BN_hCsL4RdqvdlppUyPz8vwf5iED1OM7NkUjPn5XvgotLcxgS57Sl6FJlFu4X-bmvSg6R_VGVZy6UvndDrKmvcQ1E_9b9B880sNqsvJK18YG0PEK0d2hxvFHn6ot57ljUi8Ekq3aK4CLYZ8ZYfccrv4S2Z_RB_2WmwEAQJeMxWWo0kScCcM5nr0CUf6W-XQNMuLdQ");
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void fetchUserDataByUserPropertyTest() throws JsonProcessingException {
		Map<String, Object> result = new HashMap<>();

		Map<String, Object> pidRequestMap = new HashMap<>();
		Map<String, Object> request = new HashMap<String, Object>();

		Map<String, String> filters = new HashMap<String, String>();
		filters.put("rootOrgName", "igot-karmayogi");
		request.put(PIDConstants.FILTERS, filters);

		pidRequestMap.put("request", request);
		String reqBodyData = new ObjectMapper().writeValueAsString(pidRequestMap);

		HttpEntity<Map<String, Object>> requestEnty = new HttpEntity<>(pidRequestMap, headers);
		SunbirdApiResp pidResponse = restTemplate.postForObject(url, requestEnty, SunbirdApiResp.class);

		if (pidResponse == null)
			result.put("userDetail", null);
		else
			result.put("userDetail", pidResponse.getResult().getResponse().getContent().get(0));

		assertEquals(8, pidResponse.getResult().getResponse().getCount(),
				"Count =" + pidResponse.getResult().getResponse().getCount());

		System.out.println("Response count value = " + pidResponse.getResult().getResponse().getCount());

		assertEquals("cc0c1749-4c47-49c8-9f46-2bbdd42ef877",
				pidResponse.getResult().getResponse().getContent().get(0).getId());

		System.out.println("id " + pidResponse.getResult().getResponse().getContent().get(0).getId());

		SunbirdApiRespContent content = (SunbirdApiRespContent) result.get("userDetail");

		assertEquals("0131397178949058560", content.getRootOrgId());

	}

	@Test
	public void validateUserTest() {
		String rootOrg = "igot-karmayogi";
		String userId = "cc0c1749-4c47-49c8-9f46-2bbdd42ef877";
		Map<String, Object> pidRequestMap = new HashMap<>();
		Map<String, Object> request = new HashMap<String, Object>();
		Map<String, String> filters = new HashMap<String, String>();
		filters.put(PIDConstants.ROOT_ORG, rootOrg);
		filters.put(PIDConstants.USER_ID, userId);
		request.put(PIDConstants.FILTERS, filters);
		pidRequestMap.put("request", request);

		try {
			String reqBodyData = new ObjectMapper().writeValueAsString(pidRequestMap);

			HttpEntity<String> requestEnty = new HttpEntity<>(reqBodyData, headers);
			/*
			 * List<Map<String, Object>> pidResponse = restTemplate.postForObject( "http://"
			 * + serverConfig.getPidIp() + ":" + serverConfig.getPidPort().toString() +
			 * "/user", pidRequestMap, List.class);
			 */

			SunbirdApiResp pidResponse = restTemplate.postForObject(url, requestEnty, SunbirdApiResp.class);

			/*
			 * if (pidResponse.isEmpty() || pidResponse == null) { return false; } else {
			 * return (pidResponse.get(0).get("wid").equals(userId)); }
			 */
			SunbirdApiRespContent content = null;

			if (pidResponse != null && "OK".equalsIgnoreCase(pidResponse.getResponseCode())
					&& pidResponse.getResult().getResponse().getCount() >= 1) {
				content = pidResponse.getResult().getResponse().getContent().get(0);

			}

			assertTrue(content.getId().equals(userId));

		} catch (Exception e) {
			throw new ApplicationLogicError("PID ERROR: ", e);
		}

	}

	/**
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 * 
	 */
	@Test
	public void validateUsersTest() throws JsonGenerationException, JsonMappingException, IOException {
		String rootOrg = "igot-karmayogi";
		List<String> userIds = new LinkedList<>(
				Arrays.asList("7b13e22d-9338-4380-aa97-24245a319b91", "cc0c1749-4c47-49c8-9f46-2bbdd42ef877"));

		Map<String, Object> result = new HashMap<>();
		List<String> validUuids = new ArrayList<>();

		List<String> userIdsList = new ArrayList<>();

		// String userId = "[";
		for (String uId : userIds) {
			// userId+=uId+",";
			userIdsList.add(uId);
		}
		/*
		 * userId = userId.substring(0, userId.length() - 1); userId = userId + "]";
		 */

		Map<String, Object> pidRequestMap = new HashMap<>();
		Map<String, Object> request = new HashMap<String, Object>();

		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(PIDConstants.ROOT_ORG, rootOrg);
		filters.put(PIDConstants.USER_ID, userIdsList);
		request.put(PIDConstants.FILTERS, filters);
		//request.put("query", "");
		pidRequestMap.put("request", request);

		try {

			/*
			 * List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://"
			 * + serverConfig.getPidIp() + ":" + serverConfig.getPidPort().toString() +
			 * "/user/multi-fetch/wid", pidRequestMap, List.class);
			 */

			HttpEntity<Map<String, Object>> requestEnty = new HttpEntity<>(pidRequestMap, headers);

			SunbirdApiResp pidResponse = restTemplate.postForObject(url, requestEnty, SunbirdApiResp.class);

			/*
			 * for (Map<String, Object> map : pidResponse) { if (map.get("wid") != null) {
			 * validUuids.add(map.get("wid").toString()); userIds.remove(map.get("wid")); }
			 * }
			 */

			if (pidResponse != null && "OK".equalsIgnoreCase(pidResponse.getResponseCode())
					&& pidResponse.getResult().getResponse().getCount() >= 1) {

				for (SunbirdApiRespContent content : pidResponse.getResult().getResponse().getContent()) {

					// System.out.println(content.getId().toString());

					validUuids.add(content.getId().toString());
					userIds.remove(content.getId().toString());
				}
			}

			result.put("valid_users", validUuids);
			result.put("invalid_users", userIds);
		} catch (Exception e) {
			throw new ApplicationLogicError("PID ERROR: ", e);

		}

		assertEquals("cc0c1749-4c47-49c8-9f46-2bbdd42ef877", validUuids.get(0));
	}

	@Test
	public void getUsersDataFromUserIds() {
		String rootOrg = "igot-karmayogi";
		List<String> userIds = new LinkedList<>(
				Arrays.asList("7b13e22d-9338-4380-aa97-24245a319b91", "cc0c1749-4c47-49c8-9f46-2bbdd42ef877"));

		Map<String, Object> result = new HashMap<>();
		List<String> validUuids = new ArrayList<>();
		Map<String, Object> pidRequestMap = new HashMap<>();
		Map<String, Object> request = new HashMap<String, Object>();

		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(PIDConstants.ROOT_ORG, rootOrg);
		filters.put(PIDConstants.USER_ID, userIds);
		request.put(PIDConstants.FILTERS, filters);
		//request.put("query", "");
		pidRequestMap.put("request", request);
		
		try

		{

		/*	List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://" + serverConfig.getPidIp()
					+ ":" + serverConfig.getPidPort().toString() + "/user/multi-fetch/wid", pidRequestMap,
					List.class);*/
			
			HttpEntity<Map<String, Object>> requestEnty = new HttpEntity<>(pidRequestMap, headers);
			SunbirdApiResp pidResponse = restTemplate.postForObject(url,
					requestEnty, SunbirdApiResp.class);
			

			/*for (Map<String, Object> record : pidResponse) {
				if (record.get("wid") != null && userIds.contains(record.get("wid"))) {
					result.put(record.get("wid").toString(), record);
				}
			}*/
			
			if (pidResponse != null && "OK".equalsIgnoreCase(pidResponse.getResponseCode())
					&& pidResponse.getResult().getResponse().getCount() >= 1) {
				for (SunbirdApiRespContent content : pidResponse.getResult().getResponse().getContent()) {
					result.put(content.getId(), content);
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationLogicError("PID ERROR: ", e);
		}
		
		SunbirdApiRespContent content = (SunbirdApiRespContent)result.get("7b13e22d-9338-4380-aa97-24245a319b91");
		assertEquals("7b13e22d-9338-4380-aa97-24245a319b91", content.getId());
	}
	
	@Test
	public void getUserDataFromUserIdTest() {
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> pidRequestMap = new HashMap<>();
		Map<String, Object> request = new HashMap<String, Object>();
		Map<String, String> filters = new HashMap<String, String>();
		filters.put(PIDConstants.ROOT_ORG, "");
		filters.put(PIDConstants.USER_ID, "");
		request.put(PIDConstants.FILTERS, filters);
		pidRequestMap.put("request", request);
		
		try {
			/*List<Map<String, Object>> pidResponse = restTemplate.postForObject(
					"http://" + serverConfig.getPidIp() + ":" + serverConfig.getPidPort().toString() + "/user",
					pidRequestMap, List.class);*/
			HttpEntity<Map<String, Object>> requestEnty = new HttpEntity<>(pidRequestMap, headers);
			SunbirdApiResp pidResponse = restTemplate.postForObject(url,
					requestEnty, SunbirdApiResp.class);
			

			/*if (!pidResponse.isEmpty()) {
				Map<String, Object> record = pidResponse.get(0);
				if (record.get("wid") != null && record.get("wid").equals(userId)) {
					result.put(record.get("wid").toString(), record);

				}
			}*/
			if (pidResponse != null && "OK".equalsIgnoreCase(pidResponse.getResponseCode())
					&& pidResponse.getResult().getResponse().getCount() >= 1) {
				SunbirdApiRespContent content = pidResponse.getResult().getResponse().getContent().get(0);
				result.put(content.getId(), content);
			}
			

		} catch (Exception e) {
			throw new ApplicationLogicError("PID ERROR: ", e);
		}
		
		SunbirdApiRespContent content = (SunbirdApiRespContent)result.get("7b13e22d-9338-4380-aa97-24245a319b91");
		
		assertEquals("7b13e22d-9338-4380-aa97-24245a319b91", content.getId());
		
	}
	

}
