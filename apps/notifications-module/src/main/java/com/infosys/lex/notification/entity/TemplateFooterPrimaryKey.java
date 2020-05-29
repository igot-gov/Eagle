package com.infosys.lex.notification.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TemplateFooterPrimaryKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5008443597079391540L;

	@Column(name = "root_org", columnDefinition = "VARCHAR")
	private String rootOrg;

	@Column(name = "org", columnDefinition = "VARCHAR")
	private String org;

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public TemplateFooterPrimaryKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TemplateFooterPrimaryKey(String rootOrg, String org) {
		super();
		this.rootOrg = rootOrg;
		this.org = org;
	}

	@Override
	public String toString() {
		return "TemplateFooterPrimaryKey [rootOrg=" + rootOrg + ", org=" + org + "]";
	}
}
