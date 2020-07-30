package com.infosys.lex.ext.Models.Cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserLearningGoalsPrimaryKey implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String userId;

	@PrimaryKeyColumn(name = "goal_type", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private String goalType;

	@PrimaryKeyColumn(name = "goal_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
	private String goalId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String email) {
		this.userId = email;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
