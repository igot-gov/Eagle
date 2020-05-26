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
package com.infosys.lex.progress.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.infosys.lex.progress.bodhi.repo.DiscretePointsModel;
import com.infosys.lex.progress.bodhi.repo.DiscretePointsPrimaryKeyModel;
import com.infosys.lex.progress.bodhi.repo.DiscretePointsRepository;

@Service
public class DiscretePointsServiceImpl implements DiscretePointsService {

	@Autowired
	DiscretePointsRepository discretePointsRepository;

	Map<String, Integer> pointsMap = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put("resource", 1);
			put("quiz", 2);
			put("assessment_course", 10);
			put("assessment_collection", 5);
			put("exercise_with_feedback", 4);
			put("exercise_without_feedback", 3);
		}
	};

	@Async
	@Override
	public void PutPoints(String rootOrg,String userUUID, String contentId, String resourecType, String parent,
			Boolean exerciseWithFeedback) {
		int point = 0;
		String points_for = "";
		if (resourecType.toLowerCase().equals("assessment")) {
			if (parent.toLowerCase().equals("course")) {
				points_for = "assessment_course";
			} else {
				points_for = "assessment_collection";
			}
		} else if (resourecType.toLowerCase().equals("quiz")) {
			points_for = "quiz";
		} else if (resourecType.toLowerCase().equals("exercise")) {
			if (exerciseWithFeedback)
				points_for = "exercise_with_feedback";
			else
				points_for = "exercise_without_feedback";
		} else {
			points_for = "resource";
		}
		point = pointsMap.get(points_for);
		discretePointsRepository.insert(new DiscretePointsModel(new DiscretePointsPrimaryKeyModel(rootOrg,userUUID, contentId),
				point, new Date(), points_for));

	}

}