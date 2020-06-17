package com.infosys.lexauthoringservices.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.infosys.lexauthoringservices.util.DbProperties;

@Configuration
public class EsConfig {

	@Autowired
	DbProperties dbProps;

	@Bean(destroyMethod = "close")
	public RestHighLevelClient restHighLevelClient() {

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(dbProps.getEsUser(), dbProps.getEsPassword()));

		RestClientBuilder builder = RestClient
				.builder(new HttpHost(dbProps.getEsHost(), Integer.parseInt(dbProps.getEsPort())))
				.setHttpClientConfigCallback(
						httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

		RestHighLevelClient client = new RestHighLevelClient(builder);

		return client;
	}
}