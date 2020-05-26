/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.bodhi.repo;

import java.util.List;

public interface SharedGoalCustomRepository {

	/**
	 * bulk executes goals insertion statements
	 * 
	 * @param statements
	 */
	public void bulkInsert(List<SharedGoal> sharedGoal);
}
