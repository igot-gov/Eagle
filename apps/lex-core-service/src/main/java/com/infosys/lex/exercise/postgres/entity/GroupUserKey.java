/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
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
package com.infosys.lex.exercise.postgres.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GroupUserKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "root_org")
	private String rootOrg;

	@Column(name = "group_id")
	private String groupId;

	@Column(name = "user_id")
	private String userId;

	public String getRootOrg() {
		return rootOrg;
	}

	
	public GroupUserKey() {
	}


	public String getGroupId() {
		return groupId;
	}


	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}


	public GroupUserKey(String rootOrg, String groupId, String userId) {
		this.rootOrg = rootOrg;
		this.groupId = groupId;
		this.userId = userId;
	}


	@Override
	public String toString() {
		return "GroupUserKey [rootOrg=" + rootOrg + ", groupId=" + groupId + ", userId=" + userId + "]";
	}

	
	
}
