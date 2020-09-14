package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("badge")
public class BadgeData {
	@PrimaryKeyColumn(name = "badge_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String badgeId;
	@Column("badge_type")
	private String badgeType;
	@Column("badge_group")
	private String badgeGroup;
	@Column("threshold")
	private Integer threshold;
	@Column("badge_name")
	private String badgeName;
	@Column("description")
	private String description;
	@Column("message")
	private String message;
	@Column("image")
	private String image;
	@Column("created_date")
	private Date createdDate;
	@Column("badge_order")
	private String badgeOrder;

	public String getBadgeId() {
		return badgeId;
	}

	public void setBadgeId(String badgeId) {
		this.badgeId = badgeId;
	}

	public String getBadgeType() {
		return badgeType;
	}

	public void setBadgeType(String badgeType) {
		this.badgeType = badgeType;
	}

	public String getBadgeGroup() {
		return badgeGroup;
	}

	public void setBadgeGroup(String badgeGroup) {
		this.badgeGroup = badgeGroup;
	}

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public String getBadgeName() {
		return badgeName;
	}

	public void setBadgeName(String badgeName) {
		this.badgeName = badgeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getBadgeOrder() {
		return badgeOrder;
	}

	public void setBadgeOrder(String badgeOrder) {
		this.badgeOrder = badgeOrder;
	}

	public BadgeData(String badgeId, String badgeType, String badgeGroup, Integer threshold, String badgeName,
			String description, String message, String image, Date createdDate, String badgeOrder) {
		super();
		this.badgeId = badgeId;
		this.badgeType = badgeType;
		this.badgeGroup = badgeGroup;
		this.threshold = threshold;
		this.badgeName = badgeName;
		this.description = description;
		this.message = message;
		this.image = image;
		this.createdDate = createdDate;
		this.badgeOrder = badgeOrder;
	}

	public BadgeData() {
	}
}
