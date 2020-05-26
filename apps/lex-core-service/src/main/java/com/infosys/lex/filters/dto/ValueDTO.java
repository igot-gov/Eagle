/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.filters.dto;

public class ValueDTO {
	
	private String value;
	
	private String translatedValue;

	public ValueDTO() {
		super();
	}
	
	public ValueDTO(String value, String translatedValue) {
		super();
		this.value = value;
		this.translatedValue = translatedValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTranslatedValue() {
		return translatedValue;
	}

	public void setTranslatedValue(String translatedValue) {
		this.translatedValue = translatedValue;
	}

	@Override
	public String toString() {
		return "ValueDTO [value=" + value + ", translatedValue=" + translatedValue + "]";
	}

}
