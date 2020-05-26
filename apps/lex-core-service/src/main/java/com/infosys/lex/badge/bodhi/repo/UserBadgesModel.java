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
package com.infosys.lex.badge.bodhi.repo;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_badges")
public class UserBadgesModel {

	@PrimaryKey
	private UserBadgesPrimaryKeyModel primaryKey;

	@Column("badge_type")
	private String badgeType;
	@Column("first_received_date")
	private Date firstReceivedDate;
	@Column("last_received_date")
	private Date lastReceivedDate;
	@Column("progress")
	private Float progress;
	@Column("progress_date")
	private Date progressDate;
	@Column("received_count")
	private Integer receivedCount;

	public UserBadgesPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(UserBadgesPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getBadgeType() {
		return badgeType;
	}

	public void setBadgeType(String badgeType) {
		this.badgeType = badgeType;
	}

	public Date getFirstReceivedDate() {
		return firstReceivedDate;
	}

	public void setFirstReceivedDate(Date firstReceivedDate) {
		this.firstReceivedDate = firstReceivedDate;
	}

	public Date getLastReceivedDate() {
		return lastReceivedDate;
	}

	public void setLastReceivedDate(Date lastReceivedDate) {
		this.lastReceivedDate = lastReceivedDate;
	}

	public Float getProgress() {
		return progress;
	}

	public void setProgress(Float progress) {
		this.progress = progress;
	}

	public Date getProgressDate() {
		return progressDate;
	}

	public void setProgressDate(Date progressDate) {
		this.progressDate = progressDate;
	}

	public Integer getReceivedCount() {
		return receivedCount;
	}

	public void setReceivedCount(Integer receivedCount) {
		this.receivedCount = receivedCount;
	}

	public UserBadgesModel(UserBadgesPrimaryKeyModel primaryKey, String badgeType, Date firstReceivedDate,
			Date lastReceivedDate, Float progress, Date progressDate, Integer receivedCount) {
		super();
		this.primaryKey = primaryKey;
		this.badgeType = badgeType;
		this.firstReceivedDate = firstReceivedDate;
		this.lastReceivedDate = lastReceivedDate;
		this.progress = progress;
		this.progressDate = progressDate;
		this.receivedCount = receivedCount;
	}

	public UserBadgesModel() {
		super();
	}

	@Override
	public String toString() {
		return "UserBadgesModel [primaryKey=" + primaryKey + ", badgeType=" + badgeType + ", firstReceivedDate="
				+ firstReceivedDate + ", lastReceivedDate=" + lastReceivedDate + ", progress=" + progress
				+ ", progressDate=" + progressDate + ", receivedCount=" + receivedCount + "]";
	}

}
