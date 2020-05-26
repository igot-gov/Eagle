/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.progress.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

public class ExternalProgressDTO {
	
	@NotNull(message = "{submission.content_type.mandatory}")
	private String content_type;

	@NotNull
	private String user_id;

	
	private String content_id;

	private String root_org;
	

	private Float percent_complete;

	private String completion_date;
	
	private String first_activity_date;
	
	private String source_name;
	
	private String updated_by;

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getContent_id() {
		return content_id;
	}

	public void setContent_id(String content_id) {
		this.content_id = content_id;
	}

	public String getRoot_org() {
		return root_org;
	}

	public void setRoot_org(String root_org) {
		this.root_org = root_org;
	}

	public Float getPercent_complete() {
		return percent_complete;
	}

	public void setPercent_complete(Float percent_complete) {
		this.percent_complete = percent_complete;
	}

	public String getCompletion_date() {
		return completion_date;
	}

	public void setCompletion_date(String completion_date) {
		this.completion_date = completion_date;
	}

	public String getFirst_activity_date() {
		return first_activity_date;
	}

	public void setFirst_activity_date(String first_activity_date) {
		this.first_activity_date = first_activity_date;
	}

	public String getSource_name() {
		return source_name;
	}

	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}
	
	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	
	@Override
	public String toString() {
		return "ExternalProgressDTO [content_type=" + content_type + ", user_id=" + user_id + ", content_id="
				+ content_id + ", root_org=" + root_org + ", percent_complete=" + percent_complete
				+ ", completion_date=" + completion_date + ", first_activity_date=" + first_activity_date
				+ ", source_name=" + source_name + "]";
	}
}
