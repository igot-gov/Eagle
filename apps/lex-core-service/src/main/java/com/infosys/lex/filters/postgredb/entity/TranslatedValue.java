/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.filters.postgredb.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "translated_value", schema = "wingspan")
public class TranslatedValue {
	
	@EmbeddedId
	private TranslatedValuePrimaryKey translatedValuePrimaryKey;
	
	@Column(name = "translated_value")
	private String translatedValue;

	public TranslatedValue() {
		super();
	}
	
	public TranslatedValue(TranslatedValuePrimaryKey translatedValuePrimaryKey, String translatedValue) {
		super();
		this.translatedValuePrimaryKey = translatedValuePrimaryKey;
		this.translatedValue = translatedValue;
	}

	public TranslatedValuePrimaryKey getTranslatedValuePrimaryKey() {
		return translatedValuePrimaryKey;
	}

	public void setTranslatedValuePrimaryKey(TranslatedValuePrimaryKey translatedValuePrimaryKey) {
		this.translatedValuePrimaryKey = translatedValuePrimaryKey;
	}

	public String getTranslatedValue() {
		return translatedValue;
	}

	public void setTranslatedValue(String translatedValue) {
		this.translatedValue = translatedValue;
	}

	@Override
	public String toString() {
		return "TranslatedValue [translatedValuePrimaryKey=" + translatedValuePrimaryKey + ", translatedValue="
				+ translatedValue + "]";
	}

}
