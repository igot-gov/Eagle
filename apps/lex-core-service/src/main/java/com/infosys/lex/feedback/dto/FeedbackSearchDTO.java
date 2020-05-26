/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.feedback.dto;

import java.io.Serializable;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedbackSearchDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "{feedback.query.mandatory}")
	private String query;

	@NotNull(message = "{feedback.filters.mandatory}")
	private Map<String, Object> filters;

	@NotNull(message = "{feedback.viewed_by.mandatory}")
	@NotEmpty(message = "{feedback.viewed_by.not_empty}")
	@JsonProperty("viewed_by")
	private String viewedBy;

	@NotNull(message = "{feedback.user_Id.mandatory}")
	@NotEmpty(message = "{feedback.user_id.not_empty}")
	@JsonProperty("user_id")
	private String userId;
	
	@NotNull(message = "{feedback.replyFilter.mandatory}")
	@JsonProperty("all")
	private Boolean replyFilter;
	
	@NotNull(message = "{feedback.from.mandatory}")
	@JsonProperty("from")
	private Integer from;

	@NotNull(message = "{feedback.size.mandatory}")
	@JsonProperty("size")
	private Integer offset;
	

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Boolean getReplyFilter() {
		return replyFilter;
	}

	public void setReplyFilter(Boolean replyFilter) {
		this.replyFilter = replyFilter;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Map<String, Object> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}

	public String getViewedBy() {
		return viewedBy;
	}

	public void setViewedBy(String viewedBy) {
		this.viewedBy = viewedBy;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "FeedbackSearchDTO [query=" + query + ", filters=" + filters + ", viewedBy=" + viewedBy + ", userId="
				+ userId + ", replyFilter=" + replyFilter + "]";
	}

	

}