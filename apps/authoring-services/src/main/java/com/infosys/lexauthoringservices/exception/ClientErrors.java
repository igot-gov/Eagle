package com.infosys.lexauthoringservices.exception;

import java.util.ArrayList;
import java.util.List;

public class ClientErrors {

	private List<ClientError> errors;

	public ClientErrors() {
		super();
	}

	public ClientErrors(String errorCode, Object errorMessage) {
		super();
		errors = new ArrayList<ClientError>();
		errors.add(new ClientError(errorCode, errorMessage));
	}

	public List<ClientError> getErrors() {
		return errors;
	}

	public void setErrors(List<ClientError> errors) {
		this.errors = errors;
	}

	public void addError(String errorCode, String errorMessage) {
		if (errors == null) {
			errors = new ArrayList<ClientError>();
		}
		errors.add(new ClientError(errorCode, errorMessage));
	}

	@Override
	public String toString() {
		return "ClientErrors [errors=" + errors + "]";
	}

}
