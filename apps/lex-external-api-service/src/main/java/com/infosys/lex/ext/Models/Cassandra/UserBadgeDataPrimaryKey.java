package com.infosys.lex.ext.Models.Cassandra;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserBadgeDataPrimaryKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private Date rootOrg;
	@PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private Date userId;
	@PrimaryKeyColumn(name = "badge_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
	private Date badgeId;

	

	public Date getBadgeId() {
		return badgeId;
	}

	public void setBadgeId(Date badgeId) {
		this.badgeId = badgeId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

	public Date getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(Date rootOrg) {
		this.rootOrg = rootOrg;
	}

	public Date getUserId() {
		return userId;
	}

	public void setUserId(Date userId) {
		this.userId = userId;
	}

	public UserBadgeDataPrimaryKey() {
	}

}
