package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;
import java.util.Set;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_content_progress")
public class UserContentProgress {

	@PrimaryKey
	private UserContentProgressPrimaryKey userContentProgressPrimaryKey;

	@Column("date_updated")
	private Date dateUpdated;

	@Column("first_accessed_on")
	private Date firstAccessedOn;

	@Column("first_completed_on")
	private Date firstCompletedOn;

	@Column("last_accessed_on")
	private Date lastAccessedOn;

	@Column("last_ts")
	private Date lastTs;

	@Column("progress")
	private Float progress;

	@Column("updated_by")
	private String updatedBy;

	@Column("visited_set")
	private Set<Float> visitedSet;

	public UserContentProgressPrimaryKey getUserContentProgressPrimaryKey() {
		return userContentProgressPrimaryKey;
	}

	public void setUserContentProgressPrimaryKey(UserContentProgressPrimaryKey userContentProgressPrimaryKey) {
		this.userContentProgressPrimaryKey = userContentProgressPrimaryKey;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public Date getFirstAccessedOn() {
		return firstAccessedOn;
	}

	public void setFirstAccessedOn(Date firstAccessedOn) {
		this.firstAccessedOn = firstAccessedOn;
	}

	public Date getFirstCompletedOn() {
		return firstCompletedOn;
	}

	public void setFirstCompletedOn(Date firstCompletedOn) {
		this.firstCompletedOn = firstCompletedOn;
	}

	public Date getLastAccessedOn() {
		return lastAccessedOn;
	}

	public void setLastAccessedOn(Date lastAccessedOn) {
		this.lastAccessedOn = lastAccessedOn;
	}

	public Date getLastTs() {
		return lastTs;
	}

	public void setLastTs(Date lastTs) {
		this.lastTs = lastTs;
	}

	public Float getProgress() {
		return progress;
	}

	public void setProgress(Float progress) {
		this.progress = progress;
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
}
