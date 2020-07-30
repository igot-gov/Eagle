package com.infosys.lex.ext.Models.Cassandra;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserAssessmentPrimaryKey implements Serializable {

	private static final long serialVersionUID = 1L;
	@PrimaryKeyColumn(name = "ts_created", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private Date timeStampCreated;
	@PrimaryKeyColumn(name = "parent_source_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
	private String parentSourceId;
	@PrimaryKeyColumn(name = "result_percent", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private Double resultPercent;
	@PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private String id;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getTimeStampCreated() {
		return timeStampCreated;
	}

	public void setTimeStampCreated(Date timeStampCreated) {
		this.timeStampCreated = timeStampCreated;
	}

	public String getParentSourceId() {
		return parentSourceId;
	}

	public void setParentSourceId(String parentSourceId) {
		this.parentSourceId = parentSourceId;
	}

	public Double getResultPercent() {
		return resultPercent;
	}

	public void setResultPercent(Double resultPercent) {
		this.resultPercent = resultPercent;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
