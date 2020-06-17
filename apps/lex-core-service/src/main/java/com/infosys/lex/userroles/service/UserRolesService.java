package com.infosys.lex.userroles.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserRolesService {

	public Set<String> getUserRoles(String rootOrg, String userId);

	public void removeRoles(String rootOrg, String userId, List<String> userRole);

	public void addRoles(String rootOrg, String userId, List<String> userRole);

	/**
	 * get all the roles of a user
	 *
	 * @param rootOrg
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getUserRolesByUserId(String rootOrg, String user_id);

	/**
	 * get details of all the user wjo have a particular role
	 *
	 * @param rootOrg
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getUserDetailsByRole(String rootOrg, String role, Integer prevPageNo, Integer pageSize);

	/**
	 * get default and master roles of a rootOrg
	 *
	 * @param rootOrg
	 * @param language
	 * @return
	 */
	public Map<String, Object> getAllRoles(String rootOrg, String language);

	/**
	 * add or delete role
	 *
	 * @param rootOrg
	 * @param userId
	 * @param role
	 * @param userRoleMap
	 * @throws Exception
	 */
	public void addOrDeleteUserRoles(String rootOrg, String userId, String role, Map<String, Object> userRoleMap);
}
