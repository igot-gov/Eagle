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
package com.infosys.lex.cohort.bodhi.repo;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("educators")
public class EducatorsModel {

	@PrimaryKey
	private EducatorsPrimaryKeyModel primaryKey;

	@Column("date_created")
	private Date dateCreated;

	@Column("date_modified")
	private Date dateModified;

	

	public EducatorsPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(EducatorsPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	

	public EducatorsModel(EducatorsPrimaryKeyModel primaryKey, Date dateCreated, Date dateModified) {
		super();
		this.primaryKey = primaryKey;
		this.dateCreated = dateCreated;
		this.dateModified = dateModified;
	
	}

	public EducatorsModel() {
		super();
	}

	@Override
	public String toString() {
		return "EducatorsModel [primaryKey=" + primaryKey + ", dateCreated=" + dateCreated + ", dateModified="
				+ dateModified + "]";
	}

}
