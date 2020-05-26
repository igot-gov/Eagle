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
package com.infosys.lex.exercise.service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.sunbird.repo.UserMVRepository;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.core.logger.LexLogger;
import com.infosys.lex.exercise.bodhi.repo.SubmitResult;
import com.infosys.lex.exercise.dto.SubmitDataDTO;

@Service
public class SubmitServiceImpl implements SubmitService {

	@Autowired
	ExerciseService exerciseServ;

	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	UserMVRepository userMVRepo;

	@Autowired
	ContentService contentService;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
	private LexLogger logger = new LexLogger(getClass().getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.SubmitService#submit(com.infosys.core.DTO.
	 * SubmitDataDTO, boolean, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SubmitResult submit(String rootOrg, SubmitDataDTO submitData, boolean toSubmit, String userId,
			String resourceId, String submissionOf) {
		SubmitResult submitresult = new SubmitResult();
		try {
			if (toSubmit) {

				submitresult.setResultPercent(Math.round(submitData.getResult_percent()));

				Map<String, Object> jsonMap = new HashMap<>();
				jsonMap.put("result_percent", submitData.getResult_percent());
				jsonMap.put("total_testcases", submitData.getTotal_testcases());
				jsonMap.put("testcases_passed", submitData.getTestcases_passed());
				jsonMap.put("testcases_failed", submitData.getTestcases_failed());
				jsonMap.put("response", submitData.getResponse());
				jsonMap.put("submission_type", "input");

				File convFile = null;
				convFile = File.createTempFile(
						"Submission_" + submissionOf + "_" + sdf.format(Calendar.getInstance().getTime()), ".txt");
				if (jsonMap.get("response").toString().length() == 0)
					throw new InvalidDataInputException("missing.submission");
				convFile.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(convFile);
				fos.write(jsonMap.get("response").toString().getBytes());
				fos.close();
				String url = contentService.insertFileInContentStore(convFile, "/content/Submissions/" + resourceId,
						"artifacts");
				if (url == null)
					throw new Exception("Submission not saved!");
				else {
					jsonMap.remove("response");
					jsonMap.put("url", url);
				}
				String resp = exerciseServ.insertInExercise(rootOrg, jsonMap, resourceId, userId);
				if (resp.equals(null))
					throw new Exception("Content not saved!");
				submitresult.setSubmissionMessage("Your solution is submitted!!");
				submitresult.setSubmitionStatus(true);

			} else {
				submitresult.setSubmissionMessage("Error in solution. Solution not submitted!!");
			}
		} catch (Exception e) {
			submitresult.setSubmissionMessage("Error while submitting. Try again!!");
			submitresult.setSubmitionStatus(false);
			logger.error(e);
		}
		return submitresult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.SubmitService#dbmsSubmit(com.infosys.core.DTO.
	 * SubmitDataDTO, boolean, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void dbmsSubmit(String rootOrg, SubmitDataDTO data, boolean b, String userId, String contentId,
			String string) throws Exception {
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}
		SubmitResult res = submit(rootOrg, data, true, userId, (String) contentId, "dbms");
		if (!res.isSubmitionStatus())
			throw new Exception("Error in Submission");

	}

}
