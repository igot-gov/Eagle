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
public class Competency {

	@PrimaryKey
	private CompetencyPrimarykey competencyPrimarykey;

	@Column("competency")
	private String competency;

	@Column("level")
	private int level;

	public Competency(CompetencyPrimarykey competencyPrimarykey, String competency, int level) {
		super();
		this.competencyPrimarykey = competencyPrimarykey;
		this.competency = competency;
		this.level = level;
	}

	public Competency() {
		super();
	}

	public CompetencyPrimarykey getCompetencyPrimarykey() {
		return competencyPrimarykey;
	}

	public void setCompetencyPrimarykey(CompetencyPrimarykey competencyPrimarykey) {
		this.competencyPrimarykey = competencyPrimarykey;
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

}
