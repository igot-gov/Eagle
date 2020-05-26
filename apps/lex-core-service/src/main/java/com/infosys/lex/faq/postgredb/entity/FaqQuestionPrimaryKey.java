/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.postgredb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FaqQuestionPrimaryKey implements Serializable {

	private static final long serialVersionUID = 5387656278562068494L;

	@Column(name = "root_org")
	private String rootOrg;

	@Column(name = "language")
	private String language;

	@Column(name = "group_id")
	private String groupId;

	@Column(name = "question_seq")
	private Integer questionSeq;

	public FaqQuestionPrimaryKey(String rootOrg, String language, String groupId, Integer questionSeq) {
		super();
		this.rootOrg = rootOrg;
		this.language = language;
		this.groupId = groupId;
		this.questionSeq = questionSeq;
	}

	public FaqQuestionPrimaryKey(String rootOrg, String language, String groupId) {
		super();
		this.rootOrg = rootOrg;
		this.language = language;
		this.groupId = groupId;
	}

	public FaqQuestionPrimaryKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getQuestionSequence() {
		return questionSeq;
	}

	public void setQuestionSequence(Integer questionSeq) {
		this.questionSeq = questionSeq;
	}

	@Override
	public String toString() {
		return "FaqQuestionPrimaryKey [rootOrg=" + rootOrg + ", language=" + language + ", groupId=" + groupId
				+ ", questionSequence=" + questionSeq + "]";
	}

}
