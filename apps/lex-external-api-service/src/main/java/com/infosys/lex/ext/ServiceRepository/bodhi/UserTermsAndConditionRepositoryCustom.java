package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.Date;
import java.util.Map;

public interface UserTermsAndConditionRepositoryCustom {

	Map<String, Object> getUserTermsAndCondtion(String rootOrg, Integer pageSize, String pagingSequence);

	Map<String, Object> getUserTermsAndCondtionV2(Integer pageSize, String pagingSequence, Date startDate,
			Date endDate);
}
