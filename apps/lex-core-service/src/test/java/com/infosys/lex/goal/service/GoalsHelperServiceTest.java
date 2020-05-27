/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.infosys.lex.core.exception.InvalidDataInputException;

@ExtendWith(SpringExtension.class)
public class GoalsHelperServiceTest {

	@InjectMocks
	GoalsHelper goalsService = new GoalsHelperImpl();

	@Test
	public void helperTestForCommon() {
		goalsService.validateULGoalType("common");
	}

	@Test
	public void helperTestForUser() {
		goalsService.validateULGoalType("user");
	}

	@Test
	public void helperTestForCommonShared() {
		goalsService.validateULGoalType("commonshared");
	}

	@Test
	public void helperTestForToBeShared() {
		goalsService.validateULGoalType("tobeshared");
	}

	@Test
	public void helperTestForNull() {
		assertThrows(InvalidDataInputException.class, () -> {
			goalsService.validateULGoalType(null);
		});
	}

	@Test
	public void helperTestForEmpty() {
		assertThrows(InvalidDataInputException.class, () -> {
			goalsService.validateULGoalType("");
		});
	}

	@Test
	public void helperTestForOther() {
		assertThrows(InvalidDataInputException.class, () -> {
			goalsService.validateULGoalType("test");
		});
	}

}