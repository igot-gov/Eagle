/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.bodhi.repo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommonGoalLangKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -798824243888954396L;

	@Column(name = "root_org", columnDefinition = "VARCHAR")
	private String rootOrg;

	@Column(name = "id", columnDefinition = "VARCHAR")
	private String id;

	@Column(name = "language", columnDefinition = "VARCHAR")
	private String language;

	@Column(name = "version")
	private Float version;

	public String getRootOrg() {
		return rootOrg;
	}

	public String getId() {
		return id;
	}

	public String getLanguage() {
		return language;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public CommonGoalLangKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CommonGoalLangKey(String rootOrg, String id, String language, Float version) {
		super();
		this.rootOrg = rootOrg;
		this.id = id;
		this.language = language;
		this.version = version;
	}

	@Override
	public String toString() {
		return "CommonGoalLangKey [rootOrg=" + rootOrg + ", id=" + id + ", language=" + language + ", version="
				+ version + "]";
	}
}
