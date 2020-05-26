/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.progress.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;



public class AssessmentRecalculateDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String mime_type;

	private String content_type;

	private List<String> missing_ids;

	private String user_id;

	private String resource_id;
	
	private String root_org;
	
	private Float progress;

	private String firstCompletedOn;

	private String updatedBy;

	private Set<Float> visitedSet;
	
	@Override
	public String toString() {
		return "AssessmentRecalculaeDTO [mime_type=" + mime_type + ", content_type=" + content_type + ", missing_ids="
				+ missing_ids + ",t user_id=" + user_id + ", resource_id=" + resource_id + ", root_org=" + root_org
				+ ", progress=" + progress + ", firstCompletedOn=" + firstCompletedOn + ", updatedBy=" + updatedBy
				+ ", visitedSet=" + visitedSet + "]";
	}

	public String getMime_type() {
		return mime_type;
	}

	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public List<String> getMissing_ids() {
		return missing_ids;
	}

	public void setMissing_ids(List<String> missing_ids) {
		this.missing_ids = missing_ids;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getResource_id() {
		return resource_id;
	}

	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}

	public String getRoot_org() {
		return root_org;
	}

	public void setRoot_org(String root_org) {
		this.root_org = root_org;
	}

	public Float getProgress() {
		return progress;
	}

	public void setProgress(Float progress) {
		this.progress = progress;
	}

	public String getFirstCompletedOn() {
		return firstCompletedOn;
	}

	public void setFirstCompletedOn(String firstCompletedOn) {
		this.firstCompletedOn = firstCompletedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Set<Float> getVisitedSet() {
		return visitedSet;
	}

	public void setVisitedSet(Set<Float> visitedSet) {
		this.visitedSet = visitedSet;
	}


}
