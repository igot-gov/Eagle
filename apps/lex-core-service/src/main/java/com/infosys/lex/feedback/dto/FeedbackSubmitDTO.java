/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.feedback.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedbackSubmitDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String rootFeedbackId;

	@NotNull(message = "{feedback.user_id.mandatory}")
	@NotEmpty(message = "{feedback.user_id.not_empty}")
	@JsonProperty("user_id")
	private String userId;
	
	@JsonProperty("content_id")
	private String contentId;
	
	@NotNull(message = "{feedback.text.mandatory}")
	@NotEmpty(message = "{feedback.text.not_empty}")
	@JsonProperty("text")
	private String feedbackText;


	@JsonProperty("category")
	private String feedbackCategory;

	@NotNull(message = "{feedback.type.mandatory}")
	@NotEmpty(message = "{feedback.type.not_empty}")
	@JsonProperty("type")
	@Pattern(regexp="(content_feedback|content_request|platform_feedback|service_request)",flags=Flag.CASE_INSENSITIVE,message="{feedback.type.not_valid}")
	private String feedbackType;
	
	@JsonProperty("sentiment")
	@Pattern(regexp="(positive|negative|not_applicable)",flags=Flag.CASE_INSENSITIVE,message="{feedback.sentiment.not_valid}")
	private String feedbackSentimentCategory;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getFeedbackSentimentCategory() {
		return feedbackSentimentCategory;
	}

	public void setFeedbackSentimentCategory(String feedbackSentimentCategory) {
		this.feedbackSentimentCategory = feedbackSentimentCategory;
	}

	public String getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}

	public String getRootFeedbackId() {
		return rootFeedbackId;
	}

	public void setRootFeedbackId(String rootFeedbackId) {
		this.rootFeedbackId = rootFeedbackId;
	}

	public String getFeedbackText() {
		return feedbackText;
	}

	public void setFeedbackText(String feedbackText) {
		this.feedbackText = feedbackText;
	}

	public String getFeedbackCategory() {
		return feedbackCategory;
	}

	public void setFeedbackCategory(String feedbackCategory) {
		this.feedbackCategory = feedbackCategory;
	}

	@Override
	public String toString() {
		return "FeedbackSubmitDTO [rootFeedbackId=" + rootFeedbackId + ", userId=" + userId + ", contentId=" + contentId
				+ ", feedbackText=" + feedbackText + ", feedbackCategory=" + feedbackCategory
				+ ", feedbackType=" + feedbackType + ", feedbackSentimentCategory=" + feedbackSentimentCategory + "]";
	}

	
}
