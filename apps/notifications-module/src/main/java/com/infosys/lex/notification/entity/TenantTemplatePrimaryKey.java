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

package com.infosys.lex.notification.entity;

import java.io.Serializable;

import javax.persistence.Column;

public class TenantTemplatePrimaryKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4860182524036918664L;

	@Column(name="template_id")
	private String templateId;
	
	@Column(name="language")
	private String language;

	public String getTemplateId() {
		return templateId;
	}

	public String getLanguage() {
		return language;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public TenantTemplatePrimaryKey() {
		super();
	}

	public TenantTemplatePrimaryKey(String templateId, String language) {
		super();
		this.templateId = templateId;
		this.language = language;
	}

	@Override
	public String toString() {
		return "TenantTemplatePrimaryKey [templateId=" + templateId + ", language=" + language + "]";
	}
}
