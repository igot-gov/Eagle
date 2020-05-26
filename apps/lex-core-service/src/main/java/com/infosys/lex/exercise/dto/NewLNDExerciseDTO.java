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
package com.infosys.lex.exercise.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class NewLNDExerciseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Float result_percent;

	private Integer total_testcases;

	private Integer testcases_passed;

	private Integer testcases_failed;

	@NotNull(message = "{exercise.submission_type.mandatory}")
	private String submission_type;

	@NotNull(message = "{exercise.url.mandatory}")
	private String url;


	public Float getResult_percent() {
		return result_percent;
	}

	public void setResult_percent(Float result_percent) {
		this.result_percent = result_percent;
	}

	public Integer getTotal_testcases() {
		return total_testcases;
	}

	public void setTotal_testcases(Integer total_testcases) {
		this.total_testcases = total_testcases;
	}

	public Integer getTestcases_passed() {
		return testcases_passed;
	}

	public void setTestcases_passed(Integer testcases_passed) {
		this.testcases_passed = testcases_passed;
	}

	public Integer getTestcases_failed() {
		return testcases_failed;
	}

	public void setTestcases_failed(Integer testcases_failed) {
		this.testcases_failed = testcases_failed;
	}

	public String getSubmission_type() {
		return submission_type;
	}

	public void setSubmission_type(String submission_type) {
		this.submission_type = submission_type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	

	@Override
	public String toString() {
		return "NewLNDExerciseDTO [result_percent=" + result_percent + ", total_testcases=" + total_testcases
				+ ", testcases_passed=" + testcases_passed + ", testcases_failed=" + testcases_failed
				+ ", submission_type=" + submission_type + ", url=" + url +  "]";
	}

}
