/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.timespent.service;

import java.util.Map;
public interface TimeSpentService {

	public Map<String,Object> getUserDashboard(String rootorg ,String uuid,String startDate,String endDate) throws Exception;
}
