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
@Table(name = "tenant_event_notification", schema = "wingspan")
public class TenantEvent {

	@EmbeddedId
	private TenantEventPrimaryKey primarykey;

	@Column(name = "mode_activated")
	private Boolean modeActivated;

//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "template_id", unique = true)
	@Column(name = "template_id")
	private String templateId;

	@Column(name = "updated_on")
	private Timestamp updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "receiver_emails")
	private String receiverEmail;

	public TenantEventPrimaryKey getPrimarykey() {
		return primarykey;
	}

	public void setPrimarykey(TenantEventPrimaryKey primarykey) {
		this.primarykey = primarykey;
	}

	public Boolean getModeActivated() {
		return modeActivated;
	}

	public void setModeActivated(Boolean modeActivated) {
		this.modeActivated = modeActivated;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
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

	public String getRecieverEmail() {
		return receiverEmail;
	}

	public void setRecieverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}

	public TenantEvent(TenantEventPrimaryKey primarykey, Boolean modeActivated, String templateId, Timestamp updatedOn,
			String updatedBy, String receiverEmail) {
		this.primarykey = primarykey;
		this.modeActivated = modeActivated;
		this.templateId = templateId;
		this.updatedOn = updatedOn;
		this.updatedBy = updatedBy;
		this.receiverEmail = receiverEmail;
	}

	public TenantEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	
}
