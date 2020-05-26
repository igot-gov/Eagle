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
package com.infosys.lex.common.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.infosys.lex.badge.bodhi.repo.TotalPointsRepository;
//import com.infosys.lex.badge.bodhi.repo.ConstantBadgeRepository;
import com.infosys.lex.common.bodhi.repo.HealthRepository;
import com.infosys.lex.common.mongo.repo.BatchExecutionRepository;

import com.infosys.lex.common.util.LexServerProperties;

@Service
public class HealthServiceImpl implements HealthService {

//	@Autowired
//	ConstantBadgeRepository constantBadgeRepo;

	@Autowired
	BatchExecutionRepository batchExecutionRepo;

	@Autowired
	HealthRepository healthRepo;

	@Autowired
	LexServerProperties lexServerProperties;

	@Autowired
	TotalPointsRepository pointsRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.HealthService#checkHealth()
	 */
	@Override
	public Map<String, Object> checkHealth() throws Exception {
		Map<String, Object> healthMap = new HashMap<String, Object>();
		String sbExtHost = lexServerProperties.getSbextServiceHost();
		String contentHost = lexServerProperties.getContentServiceHost();
		String contentPort = lexServerProperties.getBodhiContentPort();
		String sbExtPort = lexServerProperties.getSbextPort();

		healthMap.put("cassandra", this.checkBodhiCassandra());
		healthMap.put("mongo", this.checkMongo());
		healthMap.put("elastic", this.checkElastic());
		healthMap.put("content_store", this.checkContentStore(contentHost, contentPort));
		healthMap.put("sb_ext", this.checkSbExt(sbExtHost, sbExtPort));
		healthMap.put("application", true);
		return healthMap;
	}

	private Boolean checkBodhiCassandra() {
		try {
			return pointsRepo.findPointsLimitOne() != null ? true : false;
		} catch (Exception e) {
			return false;
		}
	}

	private Boolean checkMongo() {
		try {
			batchExecutionRepo.findByBatchName("badge_batch3",
					PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "batch_started_on")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Boolean checkElastic() {
		try {
			return healthRepo.checkElasticSearch();
		} catch (Exception e) {
			return false;
		}
	}

	private Boolean checkContentStore(String contentHost, String contentPort) {
		try {
			return healthRepo.checkContentStore(contentHost, contentPort);
		} catch (Exception e) {
			return false;
		}
	}

	private Boolean checkSbExt(String contentHost, String contentPort) {
		try {
			return healthRepo.checkSbExt(contentHost, contentPort);
		} catch (Exception e) {
			return false;
		}
	}

}
