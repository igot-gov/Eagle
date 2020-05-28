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

package com.infosys.lex.notification.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ModesDTO implements Serializable {

	private static final long serialVersionUID = -6281000773241223553L;

	@NotNull(message = "modeId can't be empty")
	@JsonProperty(value = "mode_id")
	private String modeId;

	@JsonProperty(value = "mode_name")
	private String modeName;

	@NotNull(message = "status can't be empty")
	@JsonProperty(value = "status")
	private Boolean status;

	@JsonIgnore
	private Boolean disabled;
	
	
	@JsonProperty("receiver_emails")
	private List<String> receiverEmails;


	@JsonIgnore
	@JsonProperty(value = "icon")
	private String icon;

	@JsonIgnore
	private String org;

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getModeId() {
		return modeId;
	}

	public Boolean getStatus() {
		return status;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public String getIcon() {
		return icon;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getModeName() {
		return modeName;
	}

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}
	
	public List<String> getReceiverEmails() {
		return receiverEmails;
	}

	public void setReceiverEmails(List<String> receiverEmails) {
		this.receiverEmails = receiverEmails;
	}


	public ModesDTO() {
		super();
	}

	public ModesDTO(@NotNull(message = "modeId can't be empty") String modeId,
			@NotNull(message = "status can't be empty") Boolean status, Boolean disabled, String icon,List<String> receiverEmails) {
		super();
		this.modeId = modeId;
		this.status = status;
		this.disabled = disabled;
		this.icon = icon;
		this.receiverEmails = receiverEmails;
	}

	public ModesDTO(@NotNull(message = "modeId can't be empty") String modeId,
			@NotNull(message = "status can't be empty") Boolean status) {
		super();
		this.modeId = modeId;
		this.status = status;
	}

	public ModesDTO(@NotNull(message = "modeId can't be empty") String modeId,
			@NotNull(message = "status can't be empty") Boolean status, Boolean disabled) {
		super();
		this.modeId = modeId;
		this.status = status;
		this.disabled = disabled;
	}

	public ModesDTO(@NotNull(message = "modeId can't be empty") String modeId, String modeName,
			@NotNull(message = "status can't be empty") Boolean status, Boolean disabled, String icon,List<String> receiverEmails) {
		super();
		this.modeId = modeId;
		this.modeName = modeName;
		this.status = status;
		this.disabled = disabled;
		this.icon = icon;
		this.receiverEmails = receiverEmails;

	}

	public ModesDTO(@NotNull(message = "modeId can't be empty") String modeId, String modeName,
			@NotNull(message = "status can't be empty") Boolean status, Boolean disabled, String icon, String org,List<String> receiverEmails) {
		super();
		this.modeId = modeId;
		this.modeName = modeName;
		this.status = status;
		this.disabled = disabled;
		this.icon = icon;
		this.org = org;
		this.receiverEmails = receiverEmails;

	}

	@Override
	public String toString() {
		return "ModesDTO [modeId=" + modeId + ", modeName=" + modeName + ", status=" + status + ", disabled=" + disabled
				+ ", icon=" + icon + ", org=" + org + "]";
	}
}
