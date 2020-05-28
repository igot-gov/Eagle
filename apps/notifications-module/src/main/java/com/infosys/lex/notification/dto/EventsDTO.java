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

package com.infosys.lex.notification.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventsDTO implements Serializable {

	private static final long serialVersionUID = 3735972168347309694L;

	@NotNull(message = "event Id can't be null")
	@NotEmpty(message = "event Id can't be empty")
	@JsonProperty(value = "event_id")
	private String eventId;

	@JsonProperty(value = "event_name")
	private String eventName;

	@NotNull(message = "recipients can't be left blank")
	@JsonProperty(value = "recipients")
	private List<RecipientsDTO> recipients;

	/**
	 * @return
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventName
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return
	 */
	public List<RecipientsDTO> getRecipients() {
		return recipients;
	}

	/**
	 * @param recipients
	 */
	public void setRecipients(List<RecipientsDTO> recipients) {
		this.recipients = recipients;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @param eventName
	 * @param recipients
	 */
	public EventsDTO(String eventId, List<RecipientsDTO> recipients) {
		super();
		this.eventId = eventId;
		this.recipients = recipients;
	}

	public EventsDTO(@NotNull(message = "event Id can't be empty") String eventId, String eventName,
			@NotNull(message = "recipients can't be left blank") List<RecipientsDTO> recipients) {
		super();
		this.eventId = eventId;
		this.eventName = eventName;
		this.recipients = recipients;
	}

	/**
	 * 
	 */
	public EventsDTO() {
		super();
	}

	@Override
	public String toString() {
		return "EventsDTO [eventId=" + eventId + ", eventName=" + eventName + " ,recipients=" + recipients + "]";
	}

}
