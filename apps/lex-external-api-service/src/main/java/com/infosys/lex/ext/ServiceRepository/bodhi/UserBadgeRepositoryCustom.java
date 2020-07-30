package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Date;
import java.util.Map;

public interface UserBadgeRepositoryCustom {

	Map<String, Object> queryByTimeStamp(String rootOrg, Date startDate, Date endDate, String page, Integer size);

	public Map<String, Object> getBadges(String rootOrg, Integer pageSize, String pagingSequence, Date startDate,
			Date endDate);
}
