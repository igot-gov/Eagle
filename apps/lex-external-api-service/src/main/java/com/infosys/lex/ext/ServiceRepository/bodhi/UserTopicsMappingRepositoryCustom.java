package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Date;
import java.util.Map;

public interface UserTopicsMappingRepositoryCustom {
	public Map<String, Object> getInterests(String rootOrg, Integer pageSize, String pagingSequence, Date startDate,
			Date endDate);

}
