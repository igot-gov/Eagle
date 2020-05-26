/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.feedback.repo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.infosys.lex.feedback.dto.Feedback;
import com.infosys.lex.feedback.dto.FeedbackSearchDTO;

public interface FeedbackCRUD {

	public Boolean createThread(Feedback FeedBack) throws IOException;

	public List<Feedback> fetchThreads(String rootOrg, String feedbackId) throws IOException;

	public Feedback fetchThread(String feedbackId) throws IOException;

	public Boolean updateThread(String feedbackId, Map<String, Object> updateMap) throws IOException;

	public Map<String, Object> searchThreads(FeedbackSearchDTO feedbackSearchDTO, String rootOrg) throws IOException;

	public Map<String, Object> paginatedDump(String rootOrg, Long startDate, Long endDate, Integer size, String scrollId)
			throws IOException;

	public Map<String, Object> fetchHits(String userId, String roles, Integer assign) throws IOException;

}
