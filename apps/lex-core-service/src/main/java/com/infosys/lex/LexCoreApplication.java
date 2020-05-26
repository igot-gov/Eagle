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
package com.infosys.lex;

import java.util.Properties;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;

@EnableCaching
@EnableScheduling
@ServletComponentScan
@SpringBootApplication
public class LexCoreApplication {

	/**
	 * Runs The application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Properties props = System.getProperties();
		props.setProperty("es.set.netty.runtime.available.processors", "false");
		SpringApplication.run(LexCoreApplication.class, args);
	}

	/**
	 * Initializes the rest template
	 * 
	 * @return
	 * @throws Exception
	 */

	@Bean
	public RestTemplate restTemplate() throws Exception {

		return new RestTemplate(getClientHttpRequestFactory());
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {

		int timeout = 5000;
              RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                           .setSocketTimeout(timeout).build();
              CloseableHttpClient client = HttpClientBuilder.create().setMaxConnTotal(2000).setMaxConnPerRoute(500)
                           .setDefaultRequestConfig(config).build();
              return new HttpComponentsClientHttpRequestFactory(client);
	}
	
	
	@Bean
	public RestTemplate restTemplateForCertification() throws Exception {

		return new RestTemplate(getClientHttpRequestFactoryForCertification());
	}

	private ClientHttpRequestFactory getClientHttpRequestFactoryForCertification() {

		int timeout = 10000;
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
				.setSocketTimeout(timeout).build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return new HttpComponentsClientHttpRequestFactory(client);
	}

	/**
	 * Dispatcher for invalid urls and handlers
	 * 
	 * @return
	 */
	@Bean
	public DispatcherServlet dispatcherServlet() {
		DispatcherServlet ds = new DispatcherServlet();
		ds.setThrowExceptionIfNoHandlerFound(true);
		return ds;
	}

	/**
	 * sets message source to ValidationMessages.properties
	 * 
	 * @return
	 */
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:ValidationMessages");
		messageSource.setCacheSeconds(10); // reload messages every 10 seconds
		return messageSource;
	}

}
