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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventTemplateDTO {

	@JsonProperty(value = "event_id")
	private String eventId;

	@JsonProperty(value = "template_id")
	private String templateId;

	@NotEmpty(message = "template text can't be empty")
	@NotNull(message = "template text can't be null")
	@JsonProperty(value = "template_text")
	private String templateText;

	@JsonProperty(value = "template_subject")
	private String templateSubject;

	private String mode;

	private String recipient;

	public String getMode() {
		return mode;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getTemplateId() {
		return templateId;
	}

	public String getTemplateText() {
		return templateText;
	}

	public String getTemplateSubject() {
		return templateSubject;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}

	public void setTemplateSubject(String templateSubject) {
		this.templateSubject = templateSubject;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public EventTemplateDTO() {
		super();
	}

	public EventTemplateDTO(String templateId,
			@NotEmpty(message = "template text can't be empty") @NotNull(message = "template text can't be null") String templateText,
			String templateSubject) {
		super();
		this.templateId = templateId;
		this.templateText = templateText;
		this.templateSubject = templateSubject;
	}

	public EventTemplateDTO(String eventId, String templateId,
			@NotEmpty(message = "template text can't be empty") @NotNull(message = "template text can't be null") String templateText,
			String templateSubject, String mode, String recipient) {
		super();
		this.eventId = eventId;
		this.templateId = templateId;
		this.templateText = templateText;
		this.templateSubject = templateSubject;
		this.mode = mode;
		this.recipient = recipient;
	}

	@Override
	public String toString() {
		return "TemplateDTO [templateId=" + templateId + ", templateText=" + templateText + ", templateSubject="
				+ templateSubject + "]";
	}
}
