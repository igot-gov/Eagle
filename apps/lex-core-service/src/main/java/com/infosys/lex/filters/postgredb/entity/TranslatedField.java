/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.filters.postgredb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "translated_field", schema = "wingspan")
public class TranslatedField {

	  @Id
	  private String id;
	  
	  @Column(name = "root_org")
	  private String rootOrg ;
	  
	  @Column(name = "org")
	  private String org ;
	  
	  @Column(name = "language")
	  private String language ;
	  
	  @Column(name = "field")
	  private String field ;
	  
	  @Column(name = "translated_field")
	  private String translatedField ;

	  public TranslatedField() {
		  super();
	  }
				  
	@Override
	public String toString() {
		return "TranslatedField [id=" + id + ", rootOrg=" + rootOrg + ", org=" + org + ", language=" + language
				+ ", field=" + field + ", translatedField=" + translatedField + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getTranslatedField() {
		return translatedField;
	}

	public void setTranslatedField(String translatedField) {
		this.translatedField = translatedField;
	}

	public TranslatedField(String id, String rootOrg, String org, String language, String field,
			String translatedField) {
		super();
		this.id = id;
		this.rootOrg = rootOrg;
		this.org = org;
		this.language = language;
		this.field = field;
		this.translatedField = translatedField;
	}
}
