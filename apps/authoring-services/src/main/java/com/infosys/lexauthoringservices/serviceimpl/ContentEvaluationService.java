/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.cassandra.*;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.ContentEvaluationRepository;
import com.infosys.lexauthoringservices.util.LexConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ContentEvaluationService {

	private static final String CONTENT_ID = "contentId";
	private static final String USER_ID = "userId";
	private static final String EVALUATIONS = "evaluations";
	private static final String ROLE = "role";
	private static final String HEADER = "header";
	private static final String DESCRIPTION = "description";

	@Autowired
	ContentEvaluationRepository contentEvaluationRepository;

	
	public static SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	public Response upsert(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		String rootOrg = null;
		String org = null;
		String contentId = null;
		String userId = null;
		String header = null;
		String description = null;
		String role = null;

		ObjectMapper mapper = new ObjectMapper();

		try {
			rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			org = (String) requestBody.get(LexConstants.ORG);
			List<Map<String,Object>>  evaluations = (List<Map<String,Object>>) requestBody.get(EVALUATIONS);
//			List<ContentEvaluation>  evaluationObjs = (List<ContentEvaluation>) requestBody.get(EVALUATIONS);
//			List<ContentEvaluation> pojos = mapper.convertValue(evaluationObjs, new TypeReference<List<ContentEvaluation>>() { });
//			System.out.println("pojos : "+mapper.writeValueAsString(pojos));
//			pojos.forEach(evaluation->{
//				evaluation.setDate(formatterDateTime.format(new Date()));
//				contentEvaluationRepository.save(evaluation);
//
//			});

			for(Map<String,Object> evalution: evaluations){
				contentId = (String) evalution.get(CONTENT_ID);
				userId = (String) evalution.get(USER_ID);
				header = (String) evalution.get(HEADER);
				description = (String) evalution.get(DESCRIPTION);
				role = (String) evalution.get(ROLE);
				Map<String, String> items = (Map) evalution.get("items");
				ContentEvaluation tableModel = new ContentEvaluation(new ContentEvaluationPrimaryKey(rootOrg, org, contentId, userId, header));
				tableModel.setDate(formatterDateTime.format(new Date()));
				tableModel.setItems(items);
				//tableModel.setHeader(header);
				tableModel.setDescription(description);
				tableModel.setRole(role);
				System.out.println("ContentEvaluation : "+new ObjectMapper().writeValueAsString(tableModel));
				contentEvaluationRepository.save(tableModel);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		response.put("Message", "Successfully upserted data");
		return response;
	}

	public Response fetch(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		List<ContentEvaluation> contentEvaluationList = new ArrayList<>();

		try{
			String rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			String org = (String) requestBody.get(LexConstants.ORG);
			List<Map<String,Object>>  evaluations = (List<Map<String,Object>>) requestBody.get(EVALUATIONS);
			for(Map<String,Object> evalution: evaluations) {
				String contentId = (String) evalution.get(CONTENT_ID);
				String userId = (String) evalution.get(USER_ID);
				String header = (String) evalution.get(HEADER);

				ContentEvaluationPrimaryKey pk =new ContentEvaluationPrimaryKey(rootOrg, org, contentId, userId, header);
				Optional<ContentEvaluation> contentEvaluation = contentEvaluationRepository.findById(pk);
				contentEvaluationList.add(contentEvaluation.get());
			}

		}catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);

		}
		response.put("Message", "Successful");
		response.put("results", contentEvaluationList);
		return response;
	}

	public Response fetchByContentId(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		List<ContentEvaluation> contentEvaluationList = new ArrayList<>();

		try{
			String rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			String org = (String) requestBody.get(LexConstants.ORG);
			List<Map<String,Object>>  evaluations = (List<Map<String,Object>>) requestBody.get(EVALUATIONS);
			for(Map<String,Object> evalution: evaluations) {
				String contentId = (String) evalution.get(CONTENT_ID);
				String userId = (String) evalution.get(USER_ID);

				contentEvaluationList.addAll(contentEvaluationRepository.findById(rootOrg, org, contentId, userId));
			}

		}catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);

		}
		response.put("Message", "Successful");
		response.put("results", contentEvaluationList);
		return response;
	}

	public Response delete(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();

		try{
			String rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			String org = (String) requestBody.get(LexConstants.ORG);
			List<Map<String,Object>>  evaluations = (List<Map<String,Object>>) requestBody.get(EVALUATIONS);
			for(Map<String,Object> evalution: evaluations) {
				String contentId = (String) evalution.get(CONTENT_ID);
				String userId = (String) evalution.get(USER_ID);
				String header = (String) evalution.get(HEADER);

				ContentEvaluationPrimaryKey pk =new ContentEvaluationPrimaryKey(rootOrg, org, contentId, userId, header);
				contentEvaluationRepository.deleteById(pk);
			}

		}catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);

		}
		response.put("Message", "Successfully deleted");
		return response;
	}


	
}
