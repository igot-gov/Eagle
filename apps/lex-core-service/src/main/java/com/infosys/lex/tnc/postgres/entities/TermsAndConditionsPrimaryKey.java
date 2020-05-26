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
package com.infosys.lex.tnc.postgres.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TermsAndConditionsPrimaryKey implements Serializable {

	private static final long serialVersionUID = 4691174952405149234L;

	@Column(name = "root_org")
	private String rootOrg;

	@Column(name = "doc_name")
	private String docName;

	@Column(name = "doc_for")
	private String docFor;

	private BigDecimal version;

	private String language;

	public String getRootOrg() {
		return rootOrg;
	}

	public String getDocName() {
		return docName;
	}

	public String getDocFor() {
		return docFor;
	}

	public BigDecimal getVersion() {
		return version;
	}

	public String getLanguage() {
		return language;
	}

	public TermsAndConditionsPrimaryKey() {
		super();
	}

	public TermsAndConditionsPrimaryKey(String rootOrg, String docName, String docFor, BigDecimal version,
			String language) {
		this.rootOrg = rootOrg;
		this.docName = docName;
		this.docFor = docFor;
		this.version = version;
		this.language = language;
	}

	@Override
	public String toString() {
		return "TermsAndConditionsPrimaryKey [rootOrg=" + rootOrg + ", docName=" + docName + ", docFor=" + docFor
				+ ", version=" + version + ", language=" + language + "]";
	}
}
