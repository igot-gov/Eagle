package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserLearningGoalsRepositoryCustom {

	public Map<String, Object> getUserLearningGoals(String rootOrg, Integer pageSize, String pagingSequence,
			Date startDate, Date endDate, String dumpType);

	public List<Map<String, Object>> getUserLearningGoalsByEmail(String userEmail, String goalType,
			Set<String> goalIds);
}
