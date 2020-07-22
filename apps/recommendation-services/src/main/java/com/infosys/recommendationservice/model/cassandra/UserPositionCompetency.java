/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.model.cassandra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table("user_position_competency")
public class UserPositionCompetency {

	@PrimaryKey
	private UserPositionCompetencyPrimarykey userPositionCompetencyPrimarykey;

	@Column("user_competency")
	private String userCompetency;

	@Column("user_level")
	private List<Integer> userLevel;

	@Column("delta")
	private List<Integer> delta;

	public UserPositionCompetency(UserPositionCompetencyPrimarykey userPositionCompetencyPrimarykey, String userCompetency, List<Integer> userLevel) {
		super();
		this.userPositionCompetencyPrimarykey = userPositionCompetencyPrimarykey;
		this.userCompetency = userCompetency;
		this.userLevel = userLevel;

	}

	public UserPositionCompetency() {
		super();
	}


	public UserPositionCompetencyPrimarykey getUserPositionCompetencyPrimarykey() {
		return userPositionCompetencyPrimarykey;
	}

	public void setUserPositionCompetencyPrimarykey(UserPositionCompetencyPrimarykey userPositionCompetencyPrimarykey) {
		this.userPositionCompetencyPrimarykey = userPositionCompetencyPrimarykey;
	}

	public String getUserCompetency() {
		return userCompetency;
	}

	public void setUserCompetency(String userCompetency) {
		this.userCompetency = userCompetency;
	}

	public List<Integer> getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(List<Integer> userLevel) {
		this.userLevel = userLevel;
	}

	public List<Integer> getDelta() {
		return delta;
	}

	public void setDelta(List<Integer> delta) {
		this.delta = delta;
	}


}
