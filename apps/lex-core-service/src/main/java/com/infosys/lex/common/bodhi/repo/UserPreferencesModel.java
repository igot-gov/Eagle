/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.common.bodhi.repo;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_preferences")
public class UserPreferencesModel {

	@PrimaryKey
	UserPreferencePrimaryKey userPreferencePrimaryKey;

	@Column("date_updated")
	private Date dateUpdated;

	@Column("preference_data")
	private String preference;

	public UserPreferencesModel(UserPreferencePrimaryKey userPreferencePrimaryKey, Date dateUpdated,
			String preference) {
		this.userPreferencePrimaryKey = userPreferencePrimaryKey;
		this.dateUpdated = dateUpdated;
		this.preference = preference;
	}

	public UserPreferencePrimaryKey getUserPreferencePrimaryKey() {
		return userPreferencePrimaryKey;
	}

	public void setUserPreferencePrimaryKey(UserPreferencePrimaryKey userPreferencePrimaryKey) {
		this.userPreferencePrimaryKey = userPreferencePrimaryKey;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public String getPreference() {
		return preference;
	}

	public void setPreference(String preference) {
		this.preference = preference;
	}
}
