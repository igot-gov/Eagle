/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.serviceImpl;

import java.sql.Timestamp;
import java.util.Date;

public interface GoalsHelper {
	
	/**
	 * This method validates the user learning goal type present in ULG table.
	 * 
	 * @param goalType
	 * @return
	 */
	public void validateULGoalType(String goalType);

	/**
	 * This method validates the shared goal type.
	 * 
	 * @param goalType
	 * @return
	 */
	public void validateSharedGoalType(String goalType);

	/**
	 * This method checks whether a given string is a valid UUID.
	 * 
	 * @param uuid
	 * @return
	 */
	public void validateUUID(String uuid) throws Exception;

	/**
	 * This validates the received goalType against some of the pre-defined types.
	 * 
	 * @param goalType &nbsp;Received goalType
	 * 
	 * @return Boolean depending on match result
	 */
	public void validateUserSharedGoalType(String goalType);

	/**
	 * This method is used to calculate a goal end date from given start date and
	 * duration.
	 * 
	 * @param startDate
	 * @param goalDuration
	 * @return
	 */
	public Timestamp calculateGoalEndDate(Date startDate, int goalDuration);

	/**
	 * This method gets the shared goal type based on the learning goal type.
	 * 
	 * @param goalType
	 * @return
	 */
	public String getNewSharedGoalType(String goalType);

}
