/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.language.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "language_master", schema = "wingspan")
public class LanguageMaster implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LanguageMaster() {
		super();
	}

	@EmbeddedId
	private LanguageMasterKey languageMasterKey;

	@Column(name = "locales")
	private String locale;

	@Column(name = "flag_url")
	private String flagUrl;

	@Column(name = "country_user_friendly")
	private String countryUserFriendly;

	@Column(name = "language_user_friendly")
	private String languageUserFriendly;

	public LanguageMaster(LanguageMasterKey languageTenantKey, String locale, String flagUrl,
			String countryUserFriendly, String languageUserFriendly) {
		super();
		this.languageMasterKey = languageTenantKey;
		this.locale = locale;
		this.flagUrl = flagUrl;
		this.countryUserFriendly = countryUserFriendly;
		this.languageUserFriendly = languageUserFriendly;
	}

	public LanguageMasterKey getLanguageTenantKey() {
		return languageMasterKey;
	}

	public void setLanguageTenantKey(LanguageMasterKey languageTenantKey) {
		this.languageMasterKey = languageTenantKey;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getFlagUrl() {
		return flagUrl;
	}

	public void setFlagUrl(String flagUrl) {
		this.flagUrl = flagUrl;
	}

	public String getCountryUserFriendly() {
		return countryUserFriendly;
	}

	public void setCountryUserFriendly(String countryUserFriendly) {
		this.countryUserFriendly = countryUserFriendly;
	}

	public String getLanguageUserFriendly() {
		return languageUserFriendly;
	}

	public void setLanguageUserFriendly(String languageUserFriendly) {
		this.languageUserFriendly = languageUserFriendly;
	}

	@Override
	public String toString() {
		return "LanguageMaster [languageMasterKey=" + languageMasterKey + ", locale=" + locale + ", flagUrl=" + flagUrl
				+ ", countryUserFriendly=" + countryUserFriendly + ", languageUserFriendly=" + languageUserFriendly
				+ "]";
	}

}
