/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DBErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private Exception dbException;

	

	public String getMessage() {
		return message;
	}

	

	public DBErrorException(String message, Exception dbException) {
		super(message,dbException);
		this.message = message;
		this.dbException = dbException;
	}



	public Exception getDbException() {
		return dbException;
	}



	public void setDbException(Exception dbException) {
		this.dbException = dbException;
	}



	public void setMessage(String message) {
		this.message = message;
	}

}
