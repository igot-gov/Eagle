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
package com.infosys.lex.badge.bodhi.repo;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("total_points")
public class TotalPoints {
	
	
	@PrimaryKey
	private TotalPointsPrimaryKey primaryKey;
	
	@Column("collaborative_points")
	private Long collaborativePoints;
	@Column("learning_points")
	private Long learningPoints;

	
	

	public Long getCollaborativePoints() {
		return collaborativePoints;
	}


	public void setCollaborativePoints(Long collaborativePoints) {
		this.collaborativePoints = collaborativePoints;
	}


	public Long getLearningPoints() {
		return learningPoints;
	}


	public void setLearningPoints(Long learningPoints) {
		this.learningPoints = learningPoints;
	}


	


	public TotalPoints(TotalPointsPrimaryKey primaryKey, Long collaborativePoints, Long learningPoints) {
		super();
		this.primaryKey = primaryKey;
		this.collaborativePoints = collaborativePoints;
		this.learningPoints = learningPoints;
	}


	public TotalPointsPrimaryKey getPrimaryKey() {
		return primaryKey;
	}


	public void setPrimaryKey(TotalPointsPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}


	public TotalPoints() {
	}

}
