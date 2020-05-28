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
package com.infosys.lex.common.util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LexServerProperties {

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

	@Value("${sbext.service.port}")
	private String sbextPort;

	@Value("${smtp.host}")
	private String smtpHost;

	@Value("${smtp.port}")
	private String smtpPort;

	@Value("${content.service.port}")
	private String contentServicePort;

	@Value("${pyeval.server.ip}")
	private String pyeValServiceIp;

	@Value("${pyeval.server.port}")
	private String pyeValServicePort;

	@Value("${pyeval.service.name}")
	private String pyeValServiceName;

	@Value("${iap.server.ip}")
	private String iapServiceIp;

	@Value("${iap.server.port}")
	private String iapServicePort;

	@Value("${iap.verification.service}")
	private String iapVerificationService;

	@Value("${log.access.key}")
	private String logAccessKey;

	@Value("${client.id}")
	private String clientId;

	@Value("${client.secret}")
	private String clientSecret;

	@Value("${lhub.url}")
	private String lhubUrl;

	@Value("${lhub.certification.url}")
	private String lhubCertificationUrl;

	@Value("${iap.submission.url}")
	private String iapSubmissonsUrl;

	@Value("${iap.submission.clientId}")
	private String iapSubmissonsClientId;

	@Value("${iap.submission.clientSecret}")
	private String iapSubmissonsClientSecret;

	@Value("${enable.realtime}")
	private Boolean enableRealTime;

	@Value("${content.service.host}")
	private String contentServiceHost;

	@Value("${lhub.auth.clientid}")
	private String lhubAthClientId;

	@Value("${iap.certification.clientId}")
	private String iapCertificationClientId;

	@Value("${iap.certification.url}")
	private String iapCertificationUrl;


	@Value("${iap.certification.clientSecret}")
	private String iapCertificationClientSecret;
	
	@Value("${com.infosys.root-org}")
	private String defAccessPathRootOrg;

	@Value("${com.infosys.org}")
	private String defAccessPathOrg;

//	@Value("${user.datasource}")
//	private String userDataSource;

	@Value("${feedback.allow.selfReply}")
	private Boolean selfReplyAllow;

	@Value("${pid.service.ip}")
	private String pidIp;

	@Value("${pid.service.port}")
	private Integer pidPort;

	@Value("${auth.service.host}")
	private String authServiceHost;

	@Value("${auth.service.port}")
	private String authServicePort;

	@Value("${certification.retry.gap.days}")
	private Integer certificationRetryGapInDays;
	
	
	@Value("${java.eval.server.host}")
	private String javaEvalServerHost;
	
	@Value("${java.eval.server.port}")
	private String javaEvalServerPort;
	
	@Value("${java.eval.endpoint}")
	private String javaEvalEndpoint;
	
	
	

	public Integer getCertificationRetryGapInDays() {
		return certificationRetryGapInDays;
	}

	public void setCertificationRetryGapInDays(Integer certificationDaysGap) {
		this.certificationRetryGapInDays = certificationDaysGap;
	}

	public String getAuthServiceHost() {
		return authServiceHost;
	}

	public void setAuthServiceHost(String authServiceHost) {
		this.authServiceHost = authServiceHost;
	}

	public String getAuthServicePort() {
		return authServicePort;
	}

	public void setAuthServicePort(String authServicePort) {
		this.authServicePort = authServicePort;
	}

	@Value("${notification.service.ip}")
	private String notifIp;

	@Value("${notification.service.port}")
	private String notifPort;

	public String getNotifIp() {
		return notifIp;
	}

	public String getNotifPort() {
		return notifPort;
	}

	public void setNotifIp(String notifIp) {
		this.notifIp = notifIp;
	}

	public void setNotifPort(String notifPort) {
		this.notifPort = notifPort;
	}

	public Integer getPidPort() {
		return pidPort;
	}

	public void setPidPort(Integer pidPort) {
		this.pidPort = pidPort;
	}

	public String getPidIp() {
		return pidIp;
	}

	public void setPidIp(String pidIp) {
		this.pidIp = pidIp;
	}

	public Boolean getSelfReplyAllow() {
		return selfReplyAllow;
	}

	public void setSelfReplyAllow(Boolean selfReplyAllow) {
		this.selfReplyAllow = selfReplyAllow;
	}

	public String getIapCertificationClientId() {
		return iapCertificationClientId;
	}

	public String getDefAccessPathRootOrg() {
		return defAccessPathRootOrg;
	}

	public void setDefAccessPathRootOrg(String defAccessPathRootOrg) {
		this.defAccessPathRootOrg = defAccessPathRootOrg;
	}

	public String getDefAccessPathOrg() {
		return defAccessPathOrg;
	}

	public void setDefAccessPathOrg(String defAccessPathOrg) {
		this.defAccessPathOrg = defAccessPathOrg;
	}

//substitute based on requirement
//substitute based on requirement
//	}

	public String getIapCertificationUrl() {
		return iapCertificationUrl;
	}

	public void setIapCertificationUrl(String iapCertificationUrl) {
		this.iapCertificationUrl = iapCertificationUrl;
	}

	public String getIapCertificationClientSecret() {
		return iapCertificationClientSecret;
	}

	public void setIapCertificationClientSecret(String iapCertificationClientSecret) {
		this.iapCertificationClientSecret = iapCertificationClientSecret;
	}

	@Value("${sbext.service.host}")
	private String sbextServiceHost;

	public String getContentServiceHost() {
		return contentServiceHost;
	}

	public void setContentServiceHost(String contentServiceHost) {
		this.contentServiceHost = contentServiceHost;
	}

	public String getSbextServiceHost() {
		return sbextServiceHost;
	}

	public void setSbextServiceHost(String sbextServiceHost) {
		this.sbextServiceHost = sbextServiceHost;
	}

	public Boolean getEnableRealTime() {
		return enableRealTime;
	}

	public void setEnableRealTime(Boolean enableRealTime) {
		this.enableRealTime = enableRealTime;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getContentServicePort() {
		return contentServicePort;
	}

	public void setContentServicePort(String contentServicePort) {
		this.contentServicePort = contentServicePort;
	}

	public String getIapVerificationService() {
		return iapVerificationService;
	}

	public void setIapVerificationService(String iapVerificationService) {
		this.iapVerificationService = iapVerificationService;
	}

	public String getLogAccessKey() {
		return logAccessKey;
	}

	public void setLogAccessKey(String logAccessKey) {
		this.logAccessKey = logAccessKey;
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

	public String getSbextPort() {
		return sbextPort;
	}

	public void setSbextPort(String sbextPort) {
		this.sbextPort = sbextPort;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getBodhiContentPort() {
		return contentServicePort;
	}

	public void setBodhiContentPort(String bodhiContentPort) {
		this.contentServicePort = bodhiContentPort;
	}

	public String getPyeValServiceIp() {
		return pyeValServiceIp;
	}

	public void setPyeValServiceIp(String pyeValServiceIp) {
		this.pyeValServiceIp = pyeValServiceIp;
	}

	public String getPyeValServicePort() {
		return pyeValServicePort;
	}

	public void setPyeValServicePort(String pyeValServicePort) {
		this.pyeValServicePort = pyeValServicePort;
	}

	public String getPyeValServiceName() {
		return pyeValServiceName;
	}

	public void setPyeValServiceName(String pyeValServiceName) {
		this.pyeValServiceName = pyeValServiceName;
	}

	public String getIapServiceIp() {
		return iapServiceIp;
	}

	public void setIapServiceIp(String iapServiceIp) {
		this.iapServiceIp = iapServiceIp;
	}

	public String getIapServicePort() {
		return iapServicePort;
	}

	public void setIapServicePort(String iapServicePort) {
		this.iapServicePort = iapServicePort;
	}

	public String getIapServiceName() {
		return iapVerificationService;
	}

	public void setIapServiceName(String iapServiceName) {
		this.iapVerificationService = iapServiceName;
	}

	public String getLhubUrl() {
		return lhubUrl;
	}

	public void setLhubUrl(String lhubUrl) {
		this.lhubUrl = lhubUrl;
	}

	public String getIapSubmissonsUrl() {
		return iapSubmissonsUrl;
	}

	public void setIapSubmissonsUrl(String iapSubmissonsUrl) {
		this.iapSubmissonsUrl = iapSubmissonsUrl;
	}

	public String getIapSubmissonsClientId() {
		return iapSubmissonsClientId;
	}

	public void setIapSubmissonsClientId(String iapSubmissonsClientId) {
		this.iapSubmissonsClientId = iapSubmissonsClientId;
	}

	public String getIapSubmissonsClientSecret() {
		return iapSubmissonsClientSecret;
	}

	public void setIapSubmissonsClientSecret(String iapSubmissonsClientSecret) {
		this.iapSubmissonsClientSecret = iapSubmissonsClientSecret;
	}

	public String getLhubCertificationUrl() {
		return lhubCertificationUrl;
	}

	public void setLhubCertificationUrl(String lhubCertificationUrl) {
		this.lhubCertificationUrl = lhubCertificationUrl;
	}

	public String getLhubAthClientId() {
		return lhubAthClientId;
	}

	public void setLhubAthClientId(String lhubAthClientId) {
		this.lhubAthClientId = lhubAthClientId;
	}

//	@Override
//	public String toString() {
//		return "LexServerProperties [maxFileSize=" + maxFileSize + ", maxRequestSize=" + maxRequestSize
//				+ ", connectionTimeout=" + connectionTimeout + ", maxThreads=" + maxThreads + ", minSpareThreads="
//				+ minSpareThreads + ", sbextPort=" + sbextPort + ", smtpHost=" + smtpHost + ", smtpPort=" + smtpPort
//				+ ", contentServicePort=" + contentServicePort + ", pyeValServiceIp=" + pyeValServiceIp
//				+ ", pyeValServicePort=" + pyeValServicePort + ", pyeValServiceName=" + pyeValServiceName
//				+ ", iapServiceIp=" + iapServiceIp + ", iapServicePort=" + iapServicePort + ", iapVerificationService="
//substitute based on requirement
//substitute based on requirement
//substitute based on requirement
//substitute based on requirement
//				+ ", enableRealTime=" + enableRealTime + ", contentServiceHost=" + contentServiceHost
//substitute based on requirement
//substitute based on requirement
//substitute based on requirement
//				+ ", defAccessPathOrg=" + defAccessPathOrg + ", selfReplyAllow=" + selfReplyAllow + ", pidIp=" + pidIp
//				+ ", pidPort=" + pidPort + ", authServiceHost=" + authServiceHost + ", authServicePort="
//				+ authServicePort + ", certificationRetryGapInDays=" + certificationRetryGapInDays + ", notifIp="
//				+ notifIp + ", notifPort=" + notifPort + ", sbextServiceHost=" + sbextServiceHost + "]";
//	}

	;

//	public String getUserDataSource() {
//		return userDataSource;
//	}
//
//	public void setUserDataSource(String userDataSource) {
//		this.userDataSource = userDataSource;
//	}
	

	public String getJavaEvalServerHost() {
		return javaEvalServerHost;
	}

	public void setJavaEvalServerHost(String javaEvalServerHost) {
		this.javaEvalServerHost = javaEvalServerHost;
	}

	public String getJavaEvalServerPort() {
		return javaEvalServerPort;
	}

	public void setJavaEvalServerPort(String javaEvalServerPort) {
		this.javaEvalServerPort = javaEvalServerPort;
	}

	public String getJavaEvalEndpoint() {
		return javaEvalEndpoint;
	}

	public void setJavaEvalEndpoint(String javaEvalEndpoint) {
		this.javaEvalEndpoint = javaEvalEndpoint;
	}
	
	
	


	@Override
	public String toString() {
		return "LexServerProperties [serverPort=" + serverPort + ", maxFileSize=" + maxFileSize + ", maxRequestSize="
				+ maxRequestSize + ", connectionTimeout=" + connectionTimeout + ", maxThreads=" + maxThreads
				+ ", minSpareThreads=" + minSpareThreads + ", sbextPort=" + sbextPort + ", smtpHost=" + smtpHost
				+ ", smtpPort=" + smtpPort + ", contentServicePort=" + contentServicePort + ", pyeValServiceIp="
				+ pyeValServiceIp + ", pyeValServicePort=" + pyeValServicePort + ", pyeValServiceName="
				+ pyeValServiceName + ", iapServiceIp=" + iapServiceIp + ", iapServicePort=" + iapServicePort
				+ ", iapVerificationService=" + iapVerificationService + ", logAccessKey=" + logAccessKey
				+ ", clientId=" + clientId + ", clientSecret=" + clientSecret + ", lhubUrl=" + lhubUrl
				+ ", lhubCertificationUrl=" + lhubCertificationUrl + ", iapSubmissonsUrl=" + iapSubmissonsUrl
				+ ", iapSubmissonsClientId=" + iapSubmissonsClientId + ", iapSubmissonsClientSecret="
				+ iapSubmissonsClientSecret + ", enableRealTime=" + enableRealTime + "]";
	}


}
