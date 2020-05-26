/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.rating.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class UserContentRatingDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 487478038111282255L;
	
	@NotNull(message = "{ratingservice.rating.mandatory}")
	private Float rating;

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}

	
	
	
}
