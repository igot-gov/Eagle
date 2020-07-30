/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential

*/
package com.infosys.lex.ext.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.infosys.lex.ext.exception.ApplicationLogicError;

public interface UserUtilityService {

	String getUserDataSource(String rootOrg);

	boolean validateUser(String rootOrg, String userId) throws Exception;

	Map<String, Object> getUserIdForEmail(String rootOrg, List<String> email, List<String> source);

	Map<String, Object> getUsersDataFromUserIds(String rootOrg, List<String> userIds, List<String> source);

	List<Map<String, Object>> getContentMeta(List<String> ids, List<String> source, List<String> statusList)
			throws ApplicationLogicError, IOException;

}
