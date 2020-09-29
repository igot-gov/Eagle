/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConnectionProperties {

	@Value("${es.host}")
	private String esHost;

	@Value("${es.port}")
	private String esPort;

	@Value("${es.username}")
	private String esUser;

	@Value("${es.password}")
	private String esPassword;

	@Value("${es.profile.index}")
	private String esProfileIndex;

	@Value("${es.profile.index.type}")
	private String esProfileIndexType;

	@Value("${es.profile.source.fields}")
	private String[] esProfileSourceFields;

	public String getEsProfileIndex() {
		return esProfileIndex;
	}

	public String getEsProfileIndexType() {
		return esProfileIndexType;
	}
	public String[] getEsProfileSourceFields() {
		return esProfileSourceFields;
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
