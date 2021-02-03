package org.sunbird.common.service;

import java.util.List;
import java.util.Map;

import org.sunbird.core.exception.ApplicationLogicError;

public interface UserUtilityService {

	boolean validateUser(String rootOrg, String userId) throws ApplicationLogicError;

	Map<String, Object> getUsersDataFromUserIds(String rootOrg, List<String> userIds, List<String> source);

}
