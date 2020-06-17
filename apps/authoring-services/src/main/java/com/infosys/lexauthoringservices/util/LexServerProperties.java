package com.infosys.lexauthoringservices.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LexServerProperties {

	@Value("${content.id.prefix}")
	private String contentIdPrefix;
	
	@Value("${feature.id.prefix}")
	private String featureIdPrefix;

	@Value("${content.service.url}")
	private String contentServiceUrl;
	
	@Value("${topic.service.url}")
	private String topicServiceUrl;
	
	@Value("${access.control.url}")
	private String accessUrlPrefix;
	
	@Value("${access.control.url.user}")
	private String accessUrlPostFix;
	
	@Value("${content.service.publish.url}")
    private String contentServicePublishUrl;

	@Value("${content.service.zip.url}")
	private String contentServiceZipUrl;

	@Value("${email.service.url}")
	private String emailServiceUrl;
	
	@Value("${pid.service.ip}")
	private String pidIp;
	
	@Value("${pid.service.port}")
	private String pidPort;
	
	@Value("${contentUrl.part}")
	private String contentUrlPart;
	
	@Value("${infosys.lex.core.ip}")
	private String lexCoreIp;

	public String getLexCoreIp() {
		return lexCoreIp;
	}
	
	public String getContentUrlPart() {
		return contentUrlPart;
	}
	
	public String getPidIp() {
		return pidIp;
	}

	public String getPidPort() {
		return pidPort;
	}

	public String getFeatureIdPrefix() {
		return featureIdPrefix;
	}

	public String getAccessUrlPostFix() {
		return accessUrlPostFix;
	}

	public String getContentServiceUrl() {
		return contentServiceUrl;
	}

	public String getTopicServiceUrl() {
		return topicServiceUrl;
	}

	public String getContentIdPrefix() {
		return contentIdPrefix;
	}

	public String getAccessUrlPrefix() {
		return accessUrlPrefix;
	}

    public String getContentServicePublishUrl() {
        return contentServicePublishUrl;
    }

	public String getContentServiceZipUrl() {
		return contentServiceZipUrl;
	}

	public String getEmailServiceUrl() {
		return emailServiceUrl;
	}
}
