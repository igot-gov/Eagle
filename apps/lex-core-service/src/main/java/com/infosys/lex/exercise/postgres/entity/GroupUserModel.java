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

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "group_user_mapping", schema = "wingspan")
public class GroupUserModel {

	@EmbeddedId
	GroupUserKey key;



	@Column(name = "date_created")
	private Timestamp dateCreated;


	public GroupUserKey getKey() {
		return key;
	}



	public void setKey(GroupUserKey key) {
		this.key = key;
	}










	public Timestamp getDateCreated() {
		return dateCreated;
	}



	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}



	public GroupUserModel() {
		super();
		// TODO Auto-generated constructor stub
	}



	public GroupUserKey getkey() {
		return key;
	}



	public GroupUserModel(GroupUserKey key, Timestamp dateCreated) {
		this.key = key;
		this.dateCreated = dateCreated;
	}



	@Override
	public String toString() {
		return "GroupUserMapping [key=" + key +  ", dateCreated=" + dateCreated + "]";
	}

	
	

}
