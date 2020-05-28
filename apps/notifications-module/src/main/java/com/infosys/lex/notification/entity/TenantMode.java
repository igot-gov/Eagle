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

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_tenant_mode", schema = "wingspan")
public class TenantMode {

	@EmbeddedId
	private TenantModePrimaryKey tenantModeKey;

	@Column(name = "activated")
	private Boolean activated;

	@Column(name = "icon_id")
	private String iconId;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_on")
	private Timestamp lastUpdatedOn;

	@Override
	public String toString() {
		return "TenantMode [tenantModeKey=" + tenantModeKey + ", activated=" + activated + ", iconId=" + iconId
				+ ", updatedBy=" + updatedBy + ", lastUpdatedOn=" + lastUpdatedOn + "]";
	}

	public TenantMode(TenantModePrimaryKey tenantModeKey, Boolean activated, String iconId, String updatedBy,
			Timestamp lastUpdatedOn) {
		super();
		this.tenantModeKey = tenantModeKey;
		this.activated = activated;
		this.iconId = iconId;
		this.updatedBy = updatedBy;
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public TenantModePrimaryKey getTenantModeKey() {
		return tenantModeKey;
	}

	public void setTenantModeKey(TenantModePrimaryKey tenantModeKey) {
		this.tenantModeKey = tenantModeKey;
	}

	public Boolean isActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	public TenantMode(TenantModePrimaryKey tenantModeKey, Boolean activated, String iconId) {
		super();
		this.tenantModeKey = tenantModeKey;
		this.activated = activated;
		this.iconId = iconId;
	}

	public TenantMode() {
		super();
	}
}
