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
package com.infosys.lex.assessment.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

public class AssessmentSubmissionDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "{submission.timeLimit.mandatory}")
	private Long timeLimit;

	@NotNull(message = "{submission.isAssessment.mandatory}")
	private Boolean isAssessment;

	@NotNull(message = "{submission.questions.mandatory}")
	private List<Map<String, Object>> questions;

	@NotNull(message = "{submission.identifier.mandatory}")
	private String identifier;

	@NotNull(message = "{submission.title.mandatory}")
	private String title;

	public Long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Long timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Boolean isAssessment() {
		return isAssessment;
	}

	public void setIsAssessment(Boolean isAssessment) {
		this.isAssessment = isAssessment;
	}

	public List<Map<String, Object>> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Map<String, Object>> questions) {
		this.questions = questions;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "AssessmentSubmissionDTO [timeLimit=" + timeLimit + ", isAssessment=" + isAssessment + ", questions="
				+ questions + ", identifier=" + identifier + ", title=" + title + "]";
	}

}