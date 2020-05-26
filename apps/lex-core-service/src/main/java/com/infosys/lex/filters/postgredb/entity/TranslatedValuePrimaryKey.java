/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.filters.postgredb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TranslatedValuePrimaryKey implements Serializable {

	private static final long serialVersionUID = -6227399206391169619L;
	
	@Column(name = "value")
	private String value;
	
	@Column(name = "field_meta")
	private String fieldFK;
	
	public TranslatedValuePrimaryKey() {
		super();
	}

	public TranslatedValuePrimaryKey(String value, String fieldFK) {
		super();
		this.value = value;
		this.fieldFK = fieldFK;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFieldFK() {
		return fieldFK;
	}

	public void setFieldFK(String fieldFK) {
		this.fieldFK = fieldFK;
	}

	@Override
	public String toString() {
		return "TranslatedValuePrimaryKey [value=" + value + ", fieldFK=" + fieldFK + "]";
	}

}
