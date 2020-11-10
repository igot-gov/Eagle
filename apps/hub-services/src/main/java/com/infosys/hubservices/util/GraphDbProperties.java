/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GraphDbProperties {

	@Value("${neo4j.url}")
	private String neo4jHost;

	@Value("${neo4j.username}")
	private String neo4jUserName;

	@Value("${neo4j.password}")
	private String neo4jPassword;

	@Value("${neo4j.auth.enable}")
	private String neo4jAuthEnable;
	
	@Value("${neo.timeout}")
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

}
