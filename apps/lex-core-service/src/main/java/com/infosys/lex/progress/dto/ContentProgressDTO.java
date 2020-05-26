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
package com.infosys.lex.progress.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ContentProgressDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "{submission.max_size.mandatory}")
	private Float max_size;

	@NotEmpty
	@NotNull(message = "{submission.current.mandatory}")
	private List<Float> current;


	@NotNull(message = "{submission.mime_type.mandatory}")
	private String mime_type;

	@NotNull(message = "{submission.content_type.mandatory}")
	private String content_type;

	private List<String> missing_ids;

	private String user_id;

	private String resource_id;
	
	private Boolean markAsComplete;
	
	private String root_org;

	public String getRoot_org() {
		return root_org;
	}

	public void setRoot_org(String root_org) {
		this.root_org = root_org;
	}

	
	@Override
	public String toString() {
		return "ContentProgressDTO [max_size=" + max_size + ", current=" + current + ", mime_type=" + mime_type
				+ ", content_type=" + content_type + ", missing_ids=" + missing_ids + ", user_id=" + user_id
				+ ", resource_id=" + resource_id + ", markAsComplete=" + markAsComplete + ", root_org=" + root_org
				+ "]";
	}

	public Boolean getMarkAsComplete() {
		return markAsComplete;
	}

	public void setMarkAsComplete(Boolean markAsComplete) {
		this.markAsComplete = markAsComplete;
	}

	public Float getMax_size() {
		return max_size;
	}

	public void setMax_size(Float max_size) {
		this.max_size = max_size;
	}

	public List<Float> getCurrent() {
		return current;
	}

	public void setCurrent(List<Float> current) {
		this.current = current;
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



}