package com.infosys.lex.notification.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rootOrg;

	@JsonProperty(value = "target-data")
	private Map<String, Object> targetData;

	@NotBlank
	@JsonProperty(value = "event-id")
	private String eventId;

//	@NotNull
//	@NotEmpty
	@JsonProperty(value = "tag-value-pair")
	private Map<String, Object> tagValues;

	@NotNull
	@NotEmpty
	@JsonProperty(value = "recipients")
	private Map<String, List<String>> recipients;

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public Map<String, Object> getTargetData() {
		return targetData;
	}

	public void setTargetData(Map<String, Object> targetData) {
		this.targetData = targetData;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Map<String, Object> getTagValues() {
		return tagValues;
	}

	public void setTagValues(Map<String, Object> tagValues) {
		this.tagValues = tagValues;
	}

	public Map<String, List<String>> getRecipients() {
		return recipients;
	}

	public void setRecipients(Map<String, List<String>> recipients) {
		this.recipients = recipients;
	}

	@Override
	public String toString() {
		return "NotificationEvent [rootOrg=" + rootOrg + ", targetData=" + targetData + ", eventId=" + eventId
				+ ", tagValues=" + tagValues + ", recipients=" + recipients + "]";
	}
}