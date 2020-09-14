package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserSharedGoalsRepositoryCustom {

	public Map<String, Object> getUserSharedGoals(String rootOrg, Integer pageSize, String pagingSequence,
			Date startDate, Date endDate, String dumpType);

	public List<Map<String, Object>> getUserSharedGoalsByEmail(String userEmail, String goalType, Set<String> goalIds);
}
