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
package com.infosys.lex.contentsource.postgres.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "content_source", schema = "wingspan")
public class ContentSource {

	@EmbeddedId
	private ContentSourcePrimaryKey key;

	@Column(name = "registration_url")
	private String registrationUrl;

	@Column(name = "updated_on")
	private Timestamp createdOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "registration_provided")
	private Boolean registrationProvided;

	@Column(name = "progress_provided")
	private boolean progressProvided;

	@Column(name = "registration_enabled")
	private Boolean registrationEnabled;

	@Column(name = "license_expires_on")
	private Timestamp licenseExpiresOn;

	@Column(name = "source_name")
	private String sourceName;

	@Column(name = "source_url")
	private String sourceUrl;

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public void setKey(ContentSourcePrimaryKey key) {
		this.key = key;
	}

	public Boolean getRegistrationProvided() {
		return registrationProvided;
	}

	public void setRegistrationProvided(Boolean registrationProvided) {
		this.registrationProvided = registrationProvided;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getRegistrationUrl() {
		return registrationUrl;
	}

	public void setRegistrationUrl(String registrationUrl) {
		this.registrationUrl = registrationUrl;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public boolean isProgressProvided() {
		return progressProvided;
	}

	public void setProgressProvided(boolean progressProvided) {
		this.progressProvided = progressProvided;
	}

	public Timestamp getLicenseExpiresOn() {
		return licenseExpiresOn;
	}

	public void setLicenseExpiresOn(Timestamp licenseExpiresOn) {
		this.licenseExpiresOn = licenseExpiresOn;
	}

	public Boolean getRegistrationEnabled() {
		return registrationEnabled;
	}

	public void setRegistrationEnabled(Boolean registrationEnabled) {
		this.registrationEnabled = registrationEnabled;
	}

	@Override
	public String toString() {
		return "ContentSource [key=" + key + ", registrationUrl=" + registrationUrl + ", createdOn=" + createdOn
				+ ", updatedBy=" + updatedBy + ", progressProvided=" + progressProvided + ", licenseExpiresOn="
				+ licenseExpiresOn + "]";
	}

	public ContentSourcePrimaryKey getKey() {
		return key;
	}

}
