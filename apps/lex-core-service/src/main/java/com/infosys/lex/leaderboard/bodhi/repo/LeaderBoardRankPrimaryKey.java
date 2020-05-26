/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.leaderboard.bodhi.repo;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class LeaderBoardRankPrimaryKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@PrimaryKeyColumn(name = "root_org",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
	private Integer rootOrg;
	
	
	@PrimaryKeyColumn(name = "rank",ordinal = 1,type = PrimaryKeyType.PARTITIONED)
	private Integer rank;
	
	
	
	@PrimaryKeyColumn(name = "duration_type",ordinal = 2,type = PrimaryKeyType.CLUSTERED)
	private Integer durationType;
	

	
	@PrimaryKeyColumn(name = "leaderboard_type",ordinal = 3,type = PrimaryKeyType.CLUSTERED)
	private Integer leaderBoardType;
	
	@PrimaryKeyColumn(name = "leaderboard_year",ordinal = 4,type = PrimaryKeyType.CLUSTERED)
	private Integer leaderBoardYear;
	
	
	@PrimaryKeyColumn(name = "duration_value",ordinal = 5,type = PrimaryKeyType.CLUSTERED)
	private Integer durationValue;

	
	@PrimaryKeyColumn(name = "user_id",ordinal = 6,type = PrimaryKeyType.CLUSTERED)
	private Integer userId;







	public Integer getRootOrg() {
		return rootOrg;
	}



	public void setRootOrg(Integer rootOrg) {
		this.rootOrg = rootOrg;
	}



	public LeaderBoardRankPrimaryKey(Integer rootOrg, Integer rank, Integer durationType, Integer leaderBoardType,
			Integer leaderBoardYear, Integer durationValue, Integer userId) {
		super();
		this.rootOrg = rootOrg;
		this.rank = rank;
		this.durationType = durationType;
		this.leaderBoardType = leaderBoardType;
		this.leaderBoardYear = leaderBoardYear;
		this.durationValue = durationValue;
		this.userId = userId;
	}



	public Integer getRank() {
		return rank;
	}



	public void setRank(Integer rank) {
		this.rank = rank;
	}



	public Integer getLeaderBoardYear() {
		return leaderBoardYear;
	}



	public void setLeaderBoardYear(Integer leaderBoardYear) {
		this.leaderBoardYear = leaderBoardYear;
	}



	public Integer getDurationType() {
		return durationType;
	}



	public void setDurationType(Integer durationType) {
		this.durationType = durationType;
	}



	public Integer getDurationValue() {
		return durationValue;
	}



	public void setDurationValue(Integer durationValue) {
		this.durationValue = durationValue;
	}



	public Integer getLeaderBoardType() {
		return leaderBoardType;
	}



	public void setLeaderBoardType(Integer leaderBoardType) {
		this.leaderBoardType = leaderBoardType;
	}



	public Integer getUserId() {
		return userId;
	}



	public void setEmailId(Integer userId) {
		this.userId = userId;
	}

}
