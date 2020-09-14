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
package com.infosys.lex.ext.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LexServerProperties {

	@Value("${search_service_url}")
	private String searchServiceUrl;

	@Value("${SB_EXT_IP}")
	private String sbExtIp;

	@Value("${SB_EXT_PORT}")
	private String sbExtPort;

	@Value("${neo4j.get.url}")
	private String neo4jUrl;

	@Value("${appUrl.authoring}")
	private String authroingUrl;

	@Value("${content.service.url}")
	private String contentServiceUrl;

	@Value("${LEX_CORE_SERVICE}")
	private String lexCoreSerivce;

	@Value("${contentUrl.part}")
	private String contentUrlPart;

	@Value("${pid.service.ip}")
	private String pidIp;

	@Value("${pid.service.port}")
	private Integer pidPort;

	@Value("${root_org.cdn.mapping}")
	private String domainMapping;

	@Value("${notification.service}")
	private String notificationService;

	public String getNotificationService() {
		return notificationService;
	}

	public String getPidIp() {
		return pidIp;
	}

	public String getDomainMapping() {
		return domainMapping;
	}

	public void setDomainMapping(String domainMapping) {
		this.domainMapping = domainMapping;
	}

	public void setPidIp(String pidIp) {
		this.pidIp = pidIp;
	}

	public Integer getPidPort() {
		return pidPort;
	}

	public void setPidPort(Integer pidPort) {
		this.pidPort = pidPort;
	}

	public String getContentUrlPart() {
		return contentUrlPart;
	}

	public String getContentServiceUrl() {
		return contentServiceUrl;
	}

	public String getAuthroingUrl() {
		return authroingUrl;
	}

	public String getNeo4jUrl() {
		return neo4jUrl;
	}

	public String getSearchServiceUrl() {
		return searchServiceUrl;
	}

	public String getSbExtIp() {
		return sbExtIp;
	}

	public String getSbExtPort() {
		return sbExtPort;
	}

	public void setSearchServiceUrl(String searchServiceUrl) {
		this.searchServiceUrl = searchServiceUrl;
	}

	public String getLexCoreSerivce() {
		return lexCoreSerivce;
	}

	public void setLexCoreSerivce(String lexCoreSerivce) {
		this.lexCoreSerivce = lexCoreSerivce;
	}
}
