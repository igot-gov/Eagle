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

public class TenantEventConfigurationDTO implements Serializable {

	private static final long serialVersionUID = -4273885404899962961L;

//	@NotNull(message = "Group name can't be empty")
	@JsonProperty(value = "group_name")
	private String groupName;

	@NotNull(message = "Group Id can't be null")
	@NotEmpty(message = "Group Id can't be empty")
	@JsonProperty(value = "group_id")
	private String groupId;

	@NotNull(message = "events can't be null")
	@JsonProperty(value = "events")
	private List<EventsDTO> events;

	/**
	 * @return
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return
	 */
	public List<EventsDTO> getEvents() {
		return events;
	}

	/**
	 * @param events
	 */
	public void setEvents(List<EventsDTO> events) {
		this.events = events;
	}

	/**
	 * @param groupName
	 * @param events
	 */
	public TenantEventConfigurationDTO(String groupName, List<EventsDTO> events) {
		super();
		this.groupName = groupName;
		this.events = events;
	}

	public TenantEventConfigurationDTO(@NotNull(message = "Group name can't be empty") String groupName, String groupId,
			@NotNull(message = "events can't be null") List<EventsDTO> events) {
		super();
		this.groupName = groupName;
		this.groupId = groupId;
		this.events = events;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 
	 */
	public TenantEventConfigurationDTO() {
		super();
	}

	@Override
	public String toString() {
		return "TenantEventConfigurationDTO [groupName=" + groupName + ", events=" + events + "]";
	}

}
