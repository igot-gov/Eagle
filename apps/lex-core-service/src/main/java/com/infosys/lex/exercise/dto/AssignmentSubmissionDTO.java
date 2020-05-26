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

public class AssignmentSubmissionDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4768200769808365052L;

	private String resourceId;

	private boolean ignore_error = false;

	private Integer language_code;

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public boolean isIgnore_error() {
		return ignore_error;
	}

	public void setIgnore_error(boolean ignore_error) {
		this.ignore_error = ignore_error;
	}

	public String getUser_solution() {
		return user_solution;
	}

	public void setUser_solution(String user_solution) {
		this.user_solution = user_solution;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public Integer getLanguage_code() {
		return language_code;
	}

	public void setLanguage_code(Integer language_code) {
		this.language_code = language_code;
	}

	@NotNull(message = "{assignment_submit.user_solution.mandatory}")
	private String user_solution;

	private String userId;



}