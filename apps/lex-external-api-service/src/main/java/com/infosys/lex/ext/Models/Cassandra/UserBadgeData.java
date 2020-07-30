package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_badges")
public class UserBadgeData {
	@PrimaryKey
	private UserBadgeDataPrimaryKey key;
	@Column("badge_type")
	private String badgeType;
	@Column("progress")
	private Float progress;
	@Column("first_received_date")
	private Date firstReceivedDate;
	@Column("received_count")
	private Integer sourceId;
	@Column("last_received_date")
	private Date lastReceivedDate;
	@Column("progress_date")
	private Date progressDate;

	public UserBadgeDataPrimaryKey getKey() {
		return key;
	}

	public void setKey(UserBadgeDataPrimaryKey key) {
		this.key = key;
	}

	public String getBadgeType() {
		return badgeType;
	}

	public void setBadgeType(String badgeType) {
		this.badgeType = badgeType;
	}

	public Float getProgress() {
		return progress;
	}

	public void setProgress(Float progress) {
		this.progress = progress;
	}

	public Date getFirstReceivedDate() {
		return firstReceivedDate;
	}

	public void setFirstReceivedDate(Date firstReceivedDate) {
		this.firstReceivedDate = firstReceivedDate;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Date getLastReceivedDate() {
		return lastReceivedDate;
	}

	public void setLastReceivedDate(Date lastReceivedDate) {
		this.lastReceivedDate = lastReceivedDate;
	}

	public Date getProgressDate() {
		return progressDate;
	}

	public void setProgressDate(Date progressDate) {
		this.progressDate = progressDate;
	}

	public UserBadgeData(UserBadgeDataPrimaryKey key, String badgeType, Float progress, Date firstReceivedDate,
			Integer sourceId, Date lastReceivedDate, Date progressDate) {
		super();
		this.key = key;
		this.badgeType = badgeType;
		this.progress = progress;
		this.firstReceivedDate = firstReceivedDate;
		this.sourceId = sourceId;
		this.lastReceivedDate = lastReceivedDate;
		this.progressDate = progressDate;
	}

	public UserBadgeData() {
	}
}
