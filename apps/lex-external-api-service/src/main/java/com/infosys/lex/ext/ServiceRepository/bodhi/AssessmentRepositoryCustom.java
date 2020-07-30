package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AssessmentRepositoryCustom {

	public Map<String, Object> fetchAssessments(String rootOrg, List<Date> dateCreated, Integer pageSize,
			String pageSequence, Date startDateTime, Date endDateTime);

	Map<String, Object> queryByDateCreatedAndTimeStamp(String rootOrg, List<Date> dateCreated, Date startDate,
			Date endDate, String page, Integer size);
}
