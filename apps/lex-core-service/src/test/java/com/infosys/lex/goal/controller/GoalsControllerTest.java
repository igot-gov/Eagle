/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
//substitute url based on requirement

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement

//@ExtendWith(SpringExtension.class)
//@WebMvcTest(GoalsController.class)
//public class GoalsControllerTest {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@MockBean
//	private GoalsService goalsService;
//
//	@MockBean
//	private LoggerService logger;
//
//	@Test
//	public void createGoalTestValid() throws Exception {
//		GoalDTO data = new GoalDTO("e19595e8-3814-4357-bff8-d82a824597e1", "common",
//				"Learn skills to play the role of Scrum master in projectsxa0", 5, "Become a Scrum Master",
//substitute url based on requirement
//
//		RequestBuilder builder = MockMvcRequestBuilders.post("/v4/users/EMAIL/goals")
//				.accept(MediaType.APPLICATION_JSON).content(new ObjectMapper().writer().writeValueAsString(data))
//				.contentType(MediaType.APPLICATION_JSON);
//		ResultActions result = mockMvc.perform(builder);
//		result.andExpect(status().isOk());
//
//	}
//
//	@Test
//	public void createGoalTestInvalid() throws Exception {
//		GoalDTO data = new GoalDTO("e19595e8-3814-4357-bff8-d82a824597e1", "test",
//				"Learn skills to play the role of Scrum master in projectsxa0", 5, "Become a Scrum Master",
//substitute url based on requirement
//
////		doThrow(new InvalidDataInputException("invalid test")).when(goalsService).createLearningGoal(Mockito.any(),
////				Mockito.any());
//
//		RequestBuilder builder = MockMvcRequestBuilders.post("/v4/users/EMAIL/goals")
//				.accept(MediaType.APPLICATION_JSON).content(new ObjectMapper().writer().writeValueAsString(data))
//				.contentType(MediaType.APPLICATION_JSON);
//		ResultActions result = mockMvc.perform(builder);
//		result.andExpect(status().is(400));
//
//	}
//
//}