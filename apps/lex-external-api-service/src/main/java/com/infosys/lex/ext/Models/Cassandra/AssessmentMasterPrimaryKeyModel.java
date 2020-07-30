/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential

*/
package com.infosys.lex.ext.Models.Cassandra;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class AssessmentMasterPrimaryKeyModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(name = "content_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String contentId;

	@PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private String userId;

	@PrimaryKeyColumn(name = "ts_created", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private Date tsCreated;

	@PrimaryKeyColumn(name = "result_percent", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private Double resultPercent;

	@PrimaryKeyColumn(name = "id", ordinal = 5, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private UUID id;

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public Date getTsCreated() {
		return tsCreated;
	}

	public void setTsCreated(Date tsCreated) {
		this.tsCreated = tsCreated;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "UserAssessmentMasterPrimaryKeyModel [rootOrg=" + rootOrg + ", contentId=" + contentId + ", userId="
				+ userId + ", tsCreated=" + tsCreated + ", resultPercent=" + resultPercent + ", id=" + id + "]";
	}

	public AssessmentMasterPrimaryKeyModel(String rootOrg, String contentId, String userId, Date tsCreated,
			Double resultPercent, UUID id) {
		this.rootOrg = rootOrg;
		this.contentId = contentId;
		this.userId = userId;
		this.tsCreated = tsCreated;
		this.resultPercent = resultPercent;
		this.id = id;
	}

	public Double getResultPercent() {
		return resultPercent;
	}

	public void setResultPercent(Double resultPercent) {
		this.resultPercent = resultPercent;
	}

	public AssessmentMasterPrimaryKeyModel() {
		super();
		// TODO Auto-generated constructor stub
	}

}
