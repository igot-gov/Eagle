/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.postgredb.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "faq_question", schema = "wingspan")
public class FaqQuestion {

	@EmbeddedId
	private FaqQuestionPrimaryKey faqQuestionPrimaryKey;

	@Column(name = "question")
	private String question;

	@Column(name = "answer")
	private String answer;

	public FaqQuestion() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FaqQuestion(FaqQuestionPrimaryKey faqQuestionPrimaryKey, String question, String answer) {
		super();
		this.faqQuestionPrimaryKey = faqQuestionPrimaryKey;
		this.question = question;
		this.answer = answer;
	}

	public FaqQuestionPrimaryKey getFaqQuestionPrimaryKey() {
		return faqQuestionPrimaryKey;
	}

	public void setFaqQuestionPrimaryKey(FaqQuestionPrimaryKey faqQuestionPrimaryKey) {
		this.faqQuestionPrimaryKey = faqQuestionPrimaryKey;
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

	@Override
	public String toString() {
		return "FaqQuestion [faqQuestionPrimaryKey=" + faqQuestionPrimaryKey + ", question=" + question + ", answer="
				+ answer + "]";
	}

}
