package com.infosys.lex.notification.model.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class NotificationErrorsPrimaryKey {

	@PrimaryKeyColumn(name = "root_org", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
	private String rootOrg;

	@PrimaryKeyColumn(name = "event_id", type = PrimaryKeyType.CLUSTERED, ordinal = 1)
	private String eventId;

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public NotificationErrorsPrimaryKey(String rootOrg, String eventId) {
		this.rootOrg = rootOrg;
		this.eventId = eventId;
	}
}
