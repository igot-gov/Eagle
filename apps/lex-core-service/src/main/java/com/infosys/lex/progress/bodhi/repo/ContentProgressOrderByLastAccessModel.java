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
package com.infosys.lex.progress.bodhi.repo;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


@Table("user_content_progress_order_last_assess")
public class ContentProgressOrderByLastAccessModel {

	@PrimaryKey
	private ContentProgressOrderByLastAccessPrimaryKeyModel primaryKey;
	
	@Column("progress")
	private Float progress;
	
	@Column("last_TS")
	private Date lastTS;
	
	@Column("first_completed_on")
	private Date firstCompletedOn;
	
	@Column("first_accessed_on")
	private Date firstAccessedOn;
	
	public ContentProgressOrderByLastAccessPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(ContentProgressOrderByLastAccessPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Float getProgress() {
		return progress;
	}

	public void setProgress(Float progress) {
		this.progress = progress;
	}

	public Date getLastTS() {
		return lastTS;
	}

	public void setLastTS(Date lastTS) {
		this.lastTS = lastTS;
	}
	
	public Date getFirstCompletedOn() {
		return firstCompletedOn;
	}

	public void setFirstCompletedOn(Date firstCompletedOn) {
		this.firstCompletedOn = firstCompletedOn;
	}

	public Date getFirstAccessedOn() {
		return firstAccessedOn;
	}

	public void setFirstAccessedOn(Date firstAccessedOn) {
		this.firstAccessedOn = firstAccessedOn;
	}

	public ContentProgressOrderByLastAccessModel(ContentProgressOrderByLastAccessPrimaryKeyModel primaryKey, Float progress, Date lastTS,
			Date firstCompletedOn, Date firstAccessedOn) {
		super();
		this.primaryKey = primaryKey;
		this.progress = progress;
		this.lastTS = lastTS;
		this.firstCompletedOn = firstCompletedOn;
		this.firstAccessedOn = firstAccessedOn;
	}

	public ContentProgressOrderByLastAccessModel() {
		super();
	}

	@Override
	public String toString() {
		return "ContentProgressOrderByLastAccessModel [primaryKey=" + primaryKey + ", progress=" + progress
				+ ", lastTS=" + lastTS + ", firstCompletedOn=" + firstCompletedOn + ", firstAccessedOn="
				+ firstAccessedOn + "]";
	}

}
