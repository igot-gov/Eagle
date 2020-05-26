/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.attendence.postgredb.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "content_attendance", schema = "wingspan")
public class ContentAttendance {

	@EmbeddedId
	private ContentAttendancePrimaryKey contentAttendancePrimaryKey;

	@Column(name = "updated_on")
	private Timestamp updatedOn;

	@Column(name = "updated_by")
	private String updatedBy;

	public ContentAttendance() {
		super();
	}

	public ContentAttendance(ContentAttendancePrimaryKey contentAttendancePrimaryKey, Timestamp updatedOn,
			String updatedBy) {
		super();
		this.contentAttendancePrimaryKey = contentAttendancePrimaryKey;
		this.updatedOn = new Timestamp(System.currentTimeMillis());
		this.updatedBy = updatedBy;
	}

	public ContentAttendancePrimaryKey getContentAttendancePrimaryKey() {
		return contentAttendancePrimaryKey;
	}

	public void setContentAttendancePrimaryKey(ContentAttendancePrimaryKey contentAttendancePrimaryKey) {
		this.contentAttendancePrimaryKey = contentAttendancePrimaryKey;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "ContentAttendance [contentAttendancePrimaryKey=" + contentAttendancePrimaryKey + ", updatedOn="
				+ updatedOn + ", updatedBy=" + updatedBy + "]";
	}

}
