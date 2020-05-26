/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
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
package com.infosys.lex.goal.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.infosys.lex.core.exception.InvalidDataInputException;

@Service
public class GoalsHelperImpl implements GoalsHelper {

	@Override
	public void validateULGoalType(String goalType) {

		if (goalType == null || goalType.isEmpty()) {
			throw new InvalidDataInputException("goaltype.emptyOrNull");
		}

		if (!Arrays.asList("user", "common", "commonshared", "tobeshared").contains(goalType.toLowerCase())) {
			throw new InvalidDataInputException("invalid.goaltype");
		}
	}

	@Override
	public void validateSharedGoalType(String goalType) {

		if (goalType == null || goalType.isEmpty()) {
			throw new InvalidDataInputException("goaltype.emptyOrNull");
		}

		if (!Arrays.asList("commonshared", "tobeshared", "custom_shared", "common_shared")
				.contains(goalType.toLowerCase())) {
			throw new InvalidDataInputException("invalid.goaltype");
		}
	}

	@Override
	public void validateUUID(String uuid) throws Exception {

		try {
			UUID.fromString(uuid);
		} catch (Exception e) {
			throw new InvalidDataInputException("invalid.UUID");
		}
	}

	@Override
	public void validateUserSharedGoalType(String goalType) {

		List<String> possibleGoalTypes = Arrays.asList(new String[] { "common_shared", "custom_shared" });
		if (!possibleGoalTypes.contains(goalType)) {
			throw new InvalidDataInputException("invalid.sharedgoaltype");
		}
		return;
	}

	@Override
	public Timestamp calculateGoalEndDate(Date startDate, int goalDuration) {

		Timestamp endTs = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, goalDuration);
		endDate = cal.getTime();
		Date currentDate = Calendar.getInstance().getTime();
		// If end date was before today, set endDate as today.
		if (endDate.before(currentDate)) {
			endDate = currentDate;
		}
		endTs = new Timestamp(endDate.getTime());
		return endTs;
	}

	@Override
	public String getNewSharedGoalType(String goalType) {

		String sharedGoalType = "";
		if (goalType.equalsIgnoreCase("commonshared")) {
			sharedGoalType = "common_shared";
		} else if (goalType.equalsIgnoreCase("tobeshared")) {
			sharedGoalType = "custom_shared";
		} else {
			sharedGoalType = goalType;
		}
		return sharedGoalType;
	}

}
