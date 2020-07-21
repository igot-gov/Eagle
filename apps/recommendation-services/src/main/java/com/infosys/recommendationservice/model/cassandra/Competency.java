/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.model.cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_competency")
public class UserCompetency {

	@PrimaryKey
	private UserCompetencyPrimarykey userCompetencyPrimarykey;

	@Column("competency")
	private String competency;

	@Column("level")
	private int level;

	@Column("isCompetent")
	private boolean isCompetent;

	@Column("delta")
	private double delta;


	public UserCompetency(UserCompetencyPrimarykey userCompetencyPrimarykey, String competency, int level, boolean isCompetent, double delta) {
		super();
		this.userCompetencyPrimarykey = userCompetencyPrimarykey;
		this.competency = competency;
		this.level = level;
		this.isCompetent = isCompetent;
		this.delta = delta;
	}

	public UserCompetency() {
		super();
	}

	public UserCompetencyPrimarykey getUserCompetencyPrimarykey() {
		return userCompetencyPrimarykey;
	}

	public void setUserCompetencyPrimarykey(UserCompetencyPrimarykey userCompetencyPrimarykey) {
		this.userCompetencyPrimarykey = userCompetencyPrimarykey;
	}

	public String getCompetency() {
		return competency;
	}

	public void setCompetency(String competency) {
		this.competency = competency;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isCompetent() {
		return isCompetent;
	}

	public void setCompetent(boolean competent) {
		isCompetent = competent;
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}
}
