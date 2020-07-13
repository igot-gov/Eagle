/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.serviceimpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.cassandra.*;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.ContentEvaluationRepository;
import com.infosys.lexauthoringservices.util.LexConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ContentEvaluationService {

	private static final String CONTENT_ID = "contentId";
	private static final String USER_ID = "userId";
	private static final String EVALUATIONS = "evaluations";


	@Autowired
	ContentEvaluationRepository contentEvaluationRepository;

	
	public static SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	public Response upsert(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		String rootOrg = null;
		String org = null;
		String contentId = null;
		String userId = null;
		ContentEvaluation contentEvaluation = null;


		List<AssesmentItem> assesmentItems = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();

		try {
			rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			org = (String) requestBody.get(LexConstants.ORG);
			contentId = (String) requestBody.get(CONTENT_ID);
			userId = (String) requestBody.get(USER_ID);
			String evaluation = (String)requestBody.get(EVALUATIONS);


			if (rootOrg == null || rootOrg.isEmpty() || org == null || org.isEmpty() || contentId == null
					|| contentId.isEmpty() || userId == null || userId.isEmpty() || evaluation ==null || evaluation.isEmpty()) {
				throw new BadRequestException("Invalid Request Body");
			}

//			List<String> assesmentList = (List<String>) requestBody.get(EVALUATIONS);
//			for(String assesment:assesmentList){
//			}


			assesmentItems = mapper.readValue(evaluation, new TypeReference<List<AssesmentItem>>() {});

			for(AssesmentItem assesmentItem : assesmentItems){
				System.out.println("assesmentItem : "+mapper.writeValueAsString(assesmentItem));
			}

			ContentEvaluationPrimaryKey primaryKey = new ContentEvaluationPrimaryKey(rootOrg, org, contentId, userId);
			ContentEvaluation tableModel = new ContentEvaluation(assesmentItems, primaryKey);
			tableModel.setDate(formatterDateTime.format(new Date()));
			contentEvaluationRepository.save(tableModel);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		response.put("Message", "Successfully upserted data");
		return response;
	}


	
}
