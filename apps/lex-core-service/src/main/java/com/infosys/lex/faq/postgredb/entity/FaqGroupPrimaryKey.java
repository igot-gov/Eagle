/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.postgredb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FaqGroupPrimaryKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "root_org")
	private String rootOrg;

	@Column(name = "language")
	private String language;

	@Column(name = "group_id")
	private String groupId;

	public FaqGroupPrimaryKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FaqGroupPrimaryKey(String rootOrg, String language, String groupId) {
		super();
		this.rootOrg = rootOrg;
		this.language = language;
		this.groupId = groupId;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String toString() {
		return "FaqGroupPrimaryKey [rootOrg=" + rootOrg + ", language=" + language + ", groupId=" + groupId + "]";
	}

}
