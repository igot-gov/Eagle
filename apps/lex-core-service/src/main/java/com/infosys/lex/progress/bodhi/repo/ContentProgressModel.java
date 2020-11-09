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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


@Table("user_content_progress")
public class ContentProgressModel {

	@PrimaryKey
	private ContentProgressPrimaryKeyModel primaryKey;

	@Column("progress")
	private Float progress;

	@Column("last_TS")
	private Date lastTS;

	@Column("date_updated")
	private Date dateUpdated;

	@Column("last_accessed_on")
	private Date lastAccessedOn;

	@Column("first_completed_on")
	private Date firstCompletedOn;

	@Column("first_accessed_on")
	private Date firstAccessedOn;

	@Column("updated_by")
	private String updatedBy;

	@Column("time_spent")
	private Float timespent;

	@Column("visited_set")
	private Set<Float> visitedSet;

	@Transient
	private List<String> parentList;

	@Transient
	private List<String> childrenList;

	public ContentProgressPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(ContentProgressPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Float getProgress() {
		return progress;
	}

	public void setProgress(Float progress) {
		this.progress = progress;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public Date getLastTS() {
		return lastTS;
	}

	public void setLastTS(Date lastTS) {
		this.lastTS = lastTS;
	}

	public Date getLastAccessedOn() {
		return lastAccessedOn;
	}

	public void setLastAccessedOn(Date lastAccessedOn) {
		this.lastAccessedOn = lastAccessedOn;
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

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Set<Float> getVisitedSet() {
		return visitedSet;
	}

	public void setVisitedSet(Set<Float> visitedSet) {
		this.visitedSet = visitedSet;
	}

	public List<String> getParentList() {
		return parentList;
	}

	public void setParentList(List<String> parentList) {
		this.parentList = parentList;
	}

	public List<String> getChildrenList() {
		return childrenList;
	}

	public Float getTimespent() {
		return (timespent==null ? 0 : timespent);
	}

	public void setTimespent(Float timespent) {
		this.timespent = timespent;
	}

	public void setChildrenList(List<String> childrenList) {
		this.childrenList = childrenList;
	}



	public ContentProgressModel(ContentProgressPrimaryKeyModel primaryKey, Float progress, Date lastTS,
			Date dateUpdated, Date lastAccessedOn, Date firstCompletedOn, Date firstAccessedOn, Float timespent, Set<Float> visitedSet,
			List<String> parentList, List<String> childrenList) {
		super();
		this.primaryKey = primaryKey;
		this.progress = progress;
		this.lastTS = lastTS;
		this.dateUpdated = dateUpdated;
		this.lastAccessedOn = lastAccessedOn;
		this.firstCompletedOn = firstCompletedOn;
		this.firstAccessedOn = firstAccessedOn;
		this.updatedBy = "api";
		this.visitedSet = visitedSet;
		this.parentList = parentList;
		this.childrenList = childrenList;
		this.timespent =  (timespent==null ? 0 : timespent);
	}

	public ContentProgressModel(ContentProgressPrimaryKeyModel primaryKey) {
		super();
		this.primaryKey = primaryKey;
		this.updatedBy = "api";
		this.childrenList = new ArrayList<>();
		this.parentList = new ArrayList<>();
	}

	public ContentProgressModel() {
		super();
		this.updatedBy = "api";
	}

	@Override
	public String toString() {
		return "ContentProgressModel [primaryKey=" + primaryKey + ", progress=" + progress + ", lastTS=" + lastTS
				+ ", dateUpdated=" + dateUpdated + ", lastAccessedOn=" + lastAccessedOn + ", firstCompletedOn="
				+ firstCompletedOn + ", firstAccessedOn=" + firstAccessedOn + ", updatedBy=" + updatedBy
				+ ", visitedSet=" + visitedSet + ", parentList=" + parentList + ", childrenList=" + childrenList + "]";
	}

}
