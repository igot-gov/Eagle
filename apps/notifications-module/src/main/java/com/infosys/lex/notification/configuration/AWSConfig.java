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

package com.infosys.lex.notification.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.infosys.lex.notification.properties.ApplicationServerProperties;

@Configuration
public class AWSConfig {

	@Autowired
	ApplicationServerProperties applicationServerProperties;

	/**
	 * @return
	 */
	@Bean
	public AWSCredentials awsCredentials() {
		return new BasicAWSCredentials(applicationServerProperties.getAwsAccessKey(),
				applicationServerProperties.getAwsSecretKey());
	}

	/**
	 * @return
	 */
//	@Bean
//	public AmazonSimpleEmailService emailClient() {
//
//		if (applicationServerProperties.getAwsLocal())
//			return AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1)
//					.withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
//					.withClientConfiguration(new ClientConfiguration().withProxyUsername("saurav.bhasin")
//							.withProxyPassword("Avengers1234")
//							.withProxyHost(applicationServerProperties.getClientConfigurationProxyHost())
//							.withProxyPort(applicationServerProperties.getClientConfigurationProxyPort()))
//					.build();
//		else
//			return AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1)
//					.withCredentials(new AWSStaticCredentialsProvider(awsCredentials())).build();
//	}

	/**
	 * @return
	 */
	@Bean
	public AmazonSNS snsClient() {// US_EAST_1
			return AmazonSNSClientBuilder.standard().withRegion(Regions.AP_SOUTH_1)
					.withCredentials(new AWSStaticCredentialsProvider(awsCredentials())).build();
	}

}
