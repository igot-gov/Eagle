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
package com.infosys.lex.common.bodhi.repo;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthRepositoryImpl implements HealthRepository {

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	RestHighLevelClient client;

	/* (non-Javadoc)
	 * @see com.infosys.core.repository.HealthRepository#checkElasticSearch()
	 */
	@Override
	public Boolean checkElasticSearch() {
		try {
			ClusterHealthRequest request = new ClusterHealthRequest("lexcontentindex");
			ClusterHealthResponse response = client.cluster().health(request, RequestOptions.DEFAULT);

			ClusterHealthStatus status = response.getStatus();
			if (status.equals(ClusterHealthStatus.RED)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.infosys.core.repository.HealthRepository#checkContentStore(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean checkContentStore(String contentHost, String contentPort) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity("http://" + contentHost + ":" + contentPort
					+ "/content/Images/AppImages/LexHeader.png?type=artifacts", String.class);
			if (response.getStatusCodeValue() >= 300)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.infosys.core.repository.HealthRepository#checkSbExt(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean checkSbExt(String contentHost, String contentPort) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(
					"http://" + contentHost + ":" + contentPort + "/v1/application/check-connection", String.class);
			if (response.getStatusCodeValue() >= 300)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	
}