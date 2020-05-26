/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.feedback.dto;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Feedback {

	private String rootOrg;
	private String feedbackId;
	private String feedbackText;
	private String rootFeedbackId;
	private String feedbackType;
	private String feedbackCategory;
	private String feedbackBy;

	private List<String> assignedTo;
	private String contentId;
	private String contentTitle;
	
	private String contentType;
	private String dimension;
	private String category;

	private Long createdOn;
	private Long lastActivityOn;

	private String feedbackSentimentCategory;
	private Double feedbackSentimentValue;

	private Boolean replied;
	private Boolean seenReply;
	private Integer replyCount;


	public Boolean getReplied() {
		return replied;
	}

	public void setReplied(Boolean replied) {
		this.replied = replied;
	}

	public Boolean getSeenReply() {
		return seenReply;
	}

	public void setSeenReply(Boolean seenReply) {
		this.seenReply = seenReply;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(String feedbackId) {
		this.feedbackId = feedbackId;
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

	public String getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}

	public String getFeedbackCategory() {
		return feedbackCategory;
	}

	public void setFeedbackCategory(String feedbackCategory) {
		this.feedbackCategory = feedbackCategory;
	}

	public String getFeedbackBy() {
		return feedbackBy;
	}

	public void setFeedbackBy(String feedbackBy) {
		this.feedbackBy = feedbackBy;
	}

	public List<String> getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(List<String> assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getContentTitle() {
		return contentTitle;
	}

	public void setContentTitle(String contentTitle) {
		this.contentTitle = contentTitle;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}

	public Long getLastActivityOn() {
		return lastActivityOn;
	}

	public void setLastActivityOn(Long lastActivityOn) {
		this.lastActivityOn = lastActivityOn;
	}

	public String getFeedbackSentimentCategory() {
		return feedbackSentimentCategory;
	}

	public void setFeedbackSentimentCategory(String feedbackSentimentCategory) {
		this.feedbackSentimentCategory = feedbackSentimentCategory;
	}

	public Double getFeedbackSentimentValue() {
		return feedbackSentimentValue;
	}

	public void setFeedbackSentimentValue(Double feedbackSentimentValue) {
		this.feedbackSentimentValue = feedbackSentimentValue;
	}
	
	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

	public Feedback() {
		super();
	}

	public Feedback(String rootOrg, String feedbackId, String rootFeedbackId, String feedbackText, String feedbackType,
			String feedbackCategory, String feedbackBy, List<String> contentAuthor, String contentId,
			String contentTitle, String contentType, String feedbackSentimentCategory,
			Boolean replied, Boolean seenReply) {
		super();
		this.rootOrg = rootOrg.trim();
		this.feedbackId = feedbackId;
		this.rootFeedbackId = rootFeedbackId;
		this.feedbackText = feedbackText.trim();
		this.feedbackType = feedbackType.toLowerCase().trim();
		this.feedbackCategory = feedbackCategory.trim();
		this.feedbackBy = feedbackBy;
		this.assignedTo = contentAuthor;
		this.contentId = contentId.trim();
		this.contentTitle = contentTitle.trim();
		this.contentType = contentType.trim();
		this.dimension = "";
		this.category = "";
		this.feedbackSentimentCategory = feedbackSentimentCategory.toLowerCase().trim();
		this.feedbackSentimentValue = new Double(0);
		this.createdOn =  Calendar.getInstance().getTimeInMillis();
		this.lastActivityOn = Calendar.getInstance().getTimeInMillis();
		this.replied = replied;
		this.seenReply = seenReply;
		this.replyCount = 0;
	}

	public static Feedback fromMap(Map<String, Object> json) {
		return new ObjectMapper().convertValue(json, Feedback.class);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> toMap() {
		return new ObjectMapper().convertValue(this, Map.class);
	}

	@Override
	public String toString() {
		return "Feedback [rootOrg=" + rootOrg + ", feedbackId=" + feedbackId + ", feedbackText=" + feedbackText
				+ ", rootFeedbackId=" + rootFeedbackId + ", feedbackType=" + feedbackType + ", feedbackCategory="
				+ feedbackCategory + ", feedbackBy=" + feedbackBy + ", assignedTo=" + assignedTo + ", contentId="
				+ contentId + ", contentTitle=" + contentTitle + ", contentType=" + contentType + ", dimension="
				+ dimension + ", category=" + category + ", createdOn=" + createdOn + ", lastActivityOn="
				+ lastActivityOn + ", feedbackSentimentCategory=" + feedbackSentimentCategory
				+ ", feedbackSentimentValue=" + feedbackSentimentValue + ", replied=" + replied + ", seenReply="
				+ seenReply + ", replyCount=" + replyCount + "]";
	}

}
