/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.rating.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

public class ContentIdsDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 487478038111282255L;
	
	@NotNull(message = "{ratingservice.contentids.mandatory}")
	private List<String> contentIds;

	public List<String> getContentIds() {
		return contentIds;
	}

	public void setContentIds(List<String> contentIds) {
		this.contentIds = contentIds;
	}

	@Override
	public String toString() {
		return "ContentRatingDto [contentIds=" + contentIds + "]";
	}

	public ContentIdsDto(@NotNull(message = "{ratingservice.contentids.mandatory}") List<String> contentIds) {
		this.contentIds = contentIds;
	}

	public ContentIdsDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	
	
}
