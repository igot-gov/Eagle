///*               "Copyright 2020 Infosys Ltd.
//               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
//               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
//substitute url based on requirement
////
////import java.util.ArrayList;
////import java.util.Arrays;
////import java.util.Date;
////import java.util.List;
////import java.util.Optional;
////import java.util.UUID;
////
////import org.junit.jupiter.api.DisplayName;
////import org.junit.jupiter.api.Test;
////import org.junit.jupiter.api.extension.ExtendWith;
////import org.mockito.InjectMocks;
////import org.mockito.Mock;
////import org.mockito.Mockito;
////import org.springframework.test.context.junit.jupiter.SpringExtension;
////
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
////
////@ExtendWith(SpringExtension.class)
////public class GoalsServiceTest {
////
////	@Mock
////	private GoalsHelper helper;
////
////	@Mock
////	private UserGoalRepository learningGoalsRepo;
////
////	@Mock
////	private CommonGoalRepository commonGoalsRepo;
////
////	@Mock
////	private SharedGoalRepository sharedGoalRepo;
////
////	@InjectMocks
////	GoalsService goalsService = new GoalsServiceImpl();
////
////	Date dt = null;
////
////	UserGoalKey key = null;
////
////	UserGoal goal = null;
////
////	GoalDTO data = null;
////
////	CommonGoal commonGoal = null;
////
////	SharedGoalKey sharedKey = null;
////
////	SharedGoal sharedGoal = null;
////
////	List<SharedGoal> sharedGoals = new ArrayList<>();
////
////	List<String> recipientList = null;
////
////	ActionDTO actionData = null;
////
//////----------------------------GOAL CREATION TESTS----------------------------------------------
//////------------------------------------------------------------------------------------
//////----------------------------------------1. Common goal for self creation----------------------
////
////	// Test for empty common goalId
////	@DisplayName("Goal creation - Test for empty common goalId")
////	@Test
////	public void testcreateGoalServiceMethodNegative_1() {
////
////		this.setupdataNegative_1();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////
//////		assertThrows(InvalidDataInputException.class, () -> {
//////			goalsService.createLearningGoal(data, "EMAIL");
//////		});
////	}
////
////	private void setupdataNegative_1() {
////
////		data = new GoalDTO("", "common", "Learn skills to play the role of Scrum master in projectsxa0", 5,
//substitute url based on requirement
////	}
////
////	// Test for null common goalId
////	@DisplayName("Goal creation - Test for null common goalId")
////	@Test
////	public void testcreateGoalServiceMethodNegative_2() {
////
////		this.setupdataNegative_2();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////
//////		assertThrows(InvalidDataInputException.class, () -> {
//////			goalsService.createLearningGoal(data, "EMAIL");
//////		});
////	}
////
////	private void setupdataNegative_2() {
////
////		data = new GoalDTO(null, "common", "Learn skills to play the role of Scrum master in projectsxa0", 5,
//substitute url based on requirement
////	}
////
////	// Test for invalid common goal
////	@DisplayName("Goal creation - Test for invalid common goal")
////	@Test
////	public void testcreateGoalServiceMethodNegative_3() {
////
////		this.setupdataNegative_3();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////
//////		assertThrows(InvalidDataInputException.class, () -> {
//////			goalsService.createLearningGoal(data, "EMAIL");
//////		});
////	}
////
////	private void setupdataNegative_3() {
////
////		data = new GoalDTO("e19595e8-3814-4357-bff8-d82a8245", "common",
////				"Learn skills to play the role of Scrum master in projectsxa0", 5, "Become a Scrum Master",
//substitute url based on requirement
////	}
////
////	// Test for common goal already exists for the user
////	@DisplayName("Goal creation - Test for common goal already exists for the user")
////	@Test
////	public void testcreateGoalServiceMethodNegative_4() {
////
////		this.setupdataNegative_4();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////		Mockito.when(commonGoalsRepo.findById(UUID.fromString(data.getGoalId()))).thenReturn(Optional.of(commonGoal));
////		Mockito.when(learningGoalsRepo.existsById(key)).thenReturn(true);
////		Mockito.when(
////				sharedGoalRepo.getGoalBySharedWithGoalTypeAndStatus("EMAIL", "common_shared", 1))
////				.thenReturn(null);
////
//////		assertThrows(InvalidDataInputException.class, () -> {
//////			goalsService.createLearningGoal(data, "EMAIL");
//////		});
////	}
////
////	private void setupdataNegative_4() {
////
////		data = new GoalDTO("e19595e8-3814-4357-bff8-d82a824597e1", "common",
////				"Learn skills to play the role of Scrum master in projectsxa0", 5, "Become a Scrum Master",
//substitute url based on requirement
////
////		dt = new Date();
////		commonGoal = new CommonGoal(UUID.fromString("e19595e8-3814-4357-bff8-d82a824597e1"), dt,
//substitute url based on requirement
////				"Learn skills to play the role of Scrum master in projectsxa0", "other", "Become a Scrum Master");
////
////		key = new UserGoalKey("EMAIL", data.getGoalType(), UUID.fromString(data.getGoalId()));
////	}
////
////	// Test for common goal is shared and user has already accepted
////	@DisplayName("Goal creation - Test for common goal is shared and user has already accepted")
////	@Test
////	public void testcreateGoalServiceMethodNegative_5() {
////
////		this.setupdataNegative_5();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////		Mockito.when(commonGoalsRepo.findById(UUID.fromString(data.getGoalId()))).thenReturn(Optional.of(commonGoal));
////		Mockito.when(learningGoalsRepo.existsById(key)).thenReturn(false);
////		Mockito.when(
////				sharedGoalRepo.getGoalBySharedWithGoalTypeAndStatus("EMAIL", "common_shared", 1))
////				.thenReturn(sharedGoals);
////
//////		assertThrows(InvalidDataInputException.class, () -> {
//////			goalsService.createLearningGoal(data, "EMAIL");
//////		});
////	}
////
////	private void setupdataNegative_5() {
////
////		data = new GoalDTO("e19595e8-3814-4357-bff8-d82a824597e1", "common",
////				"Learn skills to play the role of Scrum master in projectsxa0", 5, "Become a Scrum Master",
//substitute url based on requirement
////
////		dt = new Date();
////		sharedKey = new SharedGoalKey("EMAIL", "common_shared",
////				UUID.fromString("e19595e8-3814-4357-bff8-d82a824597e1"), "EMAIL");
//substitute url based on requirement
////				"Learn skills to play the role of Scrum master in projectsxa0", 5, dt, dt, "Become a Scrum Master", dt,
////				dt, 1, "");
////		sharedGoals.add(sharedGoal);
////
////		commonGoal = new CommonGoal(UUID.fromString("e19595e8-3814-4357-bff8-d82a824597e1"), dt,
//substitute url based on requirement
////				"Learn skills to play the role of Scrum master in projectsxa0", "other", "Become a Scrum Master");
////
////		key = new UserGoalKey("EMAIL", data.getGoalType(), UUID.fromString(data.getGoalId()));
////	}
////
////	// Test for successful common goal creation
////	@DisplayName("Goal creation - Test for successful common goal creation for self")
////	@Test
////	public void testcreateGoalServiceMethodPositive_1() {
////
////		this.setupdataPositive_1();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////		Mockito.when(commonGoalsRepo.findById(UUID.fromString(data.getGoalId()))).thenReturn(Optional.of(commonGoal));
////		Mockito.when(learningGoalsRepo.existsById(key)).thenReturn(false);
////		Mockito.when(
////				sharedGoalRepo.getGoalBySharedWithGoalTypeAndStatus("EMAIL", "common_shared", 1))
////				.thenReturn(null);
////
//////		goalsService.createLearningGoal(data, "EMAIL");
////	}
////
////	private void setupdataPositive_1() {
////
////		data = new GoalDTO("e19595e8-3814-4357-bff8-d82a824597e1", "common",
////				"Learn skills to play the role of Scrum master in projectsxa0", 5, "Become a Scrum Master",
//substitute url based on requirement
////		dt = new Date();
////		commonGoal = new CommonGoal(UUID.fromString("e19595e8-3814-4357-bff8-d82a824597e1"), dt,
//substitute url based on requirement
////				"Learn skills to play the role of Scrum master in projectsxa0", "other", "Become a Scrum Master");
////		key = new UserGoalKey("EMAIL", "common",
////				UUID.fromString("e19595e8-3814-4357-bff8-d82a824597e1"));
////	}
////
//////-------------------------------2. Common goal for others creation (commonshared)---------------
////	// Test for empty common goalId
////	@DisplayName("Goal creation - Test for empty commonshared goalId")
////	@Test
////	public void testcreateGoalServiceMethodNegative_6() {
////
////		this.setupdataNegative_6();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////
//////		assertThrows(InvalidDataInputException.class, () -> {
//////			goalsService.createLearningGoal(data, "EMAIL");
//////		});
////	}
////
////	private void setupdataNegative_6() {
////
////		data = new GoalDTO("", "commonshared", "Learn skills to play the role of Scrum master in projectsxa0", 5,
//substitute url based on requirement
////	}
////
////	// Test for null common goalId
////	@DisplayName("Goal creation - Test for null commonshared goalId")
////	@Test
////	public void testcreateGoalServiceMethodNegative_7() {
////
////		this.setupdataNegative_7();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////
//////		assertThrows(InvalidDataInputException.class, () -> {
//////			goalsService.createLearningGoal(data, "EMAIL");
//////		});
////	}
////
////	private void setupdataNegative_7() {
////
////		data = new GoalDTO(null, "commonshared", "Learn skills to play the role of Scrum master in projectsxa0", 5,
//substitute url based on requirement
////	}
////
////	// Test for invalid common goal
////	@DisplayName("Goal creation - Test for invalid commonshared goal")
////	@Test
////	public void testcreateGoalServiceMethodNegative_8() {
////
////		this.setupdataNegative_8();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////
//////		assertThrows(InvalidDataInputException.class, () -> {
//////			goalsService.createLearningGoal(data, "EMAIL");
//////		});
////	}
////
////	private void setupdataNegative_8() {
////
////		data = new GoalDTO("e19595e8-3814-4357-bff8-d82a8245", "commonshared",
////				"Learn skills to play the role of Scrum master in projectsxa0", 5, "Become a Scrum Master",
//substitute url based on requirement
////	}
////
////	// Test for common goal already exists for the user
////	@DisplayName("Goal creation - Test for commonshared goal already exists for the user")
////	@Test
////	public void testcreateGoalServiceMethodNegative_9() {
////
////		this.setupdataNegative_9();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////		Mockito.when(commonGoalsRepo.findById(UUID.fromString(data.getGoalId()))).thenReturn(Optional.of(commonGoal));
////		Mockito.when(learningGoalsRepo.existsById(key)).thenReturn(true);
////
//////		assertThrows(InvalidDataInputException.class, () -> {
//////			goalsService.createLearningGoal(data, "EMAIL");
//////		});
////	}
////
////	private void setupdataNegative_9() {
////
////		data = new GoalDTO("e19595e8-3814-4357-bff8-d82a824597e1", "commonshared",
////				"Learn skills to play the role of Scrum master in projectsxa0", 5, "Become a Scrum Master",
//substitute url based on requirement
////		dt = new Date();
////		commonGoal = new CommonGoal(UUID.fromString("e19595e8-3814-4357-bff8-d82a824597e1"), dt,
//substitute url based on requirement
////				"Learn skills to play the role of Scrum master in projectsxa0", "other", "Become a Scrum Master");
////		key = new UserGoalKey("EMAIL", data.getGoalType(), UUID.fromString(data.getGoalId()));
////	}
////
////	// Test for successful common goal creation
////	@DisplayName("Goal creation - Test for successful common goal for others creation")
////	@Test
////	public void testcreateGoalServiceMethodPositive_2() {
////
////		this.setupdataPositive_2();
////
////		Mockito.doNothing().when(helper).validateULGoalType(data.getGoalType());
////		Mockito.when(commonGoalsRepo.findById(UUID.fromString(data.getGoalId()))).thenReturn(Optional.of(commonGoal));
////		Mockito.when(learningGoalsRepo.existsById(key)).thenReturn(false);
////
//////		goalsService.createLearningGoal(data, "EMAIL");
////	}
////
////	private void setupdataPositive_2() {
////
////		data = new GoalDTO("e19595e8-3814-4357-bff8-d82a824597e1", "commonshared",
////				"Learn skills to play the role of Scrum master in projectsxa0", 5, "Become a Scrum Master",
//substitute url based on requirement
////		dt = new Date();
////		commonGoal = new CommonGoal(UUID.fromString("e19595e8-3814-4357-bff8-d82a824597e1"), dt,
//substitute url based on requirement
////				"Learn skills to play the role of Scrum master in projectsxa0", "other", "Become a Scrum Master");
////		key = new UserGoalKey("EMAIL", "commonshared",
////				UUID.fromString("e19595e8-3814-4357-bff8-d82a824597e1"));
////	}
////
//////-------------------------------3. Tests for Custom goal creation for self-----------------
////
////}