package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Map;

public interface LeaderBoardRepositoryCustom {
	public Map<String, Object> getLeaderBoardsData(String rootOrg, Integer pageSize, String pagingSequence,
			int leaderBoardYear, String durationType, int durationValue, String leaderBoardType);

}
