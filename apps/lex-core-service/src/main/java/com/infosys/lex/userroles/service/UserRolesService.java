/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.userroles.service;

import java.util.List;
import java.util.Set;

public interface UserRolesService {

	public Set<String> getUserRoles(String rootOrg, String userId) throws Exception;

	public void removeRoles(String rootOrg, String userId, List<String> userRole) throws Exception;

	public void addRoles(String rootOrg, String userId, List<String> userRole) throws Exception;
}
