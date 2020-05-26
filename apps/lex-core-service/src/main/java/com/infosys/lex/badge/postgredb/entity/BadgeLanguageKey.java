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
package com.infosys.lex.badge.postgredb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BadgeLanguageKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "root_org")
	private String rootOrg;

	@Column(name = "badge_id")
	private String badgeId;

	@Column(name = "language")
	private String language;

	public String getRootOrg() {
		return rootOrg;
	}

	public String getBadgeId() {
		return badgeId;
	}

	public String getLanguage() {
		return language;
	}

	public BadgeLanguageKey() {
	}

	public BadgeLanguageKey(String rootOrg, String badgeId, String language) {
		this.rootOrg = rootOrg;
		this.badgeId = badgeId;
		this.language = language;
	}

	@Override
	public String toString() {
		return "BadgeLanguageKey [rootOrg=" + rootOrg + ", badgeId=" + badgeId + ", language=" + language + "]";
	}

}
