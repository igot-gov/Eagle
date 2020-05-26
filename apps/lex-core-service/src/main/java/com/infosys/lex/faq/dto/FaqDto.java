/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FaqDto {

	@NotNull(message = "questionSequence.notEmpty")
	@NotEmpty(message = "questionSequence.notNull")
	@JsonProperty(value = "question_seq")
	private int questionSequence;

	@NotNull(message = "question.notEmpty")
	@NotEmpty(message = "question.notNull")
	private String question;

	@NotNull(message = "answer.notEmpty")
	@NotEmpty(message = "answer.notNull")
	private String answer;

	@NotNull(message = "updatedBy.notEmpty")
	@NotEmpty(message = "updatedBy.notNull")
	@JsonProperty(value = "updated_by")
	private String updatedBy;

	public FaqDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FaqDto(int questionSequence, String question, String answer, String updatedBy) {
		super();
		this.questionSequence = questionSequence;
		this.question = question;
		this.answer = answer;
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "FaqDto [questionSequence=" + questionSequence + ", question=" + question + ", answer=" + answer
				+ ", updatedBy=" + updatedBy + "]";
	}

	public int getQuestionSequence() {
		return questionSequence;
	}

	public void setQuestionSequence(int questionSequence) {
		this.questionSequence = questionSequence;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

}
