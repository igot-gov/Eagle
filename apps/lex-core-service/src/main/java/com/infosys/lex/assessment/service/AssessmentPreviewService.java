/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.assessment.service;

import java.util.Map;

import com.infosys.lex.assessment.dto.AssessmentSubmissionDTO;
public interface AssessmentPreviewService {

	Map<String, Object> getAssessmentVerifyPreview(String rootOrg, String org, String userId,
			AssessmentSubmissionDTO data) throws Exception;

}
