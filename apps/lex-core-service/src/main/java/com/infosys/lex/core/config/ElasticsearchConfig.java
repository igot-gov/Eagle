/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.core.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.infosys.lex.common.util.DatabaseProperties;

@Configuration
public class ElasticsearchConfig {

	@Autowired
	DatabaseProperties dbProperties;

	@Bean(destroyMethod = "close")
	public RestHighLevelClient restClient() {

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(dbProperties.getElasticUser(), dbProperties.getElasticPassword()));

		String[] elasticIps = dbProperties.getElasticIp().toString().split(",");
		String[] elasticPorts=dbProperties.getElasticPort().toString().split(",");

		System.out.println("elastic ips are initialized with ips:"+dbProperties.getElasticIp().toString());
		System.out.println("elastic ports are initialized with ports:"+dbProperties.getElasticPort().toString());
		
		List<HttpHost> httpHosts = new ArrayList<>();
		for(int i=0;i<elasticIps.length;i++) {
			HttpHost httpHost = new HttpHost(elasticIps[i],Integer.parseInt(elasticPorts[i]));
			httpHosts.add(httpHost);
		}
		
		RestClientBuilder builder = RestClient
				.builder(httpHosts.toArray(new HttpHost[0]))
				.setHttpClientConfigCallback(
						httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
				.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectionRequestTimeout(0).setConnectTimeout(5000)
								.setSocketTimeout(5000);
					}
				}).setMaxRetryTimeoutMillis(15000);
;

		RestHighLevelClient client = new RestHighLevelClient(builder);

		return client;	

	}

}
