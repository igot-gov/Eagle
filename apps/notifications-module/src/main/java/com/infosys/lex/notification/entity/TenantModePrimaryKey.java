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
import javax.persistence.Embeddable;
	
@Embeddable
public class TenantModePrimaryKey implements Serializable {

		private static final long serialVersionUID = 1L;

		@Column(name = "root_org", columnDefinition = "VARCHAR")
		private String rootOrg;

		@Column(name = "org", columnDefinition = "VARCHAR")
		private String org;

		@Column(name = "mode", columnDefinition = "VARCHAR")
		private String mode;

		public String getRootOrg() {
			return rootOrg;
		}

		public void setRootOrg(String root_org) {
			this.rootOrg = root_org;
		}

		public String getOrg() {
			return org;
		}

		public void setOrg(String org) {
			this.org = org;
		}

		public String getMode() {
			return mode;
		}

		public void setMode(String mode) {
			this.mode = mode;
		}

		public TenantModePrimaryKey(String rootOrg, String org, String mode) {
			super();
			this.rootOrg = rootOrg;
			this.org = org;
			this.mode = mode;
		}

		public TenantModePrimaryKey() {
			super();
		}

}
