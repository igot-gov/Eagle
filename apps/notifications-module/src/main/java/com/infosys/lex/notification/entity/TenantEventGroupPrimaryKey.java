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
public class TenantEventGroupPrimaryKey implements Serializable{
	private static final long serialVersionUID = 4239206353368766756L;

	@Column(name="group_id")
	private String groupId;
	
	@Column(name="event_id")
	private String eventId;

	public String getGroup_id() {
		return groupId;
	}

	public void setGroup_id(String groupId) {
		this.groupId = groupId;
	}

	public String getEvent_id() {
		return eventId;
	}

	public void setEvent_id(String eventId) {
		this.eventId = eventId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public TenantEventGroupPrimaryKey(String groupId, String eventId) {
		super();
		this.groupId = groupId;
		this.eventId = eventId;
	}

	public TenantEventGroupPrimaryKey() {
		super();
	}

	@Override
	public String toString() {
		return "EventGroupPrimaryKey [groupId=" + groupId + ", eventId=" + eventId + "]";
	}
	
	
}
