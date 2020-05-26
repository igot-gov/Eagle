/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.rating.bodhi.repo;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserContentRatingPrimaryKeyModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6522789124174079848L;
	
	@PrimaryKeyColumn(name = "user_id",ordinal = 1, type = PrimaryKeyType.CLUSTERED)
	private String userId;
	
	@PrimaryKeyColumn(name = "content_id",ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String contentId;
	
	@PrimaryKeyColumn(name = "root_org",ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public UserContentRatingPrimaryKeyModel( String rootOrg, String contentId,String userId) {
		this.userId = userId;
		this.contentId = contentId;
		this.rootOrg = rootOrg;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	

}
