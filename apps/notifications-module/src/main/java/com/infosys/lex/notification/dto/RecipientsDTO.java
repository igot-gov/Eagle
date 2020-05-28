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

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecipientsDTO implements Serializable{
	private static final long serialVersionUID = 4040708780284923924L;

	@NotNull(message="recipient name can't be empty")
	@JsonProperty(value="recipient")
	private String recipient;
	
	@NotNull(message="modes can' be left blank")
	@JsonProperty(value="modes")
	private List<ModesDTO> modes;

	/**
	 * @return
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * @return
	 */
	public List<ModesDTO> getModes() {
		return modes;
	}

	/**
	 * @param modes
	 */
	public void setModes(List<ModesDTO> modes) {
		this.modes = modes;
	}

	/**
	 * @param recipient
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * @param recipient
	 * @param modes
	 */
	public RecipientsDTO(String recipient, List<ModesDTO> modes) {
		super();
		this.recipient = recipient;
		this.modes = modes;
	}

	/**
	 * 
	 */
	public RecipientsDTO() {
		super();
	}

	@Override
	public String toString() {
		return "RecipientsDTO [recipient=" + recipient + ", modes=" + modes + "]";
	}

}