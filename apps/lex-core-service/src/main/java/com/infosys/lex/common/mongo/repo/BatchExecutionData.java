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
package com.infosys.lex.common.mongo.repo;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "batch_execution_collection")
public class BatchExecutionData {
	@Id
	private ObjectId id;
	@Field("batch_name")
	private String batchName;
	@Field("batch_started_on")
	private Date batchStartedOn;
	@Field("batch_ended_on")
	private Date batchEndedOn;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public Date getBatchStartedOn() {
		return batchStartedOn;
	}

	public void setBatchStartedOn(Date batchStartedOn) {
		this.batchStartedOn = batchStartedOn;
	}

	public Date getBatchEndedOn() {
		return batchEndedOn;
	}

	public void setBatchEndedOn(Date batchEndedOn) {
		this.batchEndedOn = batchEndedOn;
	}

	public BatchExecutionData(ObjectId id, String batchName, Date batchStartedOn, Date batchEndedOn) {
		super();
		this.id = id;
		this.batchName = batchName;
		this.batchStartedOn = batchStartedOn;
		this.batchEndedOn = batchEndedOn;
	}

	public BatchExecutionData() {
	}

}
