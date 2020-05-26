/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.contentsource.postgres.projection;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

public interface ContentSourceProj {

	
	@Value("#{target.key.sourceShortName}")
	public String getSourceShortName();
	
	public String getSourceName();
	
	public String getRegistrationUrl();
	
	public Boolean getProgressProvided();
	
	public Date getLicenseExpiresOn();
	
	public String getSourceUrl();
	
	public Boolean getRegistrationProvided();
	
	
}
