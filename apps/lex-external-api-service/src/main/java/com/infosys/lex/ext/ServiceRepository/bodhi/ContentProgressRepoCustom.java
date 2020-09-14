package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ContentProgressRepoCustom {
	public Map<String, Object> fetchProgress(String rootOrg, Integer size, String page, List<Date> filterDates);
}
