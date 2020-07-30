package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Date;
import java.util.Map;

public interface SharedPlayListCustomRepository {
	public Map<String, Object> getSharedPlayLists(String rootOrg, Integer pageSize, String pagingSequence,
			Date startDate, Date endDate, String dumpType);

}
