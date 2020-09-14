package com.infosys.lex.ext.Models.Cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserSharedGoalsPrimaryKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(name = "shared_with", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String sharedWith;

	@PrimaryKeyColumn(name = "goal_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private String goalType;

	@PrimaryKeyColumn(name = "goal_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
	private String goalId;

	@PrimaryKeyColumn(name = "shared_by", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
	private String sharedBy;

	public String getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(String sharedWith) {
		this.sharedWith = sharedWith;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public String getGoalId() {
		return goalId;
	}

	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}

	public String getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(String sharedBy) {
		this.sharedBy = sharedBy;
	}

}
