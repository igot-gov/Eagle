package com.infosys.lex.notification.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecipientDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3151337997511614236L;

	@JsonProperty(value = "recipient")
	private String recipient;

	@JsonProperty(value = "modes")
	private List<ModesDTO> modes;

	@JsonProperty(value = "classified_as")
	private String classification;
}