/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.common.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.bodhi.repo.AppConfig;
import com.infosys.lex.common.bodhi.repo.AppConfigPrimaryKey;
import com.infosys.lex.common.bodhi.repo.AppConfigRepository;
import com.infosys.lex.common.constants.JsonKey;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

	@Autowired
	AppConfigRepository appConfigRepo;

	@Autowired
	LexServerProperties serverProps;

	@Autowired
	private ServletContext servletContext;
	
	private static String  infosysInstanceRootOrg = JsonKey.INFOSYS_ROOTORG;

	
	@Override
	public void getAccessToken(String prefix) throws Exception {

		
		String refreshTokenKey = prefix + "_refresh_token";
		String accessTokenKey = prefix + "_access_token";
		String tokenExpiresOnKey = prefix + "_token_expires_on";
		String refreshToken = "";
		String accessToken = "";
		Date expirationDate = new Date(0);
		


		if (servletContext.getAttribute(accessTokenKey) == null
				|| ((Date) servletContext.getAttribute(tokenExpiresOnKey))
						.compareTo(Calendar.getInstance().getTime()) <= 0) {

			List<String> keys = new ArrayList<>(Arrays.asList(refreshTokenKey, accessTokenKey, tokenExpiresOnKey));
			
			List<Map<String,Object>> properties = appConfigRepo.findAllByPrimaryKeyRootOrgAndPrimaryKeyKeyIn(infosysInstanceRootOrg ,keys);
			for (Map<String,Object> m : properties) {

				if (m.get("key").toString().equals(tokenExpiresOnKey)) {
					expirationDate = new Date(Long.parseLong(m.get("value").toString()) * 1000);
				} else if (m.get("key").toString().equals(refreshTokenKey)) {
					refreshToken = m.get("value").toString();
				} else if (m.get("key").toString().equals(accessTokenKey)) {
					accessToken = m.get("value").toString();
				}
			}


			
			servletContext.setAttribute(tokenExpiresOnKey, expirationDate);

			if (expirationDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

				accessToken = this.getAccessTokenFromRefreshToken(refreshToken, refreshTokenKey, accessTokenKey,
						tokenExpiresOnKey);
			}

			servletContext.setAttribute(accessTokenKey, accessToken);

		}
	}

	@SuppressWarnings("unchecked")
	private String getAccessTokenFromRefreshToken(String refresh_token, String refreshTokenKey, String accessTokenKey,
			String tokenExpiresOnKey) throws Exception {

		String access_token = "";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// CloseableHttpClient httpClient = this.getAuthorizedClient();
		HttpPost postRequest = new HttpPost("https://login.microsoftonline.com/common/oauth2/token");

		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("client_id", serverProps.getClientId()));
		postParameters.add(new BasicNameValuePair("refresh_token", refresh_token));
		postParameters.add(new BasicNameValuePair("redirect_uri", "https://lex-dev.infosysapps.com/"));
		postParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
		postParameters.add(new BasicNameValuePair("client_secret", serverProps.getClientSecret()));
		postRequest.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
		HttpResponse response = httpClient.execute(postRequest);

		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

		String output = "";
		Map<String, Object> map = new HashMap<String, Object>();
		while ((output = br.readLine()) != null) {
			// System.out.println(output);
			map.putAll(new ObjectMapper().readValue(output, Map.class));
		}
		if (!map.containsKey("error_description"))
			access_token = map.get("access_token").toString();
		else {
			throw new Exception("Refresh Token Expired.");
		}
		postRequest.releaseConnection();
		httpClient.close();

		// insert tokens in cassandra
		List<AppConfig> entities = new ArrayList<>();
		entities.add(new AppConfig(new AppConfigPrimaryKey(infosysInstanceRootOrg, refreshTokenKey), map.get("refresh_token").toString(),"LHub Authentication Refresh token"));
		entities.add(new AppConfig(new AppConfigPrimaryKey(infosysInstanceRootOrg, accessTokenKey), map.get("access_token").toString(),"Lhub Authentication access token"));
		entities.add(new AppConfig(new AppConfigPrimaryKey(infosysInstanceRootOrg, tokenExpiresOnKey),  map.get("expires_on").toString(),"Lhub Authentication token expires on "));
		appConfigRepo.saveAll(entities);

		servletContext.setAttribute(tokenExpiresOnKey,
				new Date(Long.parseLong(map.get("expires_on").toString()) * 1000));

		return access_token;
	}

	@SuppressWarnings("unused")
	private CloseableHttpClient getAuthorizedClient() throws Exception {
		final String username = "";
		final String password = "";
		final String proxyUrl = "IPaddress";
		final int port = 80;
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyUrl, port),
				new UsernamePasswordCredentials(username, password));

		HttpHost myProxy = new HttpHost(proxyUrl, port);
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();

		clientBuilder.setProxy(myProxy).setDefaultCredentialsProvider(credsProvider).disableCookieManagement();
		return clientBuilder.build();
	}
}
