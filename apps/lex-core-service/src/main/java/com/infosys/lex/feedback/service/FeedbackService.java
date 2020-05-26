/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.feedback.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.infosys.lex.feedback.dto.FeedbackSearchDTO;
import com.infosys.lex.feedback.dto.FeedbackSubmitDTO;

public interface FeedbackService {

	public Map<String, Object> submitFeedback(FeedbackSubmitDTO feedBackSubmitDTO, String rootOrg,String role) throws Exception;
	
	public List<Map<String,Object>> fetchFeedbacks(String feedbackId,String userId,String rootOrg) throws Exception;
	
	public Map<String, Object> updateStatus(String userId, String feedbackId, String rootOrg, String category) throws Exception;

	public Map<String, Object> fetchLatestFeedbacksReport(String userId, String rootOrg) throws IOException, Exception;

	public Map<String,Object> getFeedbackCategory(String rootOrg) throws Exception;

	public Map<String, Object> searchFeedback(FeedbackSearchDTO feedbackSearchDTO, String rootOrg) throws Exception;

	public Map<String, Object> feedbackDump(String rootOrg, Long startDate, Long endDate, Integer size, String scrollId)
			throws IOException;
}
