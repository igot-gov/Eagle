/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.core.exception;

import java.util.ArrayList;
import java.util.List;

public class ClientErrors {

	private List<ClientError> errors;

	public ClientErrors() {
		super();
	}

	public ClientErrors(String errorCode, String errorMessage) {
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
