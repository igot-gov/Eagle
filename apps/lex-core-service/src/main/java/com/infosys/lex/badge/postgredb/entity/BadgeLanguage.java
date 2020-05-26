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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "badge_language", schema = "wingspan")
public class BadgeLanguage {

	@EmbeddedId
	BadgeLanguageKey key;

	@Column(name = "badge_name")
	private String badgeName;

	@Column(name = "badge_description")
	private String badgeDescription;

	@Column(name = "completed_description")
	private String completedDescription;

	private String message;

	@Column(name = "badge_group_text")
	private String badgeGroupText;

	public BadgeLanguageKey getkey() {
		return key;
	}

	public String getBadgeName() {
		return badgeName;
	}

	public String getBadgeDescription() {
		return badgeDescription;
	}

	public String getCompletedDescription() {
		return completedDescription;
	}

	public String getMessage() {
		return message;
	}

	public String getBadgeGroupText() {
		return badgeGroupText;
	}

	public BadgeLanguage() {

	}

	public BadgeLanguage(BadgeLanguageKey key, String badgeName, String badgeDescription, String completedDescription,
			String message, String badgeGroupText) {
		this.key = key;
		this.badgeName = badgeName;
		this.badgeDescription = badgeDescription;
		this.completedDescription = completedDescription;
		this.message = message;
		this.badgeGroupText = badgeGroupText;
	}

	@Override
	public String toString() {
		return "BadgeLanguage [badgeLanguagekey=" + key + ", badgeName=" + badgeName + ", badgeDescription="
				+ badgeDescription + ", completedDescription=" + completedDescription + ", message=" + message
				+ ", badgeGroupText=" + badgeGroupText + "]";
	}

}
