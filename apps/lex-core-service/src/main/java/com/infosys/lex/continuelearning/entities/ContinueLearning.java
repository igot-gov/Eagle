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
package com.infosys.lex.continuelearning.entities;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("continue_learning")
public class ContinueLearning {

	@PrimaryKey
	ContinueLearningKey continueLearningKey;

	private String data;

	@Column("date_accessed")
	private Long dateAccessed;

	@Column("resource_id")
	private String resourceId;

	public String getData() {
		return data;
	}

	public Long getDateAccessed() {
		return dateAccessed;
	}

	public String getResourceId() {
		return resourceId;
	}

	public ContinueLearning() {
	}

	public ContinueLearning(ContinueLearningKey continueLearningKey, String data, Long dateAccessed,
			String resourceId) {
		this.continueLearningKey = continueLearningKey;
		this.data = data;
		this.dateAccessed = dateAccessed;
		this.resourceId = resourceId;
	}

	@Override
	public String toString() {
		return "ContinueLearning [continueLearningKey=" + continueLearningKey + ", data=" + data + ", dateAccessed="
				+ dateAccessed + ", resourceId=" + resourceId + "]";
	}

}