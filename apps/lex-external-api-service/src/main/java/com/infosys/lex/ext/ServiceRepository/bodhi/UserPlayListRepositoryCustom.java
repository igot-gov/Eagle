package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Date;
import java.util.Map;

public interface UserPlayListRepositoryCustom {
	public Map<String, Object> getUserPlayList(String rootOrg, Integer pageSize, String pagingSequence, Date startDate,
			Date endDate, String dumpType);
}
