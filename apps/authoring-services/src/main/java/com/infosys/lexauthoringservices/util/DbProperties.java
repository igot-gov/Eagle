package com.infosys.lexauthoringservices.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DbProperties {

	@Value("${neo4j.url}")
	private String neo4jHost;

	@Value("${neo4j.username}")
	private String neo4jUserName;

	@Value("${neo4j.password}")
	private String neo4jPassword;

	@Value("${neo4j.auth.enable}")
	private String neo4jAuthEnable;

	@Value("${infosys.es.host}")
	private String esHost;

	@Value("${infosys.es.port}")
	private String esPort;

	@Value("${infosys.es.username}")
	private String esUser;

	@Value("${infosys.es.password}")
	private String esPassword;
	
	@Value("${infosys.neo.timeout}")
	private Long neoTimeout;

	public Long getNeoTimeout() {
		return neoTimeout;
	}
	public String getNeo4jHost() {
		return neo4jHost;
	}

	public String getNeo4jUserName() {
		return neo4jUserName;
	}

	public String getNeo4jPassword() {
		return neo4jPassword;
	}

	public String getNeo4jAuthEnable() {
		return neo4jAuthEnable;
	}

	public String getEsHost() {
		return esHost;
	}

	public String getEsPort() {
		return esPort;
	}

	public String getEsUser() {
		return esUser;
	}

	public String getEsPassword() {
		return esPassword;
	}
}
