package com.infosys.lex.ext.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

	@Value("${elasticsearch.host}")
	String esHost;

	@Value("${elasticsearch.port}")
	String esPort;

	@Value("${elasticsearch.user}")
	String esUser;

	@Value("${elasticsearch.password}")
	String esPassword;

	@Bean(destroyMethod = "close")
	public RestHighLevelClient restClient() {

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esUser, esPassword));

		RestClientBuilder builder = RestClient.builder(new HttpHost(esHost, Integer.parseInt(esPort)))
				.setHttpClientConfigCallback(
						httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

		RestHighLevelClient client = new RestHighLevelClient(builder);

		return client;
	}
}