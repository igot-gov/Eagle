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
public class RecipientTagsPrimaryKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1466394203418413785L;

	@Column(name = "event_id", columnDefinition = "VARCHAR")
	private String eventId;
	
	@Column(name = "recipient", columnDefinition = "VARCHAR")
	private String recipient;

	public String getEventId() {
		return eventId;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public RecipientTagsPrimaryKey() {
		super();
	}

	public RecipientTagsPrimaryKey(String eventId, String recipient) {
		super();
		this.eventId = eventId;
		this.recipient = recipient;
	}

	@Override
	public String toString() {
		return "RecipientTagsPrimaryKey [eventId=" + eventId + ", recipient=" + recipient + "]";
	}
}
