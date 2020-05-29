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

package com.infosys.lex.notification.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "event_recipient", schema = "wingspan")
public class RecipientTags {

	@EmbeddedId
	private RecipientTagsPrimaryKey tagsPrimaryKey;

	@Column(name = "tag")
	private String tag;

	@Column(name = "updated_on")
	private Timestamp updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "classification")
	private String classification;

	@Column(name = "user_roles")
	private String userRoles;

	@Column(name = "target_url")
	private String targetUrl;

	public RecipientTagsPrimaryKey getTagsPrimaryKey() {
		return tagsPrimaryKey;
	}

	public void setTagsPrimaryKey(RecipientTagsPrimaryKey tagsPrimaryKey) {
		this.tagsPrimaryKey = tagsPrimaryKey;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(String userRoles) {
		this.userRoles = userRoles;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public RecipientTags() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecipientTags(RecipientTagsPrimaryKey tagsPrimaryKey, String tag, Timestamp updatedOn, String updatedBy,
			String classification, String userRoles, String targetUrl) {
		super();
		this.tagsPrimaryKey = tagsPrimaryKey;
		this.tag = tag;
		this.updatedOn = updatedOn;
		this.updatedBy = updatedBy;
		this.classification = classification;
		this.userRoles = userRoles;
		this.targetUrl = targetUrl;
	}

	@Override
	public String toString() {
		return "RecipientTags [tagsPrimaryKey=" + tagsPrimaryKey + ", tag=" + tag + ", updatedOn=" + updatedOn
				+ ", updatedBy=" + updatedBy + ", classification=" + classification + ", userRoles=" + userRoles
				+ ", targetUrl=" + targetUrl + "]";
	}
}
