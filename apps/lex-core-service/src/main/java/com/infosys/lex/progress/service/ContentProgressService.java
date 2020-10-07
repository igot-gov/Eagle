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
package com.infosys.lex.progress.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.infosys.lex.progress.dto.AssessmentRecalculateDTO;
import com.infosys.lex.progress.dto.ContentProgressDTO;
import com.infosys.lex.progress.dto.ExternalProgressDTO;

public interface ContentProgressService {

	/**
	 * updates the progress and access dates for a user's content and its hierarchy
	 * 
	 * @param emailId
	 * @param contentId
	 * @param resourceInfo
	 * @return
	 * @throws Exception
	 */
	public String updateProgress(ContentProgressDTO resourceInfo) throws Exception;

	/**
	 * calls update progress asynchronously
	 * 
	 * @param email
	 * @param contentId
	 * @param userIdType
	 * @param mimeType
	 * @param result
	 * @throws Exception
	 */
	void callProgress(String rootOrg, String email, String contentId, String mimeType, Float result) throws Exception;

	Map<String, Object> metaForProgress(String rootOrg, String userUUID, List<String> idsList) throws Exception;

	public String updateAssessmentRecalculate(@Valid AssessmentRecalculateDTO progressDTO) throws Exception;

	Map<String, Object> metaForProgressForContentId(String rootOrg, String userUUID, String contentId) throws Exception;

	Map<String, Object> metaForProgressHierarchy(String rootOrg, String userUUID, String contentId) throws Exception;

	String updateExternalProgress(ExternalProgressDTO externalInfo) throws Exception;

	public Boolean getMandatoryContentStatusForUser(String rootOrg, String org, String userId);

}
