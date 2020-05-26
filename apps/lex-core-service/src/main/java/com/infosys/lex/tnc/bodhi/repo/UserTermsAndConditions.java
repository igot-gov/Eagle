/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.tnc.bodhi.repo;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_terms_condition")
public class UserTermsAndConditions {

	@PrimaryKey
	private UserTermsAndConditionsPrimaryKey key;

	@Column("accepted_on")
	private Date acceptedOn;

	@Column
	private String language;

	public UserTermsAndConditionsPrimaryKey getKey() {
		return key;
	}

	public void setKey(UserTermsAndConditionsPrimaryKey key) {
		this.key = key;
	}

	public Date getAcceptedOn() {
		return acceptedOn;
	}

	public void setAcceptedOn(Date acceptedOn) {
		this.acceptedOn = acceptedOn;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public UserTermsAndConditions() {
		super();
	}

	public UserTermsAndConditions(UserTermsAndConditionsPrimaryKey key, Date acceptedOn, String language) {
		this.key = key;
		this.acceptedOn = acceptedOn;
		this.language = language;
	}

	@Override
	public String toString() {
		return "TermsAndConditions [key=" + key + ", acceptedOn=" + acceptedOn + ", language=" + language + "]";
	}
}
