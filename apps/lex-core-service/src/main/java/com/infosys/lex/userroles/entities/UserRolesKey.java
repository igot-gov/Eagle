package com.infosys.lex.userroles.entities;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserRolesKey {
	@PrimaryKeyColumn(name="user_id",ordinal=1,type=PrimaryKeyType.PARTITIONED)
	private String userId;
	
	@PrimaryKeyColumn(name="root_org", ordinal = 0 , type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}
}
