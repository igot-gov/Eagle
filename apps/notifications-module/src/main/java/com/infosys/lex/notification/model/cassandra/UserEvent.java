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

package com.infosys.lex.notification.model.cassandra;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_notification_event")
public class UserEvent {

	@PrimaryKey
	private UserEventPrimaryKey primaryKey;

	@Column("modes_activated")
	private Map<String, Boolean> modes;

	@Column("group_id")
	private String groupId;

	@Column("updated_on")
	private Timestamp updatedOn;

	/**
	 * @return
	 */
	public UserEventPrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param primaryKey
	 */
	public void setPrimaryKey(UserEventPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @return
	 */
	public Map<String, Boolean> getModes() {
		return modes;
	}

	/**
	 * @param modes
	 */
	public void setModes(Map<String, Boolean> modes) {
		this.modes = modes;
	}

	/**
	 * @return
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	/**
	 * @param primaryKey
	 * @param modes
	 * @param groupId
	 */
	public UserEvent(UserEventPrimaryKey primaryKey, Map<String, Boolean> modes, String groupId, Timestamp updatedOn) {
		super();
		this.primaryKey = primaryKey;
		this.modes = modes;
		this.groupId = groupId;
		this.updatedOn = updatedOn;
	}

	/**
	 * 
	 */
	public UserEvent() {
		super();
	}

}
