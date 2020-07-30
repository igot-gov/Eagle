package com.infosys.lex.ext.Models.Cassandra;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class AssessmentDataPrimaryKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "date_created", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private Date dateCreated;
	@PrimaryKeyColumn(name = "ts_created", ordinal = 0, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private Date tsCreated;
	@PrimaryKeyColumn(name = "parent_source_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
	private String pareentSourceId;
	@PrimaryKeyColumn(name = "result_percent", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private Float resultPercent;
	@PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private UUID id;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getTsCreated() {
		return tsCreated;
	}

	public void setTsCreated(Date tsCreated) {
		this.tsCreated = tsCreated;
	}

	public String getPareentSourceId() {
		return pareentSourceId;
	}

	public void setPareentSourceId(String pareentSourceId) {
		this.pareentSourceId = pareentSourceId;
	}

	public Float getResultPercent() {
		return resultPercent;
	}

	public void setResultPercent(Float resultPercent) {
		this.resultPercent = resultPercent;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public AssessmentDataPrimaryKey(Date dateCreated, Date tsCreated, String pareentSourceId, Float resultPercent,
			UUID id) {
		super();
		this.dateCreated = dateCreated;
		this.tsCreated = tsCreated;
		this.pareentSourceId = pareentSourceId;
		this.resultPercent = resultPercent;
		this.id = id;
	}

	public AssessmentDataPrimaryKey() {
	}

}
