/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.postgredb.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "faq_group", schema = "wingspan")
public class FaqGroup {

	@EmbeddedId
	private FaqGroupPrimaryKey faqGroupPrimaryKey;

	@Column(name = "group_name")
	private String groupName;

	@Column(name = "updated_on")
	private Timestamp updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	public FaqGroup(FaqGroupPrimaryKey faqGroupPrimaryKey, String groupName, Timestamp updatedOn, String updatedBy) {
		super();
		this.faqGroupPrimaryKey = faqGroupPrimaryKey;
		this.groupName = groupName;
		this.updatedOn = updatedOn;
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "FaqGroup [faqGroupPrimaryKey=" + faqGroupPrimaryKey + ", groupName=" + groupName + ", updatedOn="
				+ updatedOn + ", updatedBy=" + updatedBy + "]";
	}

	public FaqGroup() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FaqGroupPrimaryKey getFaqGroupPrimaryKey() {
		return faqGroupPrimaryKey;
	}

	public void setFaqGroupPrimaryKey(FaqGroupPrimaryKey faqGroupPrimaryKey) {
		this.faqGroupPrimaryKey = faqGroupPrimaryKey;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

}
