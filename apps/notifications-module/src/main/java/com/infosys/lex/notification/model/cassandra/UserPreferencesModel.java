package com.infosys.lex.notification.model.cassandra;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_preferences")
public class UserPreferencesModel {

	@PrimaryKey
	UserPreferencesPrimaryKey userPreferencePrimaryKey;

	@Column("date_updated")
	private Date dateUpdated;

	@Column("preference_data")
	private String preference;

	public UserPreferencesModel(UserPreferencesPrimaryKey userPreferencePrimaryKey, Date dateUpdated,
			String preference) {
		this.userPreferencePrimaryKey = userPreferencePrimaryKey;
		this.dateUpdated = dateUpdated;
		this.preference = preference;
	}

	public UserPreferencesPrimaryKey getUserPreferencePrimaryKey() {
		return userPreferencePrimaryKey;
	}

	public void setUserPreferencePrimaryKey(UserPreferencesPrimaryKey userPreferencePrimaryKey) {
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