/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.exercise.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class NewExerciseFeedbackDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "{feedback.educator_id.mandatory}")
	private String educator_id;

	@NotNull(message = "{feedback.rating.mandatory}")
	private Integer rating;

	@NotNull(message = "{feedback.max_rating.mandatory}")
	private Integer max_rating;

	@NotNull(message = "{feedback.feedback_type.mandatory}")
	private String feedback_type;

	@NotNull(message = "{feedback.url.mandatory}")
	private String url;

	

	public String getEducator_id() {
		return educator_id;
	}

	public void setEducator_id(String educator_id) {
		this.educator_id = educator_id;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Integer getMax_rating() {
		return max_rating;
	}

	public void setMax_rating(Integer max_rating) {
		this.max_rating = max_rating;
	}

	public String getFeedback_type() {
		return feedback_type;
	}

	public void setFeedback_type(String feedback_type) {
		this.feedback_type = feedback_type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	

	@Override
	public String toString() {
		return "NewExerciseFeedbackDTO [educator_id=" + educator_id + ", rating=" + rating + ", max_rating="
				+ max_rating + ", feedback_type=" + feedback_type + ", url=" + url
				+ "]";
	}

}
