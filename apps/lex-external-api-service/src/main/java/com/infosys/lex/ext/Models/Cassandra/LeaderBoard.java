package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("leaderboard")
public class LeaderBoard {
	@PrimaryKey
	private LeaderBoardPrimaryKey leaderBoardPrimaryKey;
	@Column("start_date")
	private Date startDate;
	@Column("end_date")
	private Date endDate;
	@Column("points")
	private Integer points;
	@Column("rank")
	private Integer rank;

	public LeaderBoardPrimaryKey getLeaderBoardPrimaryKey() {
		return leaderBoardPrimaryKey;
	}

	public void setLeaderBoardPrimaryKey(LeaderBoardPrimaryKey leaderBoardPrimaryKey) {
		this.leaderBoardPrimaryKey = leaderBoardPrimaryKey;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

}
