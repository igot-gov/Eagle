/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.filters.dto;

import java.util.List;

public class FieldDTO {

	private String field;
	
	private String translatedField;
	
	private List<ValueDTO> values;

	public FieldDTO() {
		super();
	}
	
	public FieldDTO(String field, String translatedField, List<ValueDTO> values) {
		super();
		this.field = field;
		this.translatedField = translatedField;
		this.values = values;
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

	public List<ValueDTO> getValues() {
		return values;
	}

	public void setValues(List<ValueDTO> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "FieldDTO [field=" + field + ", translatedField=" + translatedField + ", values=" + values + "]";
	}

}
