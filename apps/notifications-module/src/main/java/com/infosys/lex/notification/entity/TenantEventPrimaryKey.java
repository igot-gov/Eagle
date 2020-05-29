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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TenantEventPrimaryKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "root_org", columnDefinition = "VARCHAR")
	private String rootOrg;

	@Column(name = "org", columnDefinition = "VARCHAR")
	private String org;

	@Column(name = "event_id", columnDefinition = "VARCHAR")
	private String eventId;

	@Column(name = "recipient", columnDefinition = "VARCHAR")
	private String recipient;

	@Column(name = "mode", columnDefinition = "VARCHAR")
	private String mode;

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public TenantEventPrimaryKey(String rootOrg, String org, String eventId, String recipient, String mode) {
		super();
		this.rootOrg = rootOrg;
		this.org = org;
		this.eventId = eventId;
		this.recipient = recipient;
		this.mode = mode;
	}

	public TenantEventPrimaryKey() {
		super();
	}

	@Override
	public String toString() {
		return "TenantEventPrimaryKey [rootOrg=" + rootOrg + ", org=" + org + ", eventId=" + eventId + ", recipient="
				+ recipient + ", mode=" + mode + "]";
	}

}
