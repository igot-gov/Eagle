/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.leaderboard.bodhi.repo;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class LeaderBoardPrimaryKey implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@PrimaryKeyColumn(name = "root_org",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
	private Integer rootOrg;
	
	@PrimaryKeyColumn(name = "leaderboard_year",ordinal = 1,type = PrimaryKeyType.PARTITIONED)
	private Integer leaderBoardYear;
	
	@PrimaryKeyColumn(name = "duration_type",ordinal = 2,type = PrimaryKeyType.PARTITIONED)
	private String durationType;
	
	@PrimaryKeyColumn(name = "duration_value",ordinal = 3,type = PrimaryKeyType.PARTITIONED)
	private Integer durationValue;
	
	@PrimaryKeyColumn(name = "leaderboard_type",ordinal = 4,type = PrimaryKeyType.PARTITIONED)
	private String leaderBoardType;
	

	@PrimaryKeyColumn(name = "user_id",ordinal = 5,type = PrimaryKeyType.CLUSTERED)
	private String userId;
	
	
	public Integer getLeaderBoardYear() {
		return leaderBoardYear;
	}

	public void setLeaderBoardYear(Integer leaderBoardYear) {
		this.leaderBoardYear = leaderBoardYear;
	}



	public String getDurationType() {
		return durationType;
	}

	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}

	public String getLeaderBoardType() {
		return leaderBoardType;
	}

	public void setLeaderBoardType(String leaderBoardType) {
		this.leaderBoardType = leaderBoardType;
	}

	

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getDurationValue() {
		return durationValue;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setDurationValue(Integer durationValue) {
		this.durationValue = durationValue;
	}

	public Integer getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(Integer rootOrg) {
		this.rootOrg = rootOrg;
	}

	public LeaderBoardPrimaryKey(Integer rootOrg, Integer leaderBoardYear, String durationType, Integer durationValue,
			String leaderBoardType, String userId) {
		super();
		this.rootOrg = rootOrg;
		this.leaderBoardYear = leaderBoardYear;
		this.durationType = durationType;
		this.durationValue = durationValue;
		this.leaderBoardType = leaderBoardType;
		this.userId = userId;
	}

	




}
