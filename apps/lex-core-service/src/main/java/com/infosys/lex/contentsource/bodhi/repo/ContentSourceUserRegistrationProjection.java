/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.contentsource.bodhi.repo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

public interface ContentSourceUserRegistrationProjection {

	
	public Date getStartDate();
	
	public Date getEndDate();
	
	public Date getStartedOn();
	
	@Value("#{target.key.sourceShortName}")
	public String getSourceShortName();
	
	@Value("#{target.key.rootOrg}")
	public String getRootOrg();
	
	@Value("#{target.key.userId}")
	public String getUserId();
}

