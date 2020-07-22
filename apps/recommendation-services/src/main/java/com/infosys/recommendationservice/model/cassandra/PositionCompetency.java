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

import java.util.List;

@Table("postion_competency_allocation")
public class PositionCompetency {

	@PrimaryKey
	private PositionCompetencyPrimarykey positionCompetencyPrimarykey;

	@Column("competency")
	private String competency;

	@Column("level")
	private List<Integer> level;

	public PositionCompetency(PositionCompetencyPrimarykey positionPompetencyPrimarykey, String competency, List<Integer> level) {
		super();
		this.positionCompetencyPrimarykey = positionPompetencyPrimarykey;
		this.competency = competency;
		this.level = level;
	}

	public PositionCompetency() {
		super();
	}

	public PositionCompetencyPrimarykey getPositionCompetencyPrimarykey() {
		return positionCompetencyPrimarykey;
	}

	public void setPositionCompetencyPrimarykey(PositionCompetencyPrimarykey positionCompetencyPrimarykey) {
		this.positionCompetencyPrimarykey = positionCompetencyPrimarykey;
	}

	public String getCompetency() {
		return competency;
	}

	public void setCompetency(String competency) {
		this.competency = competency;
	}

	public List<Integer> getLevel() {
		return level;
	}

	public void setLevel(List<Integer> level) {
		this.level = level;
	}
}
