/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.contentsource.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public class ContentSourceUserDetailDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6880004821874361175L;
	
	@NotBlank(message = "userId is null or empty")
	private String userId;
	
	@NotBlank(message = "startDate is null or empty")
	private String startDate;

	@NotBlank(message = "endDate is null or empty ")
	private String endDate;
	
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

}
