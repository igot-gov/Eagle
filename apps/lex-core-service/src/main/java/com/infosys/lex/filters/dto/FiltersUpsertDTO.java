/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.filters.dto;

import java.util.List;

public class FiltersUpsertDTO {

	private String language;
	
	private List<FieldDTO> data;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<FieldDTO> getData() {
		return data;
	}

	public void setData(List<FieldDTO> data) {
		this.data = data;
	}
	
}
