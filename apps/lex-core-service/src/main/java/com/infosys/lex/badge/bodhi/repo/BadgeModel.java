/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
///**
//© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
//Version: 1.10
//
//Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
//this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
//the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
//by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
//this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
//under the law.
//
//Highly Confidential
// 
//*/
//substitute url based on requirement
//
//import java.util.Date;
//
//import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
//import org.springframework.data.cassandra.core.mapping.Column;
//import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
//import org.springframework.data.cassandra.core.mapping.Table;
//
//@Table("badge")
//public class BadgeModel {
//	@PrimaryKeyColumn(name = "badge_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
//	private String badgeId;
//	@Column("badge_type")
//	private String badgeType;
//	@Column("badge_group")
//	private String badgeGroup;
//	@Column("threshold")
//	private Integer threshold;
//	@Column("badge_name")
//	private String badgeName;
//	@Column("description")
//	private String description;
//	@Column("message")
//	private String message;
//	@Column("image")
//	private String image;
//	@Column("created_date")
//	private Date createdDate;
//	@Column("badge_order")
//	private String badgeOrder;
//
//	public String getBadgeId() {
//		return badgeId;
//	}
//
//	public void setBadgeId(String badgeId) {
//		this.badgeId = badgeId;
//	}
//
//	public String getBadgeType() {
//		return badgeType;
//	}
//
//	public void setBadgeType(String badgeType) {
//		this.badgeType = badgeType;
//	}
//
//	public String getBadgeGroup() {
//		return badgeGroup;
//	}
//
//	public void setBadgeGroup(String badgeGroup) {
//		this.badgeGroup = badgeGroup;
//	}
//
//	public Integer getThreshold() {
//		return threshold;
//	}
//
//	public void setThreshold(Integer threshold) {
//		this.threshold = threshold;
//	}
//
//	public String getBadgeName() {
//		return badgeName;
//	}
//
//	public void setBadgeName(String badgeName) {
//		this.badgeName = badgeName;
//	}
//
//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}
//
//	public String getMessage() {
//		return message;
//	}
//
//	public void setMessage(String message) {
//		this.message = message;
//	}
//
//	public String getImage() {
//		return image;
//	}
//
//	public void setImage(String image) {
//		this.image = image;
//	}
//
//	public Date getCreatedDate() {
//		return createdDate;
//	}
//
//	public void setCreatedDate(Date createdDate) {
//		this.createdDate = createdDate;
//	}
//
//	public String getBadgeOrder() {
//		return badgeOrder;
//	}
//
//	public void setBadgeOrder(String badgeOrder) {
//		this.badgeOrder = badgeOrder;
//	}
//
//	public BadgeModel(String badgeId, String badgeType, String badgeGroup, Integer threshold, String badgeName,
//			String description, String message, String image, Date createdDate, String badgeOrder) {
//		super();
//		this.badgeId = badgeId;
//		this.badgeType = badgeType;
//		this.badgeGroup = badgeGroup;
//		this.threshold = threshold;
//		this.badgeName = badgeName;
//		this.description = description;
//		this.message = message;
//		this.image = image;
//		this.createdDate = createdDate;
//		this.badgeOrder = badgeOrder;
//	}
//
//	public BadgeModel() {
//	}
//
//	@Override
//	public String toString() {
//		return "BadgeModel [badgeId=" + badgeId + ", badgeType=" + badgeType + ", badgeGroup=" + badgeGroup
//				+ ", threshold=" + threshold + ", badgeName=" + badgeName + ", description=" + description
//				+ ", message=" + message + ", image=" + image + ", createdDate=" + createdDate + ", badgeOrder="
//				+ badgeOrder + "]";
//	}
//
//}