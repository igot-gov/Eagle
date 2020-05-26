/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.contentsource.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;

public class ContentSourceNameListDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -587409150208234193L;
	
	@NotEmpty(message = "source names cannot be empty or null")
	List<String> contentSourceNames;

	public List<String> getContentSourceNames() {
		return contentSourceNames;
	}

	public void setContentSourceNames(List<String> contentSourceNames) {
		this.contentSourceNames = contentSourceNames;
	}

	@Override
	public String toString() {
		return "ContentSourceNameListDto [contentSourceNames=" + contentSourceNames + "]";
	}

	
	
	
	

}
