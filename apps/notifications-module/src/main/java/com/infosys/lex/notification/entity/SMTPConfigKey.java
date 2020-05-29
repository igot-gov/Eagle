package com.infosys.lex.notification.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SMTPConfigKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6293896224456746135L;

	@Column(name = "root_org")
	private String rootOrg;
	
	@Column(name = "org")
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

	public SMTPConfigKey(String rootOrg, String org) {
		this.rootOrg = rootOrg;
		this.org = org;
	}

	public SMTPConfigKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "SMTPConfigKey [rootOrg=" + rootOrg + ", org=" + org + "]";
	}
	
	
}
