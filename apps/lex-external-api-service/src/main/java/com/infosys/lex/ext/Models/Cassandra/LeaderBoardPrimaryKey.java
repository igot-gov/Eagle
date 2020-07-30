package com.infosys.lex.ext.Models.Cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class LeaderBoardPrimaryKey implements Serializable {
	private static final long serialVersionUID = 1L;
	@PrimaryKeyColumn(name = "leaderboard_year", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private Integer leaderBoardYear;
	@PrimaryKeyColumn(name = "duration_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String durationType;
	@PrimaryKeyColumn(name = "duration_value", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private Integer durationValue;
	@PrimaryKeyColumn(name = "leaderboard_type", ordinal = 3, type = PrimaryKeyType.PARTITIONED)
	private String leaderBoardType;
	@PrimaryKeyColumn(name = "email_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
	private String emailId;

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

	public Integer getDurationValue() {
		return durationValue;
	}

	public void setDurationValue(Integer durationValue) {
		this.durationValue = durationValue;
	}

	public String getLeaderBoardType() {
		return leaderBoardType;
	}

	public void setLeaderBoardType(String leaderBoardType) {
		this.leaderBoardType = leaderBoardType;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
