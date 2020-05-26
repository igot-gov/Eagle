/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
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
package com.infosys.lex.badge.postgredb.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "badge", schema = "wingspan")
public class Badge {

	@EmbeddedId
	BadgeKey key;

	@Column(name = "image")
	private String image;

	@Column(name = "badge_type")
	private String badgeType;

	@Column(name = "threshold1")
	private BigDecimal threshold1;

	@Column(name = "time_period")
	private Integer timePeriod;

	@Column(name = "threshold2")
	private Integer threshold2;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "start_date")
	private Timestamp startDate;

	@Column(name = "end_date")
	private Timestamp endDate;

	@Column(name = "sharable")
	private String sharable;

	@Column(name = "badge_group")
	private String badgeGroup;

	@Column(name = "group_order")
	private Integer groupOrder;

	public BadgeKey getKey() {
		return key;
	}

	public String getImage() {
		return image;
	}

	public String getBadgeType() {
		return badgeType;
	}

	public BigDecimal getThreshold1() {
		return threshold1;
	}

	public Integer getTimePeriod() {
		return timePeriod;
	}

	public Integer getThreshold2() {
		return threshold2;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public String getSharable() {
		return sharable;
	}

	public String getBadgeGroup() {
		return badgeGroup;
	}

	public Integer getGroupOrder() {
		return groupOrder;
	}

	public Badge() {

	}

	public Badge(BadgeKey key, String image, String badgeType, BigDecimal threshold1, Integer timePeriod,
			Integer threshold2, Timestamp createdDate, String createdBy, Timestamp startDate, Timestamp endDate,
			String sharable, String badgeGroup, Integer groupOrder) {
		this.key = key;
		this.image = image;
		this.badgeType = badgeType;
		this.threshold1 = threshold1;
		this.timePeriod = timePeriod;
		this.threshold2 = threshold2;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sharable = sharable;
		this.badgeGroup = badgeGroup;
		this.groupOrder = groupOrder;
	}

	@Override
	public String toString() {
		return "Badge [key=" + key + ", image=" + image + ", badgeType=" + badgeType + ", threshold1=" + threshold1
				+ ", timePeriod=" + timePeriod + ", threshold2=" + threshold2 + ", createdDate=" + createdDate
				+ ", createdBy=" + createdBy + ", startDate=" + startDate + ", endDate=" + endDate + ", sharable="
				+ sharable + ", badgeGroup=" + badgeGroup + ", groupOrder=" + groupOrder + "]";
	}

}
