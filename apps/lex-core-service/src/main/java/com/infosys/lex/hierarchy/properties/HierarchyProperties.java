/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.hierarchy.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HierarchyProperties {
	
	@Value("${sbext.service.host}")
	private String sbExtIp;
	
	@Value("${sbext.service.port}")
	private String sbExtPort;

	public String getSbExtIp() {
		return sbExtIp;
	}

	public String getSbExtPort() {
		return sbExtPort;
	}


}
