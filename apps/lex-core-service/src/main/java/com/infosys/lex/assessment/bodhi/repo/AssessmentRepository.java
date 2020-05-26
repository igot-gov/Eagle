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
package com.infosys.lex.assessment.bodhi.repo;

import java.util.List;
import java.util.Map;

import com.infosys.lex.assessment.dto.AssessmentSubmissionDTO;

public interface AssessmentRepository {

	/**
	 * gets answer key for the assessment given the url
	 * 
	 * @param artifactUrl
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getAssessmentAnswerKey(String artifactUrl) throws Exception;

	/**
	 * gets answerkey for the quiz submission
	 * 
	 * @param quizMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getQuizAnswerKey(AssessmentSubmissionDTO quizMap) throws Exception;

	/**
	 * inserts quiz or assessments for a user
	 * 
	 * @param persist
	 * @param isAssessment
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> insertQuizOrAssessment(Map<String, Object> persist, Boolean isAssessment)
			throws Exception;

	/**
	 * gets assessment for a user given a content id
	 * 
	 * @param courseId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getAssessmetbyContentUser(String rootOrg, String courseId, String userId)
			throws Exception;

}
