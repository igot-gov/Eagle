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
@Table(name = "tenant_event_template", schema = "wingspan")
public class TenantTemplate {

	@EmbeddedId
	private TenantTemplatePrimaryKey primarykey;

	@Column(name = "template_subject")
	private String template_subject;

	@Column(name = "template_text")
	private String template_text;

	@Column(name = "updated_on")
	private Timestamp updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;
//	@Column(name="tag")
//	private String tag;

	public TenantTemplatePrimaryKey getPrimarykey() {
		return primarykey;
	}

	public String getTemplateSubject() {
		return template_subject;
	}

	public String getTemplateText() {
		return template_text;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setPrimarykey(TenantTemplatePrimaryKey primarykey) {
		this.primarykey = primarykey;
	}

	public void setTemplateSubject(String template_subject) {
		this.template_subject = template_subject;
	}

	public void setTemplateText(String template_text) {
		this.template_text = template_text;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public TenantTemplate() {
		super();
	}

	public TenantTemplate(TenantTemplatePrimaryKey primarykey, String template_subject, String template_text,
			Timestamp updatedOn, String updatedBy) {
		super();
		this.primarykey = primarykey;
		this.template_subject = template_subject;
		this.template_text = template_text;
		this.updatedOn = updatedOn;
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "TenantTemplate [primarykey=" + primarykey + ", template_subject=" + template_subject
				+ ", template_text=" + template_text + ", updatedOn=" + updatedOn + ", updatedBy=" + updatedBy + "]";
	}
}
