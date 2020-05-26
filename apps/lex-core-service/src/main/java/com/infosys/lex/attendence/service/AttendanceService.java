/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.attendence.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AttendanceService {

	public Map<String,Object> verifyUserAttencdance(String rootOrg,String userId,List<String> contentIds) throws Exception;

	public List<Map<String,Object>> fetchAttendedContent(String rootOrg,String userId,List<String> sourceFields) throws IOException, Exception;

	public List<Map<String, Object>> fetchCohorts(String rootOrg, String contentId);

}
