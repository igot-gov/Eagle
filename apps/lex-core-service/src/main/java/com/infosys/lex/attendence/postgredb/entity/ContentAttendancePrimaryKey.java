/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.attendence.postgredb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ContentAttendancePrimaryKey implements Serializable {

	private static final long serialVersionUID = -6227399206391169619L;
	
	@Column(name = "root_org")
	private String rootOrg;
	
	@Column(name = "content_id")
	private String contentId;

	@Column(name = "user_id")
	private String userId;
	
	public ContentAttendancePrimaryKey() {
		super();
	}

	public ContentAttendancePrimaryKey(String rootOrg, String contentId, String userId) {
		super();
		this.rootOrg = rootOrg;
		this.contentId = contentId;
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "ContentAttendancePrimaryKey [rootOrg=" + rootOrg + ", contentId=" + contentId + ", userId=" + userId
				+ "]";
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
