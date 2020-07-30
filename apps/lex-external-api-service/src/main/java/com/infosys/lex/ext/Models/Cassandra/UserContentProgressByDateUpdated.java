package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_content_progress_by_date_updated")
public class UserContentProgressByDateUpdated {
	@PrimaryKey
	private UserContentProgressByDateUpdatedPrimaryKey userContentProgressPrimaryKey;

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

	public UserContentProgressByDateUpdatedPrimaryKey getUserContentProgressPrimaryKey() {
		return userContentProgressPrimaryKey;
	}

	public void setUserContentProgressPrimaryKey(
			UserContentProgressByDateUpdatedPrimaryKey userContentProgressPrimaryKey) {
		this.userContentProgressPrimaryKey = userContentProgressPrimaryKey;
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
}
