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

package com.infosys.lex.notification.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationServerProperties {

	@Value("${pid.service.url}")
	private String pidServiceUrl;

	@Value("${kafka.bootstrapAddress}")
	private String kafkabootstrapAddress;

	@Value("${notification.admin.rootOrg}")
	private String notificationAdminRootOrg;

	@Value("${notification.admin.org}")
	private String notificationAdminOrg;

	@Value("${kakfa.offset.reset.value}")
	private String kafkaOffsetResetValue;

	public String getKafkaOffsetResetValue() {
		return kafkaOffsetResetValue;
	}

	public void setKafkaOffsetResetValue(String kafkaOffsetResetValue) {
		this.kafkaOffsetResetValue = kafkaOffsetResetValue;
	}

	@Value("${server.port}")
	private String serverPort;

	@Value("${spring.servlet.multipart.max-file-size}")
	private String maxFileSize;

	@Value("${spring.servlet.multipart.max-request-size}")
	private String maxRequestSize;

	@Value("${server.connection-timeout}")
	private String connectionTimeout;

	@Value("${server.tomcat.max-threads}")
	private String maxThreads;

	@Value("${server.tomcat.min-spare-threads}")
	private String minSpareThreads;

	@Value("${appserver.ip}")
	private String appServerIp;

	@Value("${sbext.service.port}")
	private String sbextPort;

	@Value("${content.service.port}")
	private String contentServicePort;

	@Value("${log.access.key}")
	private String logAccessKey;

	@Value("${aws_access_key}")
	private String awsAccessKey;

	@Value("${aws_access_secret}")
	private String awsSecretKey;

	@Value("${spring.data.cassandra.username}")
	private String cassandraUserName;

	@Value("${spring.data.cassandra.password}")
	private String cassandraPassword;

	@Value("${notification.markAsRead.count}")
	private Integer markAllAsReadCount;

	@Value("${kafka.consumer.filter.days}")
	private Integer kafkaConsumerFilterDays;

	@Value("${mail.smtp.connectiontimeout}")
	private Integer smtpConnectionTimeout;

	@Value("${kafka.max.poll.interval.ms}")
	private Integer kafkaMaxPollInterval;

	public Integer getKafkaMaxPollRecords() {
		return kafkaMaxPollRecords;
	}

	public void setKafkaMaxPollRecords(Integer kafkaMaxPollRecords) {
		this.kafkaMaxPollRecords = kafkaMaxPollRecords;
	}

	@Value("${kafka.max.poll.records}")
	private Integer kafkaMaxPollRecords;

	public Integer getKafkaMaxPollInterval() {
		return kafkaMaxPollInterval;
	}

	public void setKafkaMaxPollInterval(Integer kafkaMaxPollInterval) {
		this.kafkaMaxPollInterval = kafkaMaxPollInterval;
	}

	public Integer getSmtpConnectionTimeout() {
		return smtpConnectionTimeout;
	}

	public void setSmtpConnectionTimeout(Integer smtpConnectionTimeout) {
		this.smtpConnectionTimeout = smtpConnectionTimeout;
	}

	public Integer getMarkAllAsReadCount() {
		return markAllAsReadCount;
	}

	public void setMarkAllAsReadCount(Integer markAllAsReadCount) {
		this.markAllAsReadCount = markAllAsReadCount;
	}

	public String getPidServiceUrl() {
		return pidServiceUrl;
	}

	public void setPidServiceUrl(String pidServiceUrl) {
		this.pidServiceUrl = pidServiceUrl;
	}

	public String getKafkabootstrapAddress() {
		return kafkabootstrapAddress;
	}

	public void setKafkabootstrapAddress(String kafkabootstrapAddress) {
		this.kafkabootstrapAddress = kafkabootstrapAddress;
	}

	public void setNotificationAdminOrg(String notificationAdminOrg) {
		this.notificationAdminOrg = notificationAdminOrg;
	}

	public String getNotificationAdminRootOrg() {
		return notificationAdminRootOrg;
	}

	public void setNotificationAdminRootOrg(String notificationAdminRootOrg) {
		this.notificationAdminRootOrg = notificationAdminRootOrg;
	}

	public String getNotificationAdminOrg() {
		return notificationAdminOrg;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(String maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public String getMaxRequestSize() {
		return maxRequestSize;
	}

	public void setMaxRequestSize(String maxRequestSize) {
		this.maxRequestSize = maxRequestSize;
	}

	public String getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(String connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public String getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(String maxThreads) {
		this.maxThreads = maxThreads;
	}

	public String getMinSpareThreads() {
		return minSpareThreads;
	}

	public void setMinSpareThreads(String minSpareThreads) {
		this.minSpareThreads = minSpareThreads;
	}

	public String getAppServerIp() {
		return appServerIp;
	}

	public void setAppServerIp(String appServerIp) {
		this.appServerIp = appServerIp;
	}

	public String getSbextPort() {
		return sbextPort;
	}

	public void setSbextPort(String sbextPort) {
		this.sbextPort = sbextPort;
	}

	public String getContentServicePort() {
		return contentServicePort;
	}

	public void setContentServicePort(String contentServicePort) {
		this.contentServicePort = contentServicePort;
	}

	public String getLogAccessKey() {
		return logAccessKey;
	}

	public void setLogAccessKey(String logAccessKey) {
		this.logAccessKey = logAccessKey;
	}

	public String getAwsAccessKey() {
		return awsAccessKey;
	}

	public void setAwsAccessKey(String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}

	public String getAwsSecretKey() {
		return awsSecretKey;
	}

	public void setAwsSecretKey(String awsSecretKey) {
		this.awsSecretKey = awsSecretKey;
	}

	public String getCassandraUserName() {
		return cassandraUserName;
	}

	public String getCassandraPassword() {
		return cassandraPassword;
	}

	public void setCassandraUserName(String cassandraUserName) {
		this.cassandraUserName = cassandraUserName;
	}

	public void setCassandraPassword(String cassandraPassword) {
		this.cassandraPassword = cassandraPassword;
	}

	public Integer getKafkaConsumerFilterDays() {
		return kafkaConsumerFilterDays;
	}

	public void setKafkaConsumerFilterDays(Integer kafkaConsumerFilterDays) {
		this.kafkaConsumerFilterDays = kafkaConsumerFilterDays;
	}

}
