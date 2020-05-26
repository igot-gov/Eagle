/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.bodhi.repo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommonGoalKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4925749029377740580L;

	@Column(name = "root_org", columnDefinition = "VARCHAR")
	private String rootOrg;

	@Column(name = "id", columnDefinition = "VARCHAR")
	private String id;

	@Column(name = "version")
	private Float version;

	public String getRootOrg() {
		return rootOrg;
	}

	public String getId() {
		return id;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Float getVersion() {
		return version;
	}

	public void setVersion(Float version) {
		this.version = version;
	}

	public CommonGoalKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CommonGoalKey(String rootOrg, String id, Float version) {
		super();
		this.rootOrg = rootOrg;
		this.id = id;
		this.version = version;
	}

	@Override
	public String toString() {
		return "CommonGoalKey [rootOrg=" + rootOrg + ", id=" + id + ", version=" + version + "]";
	}

}
