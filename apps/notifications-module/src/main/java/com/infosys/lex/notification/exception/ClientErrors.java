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

package com.infosys.lex.notification.exception;

import java.util.ArrayList;
import java.util.List;

public class ClientErrors {

	private List<ClientError> error;

	/**
	 * @return
	 */
	public List<ClientError> getError() {
		return error;
	}

	/**
	 * @param error
	 */
	public void setError(List<ClientError> error) {
		this.error = error;
	}

	/**
	 * @param code
	 * @param message
	 */
	public ClientErrors(String code, String message) {
		super();
		error=new ArrayList<>();
		error.add(new ClientError(code,message));
	}

	/**
	 * 
	 */
	public ClientErrors() {
		super();
	}
	
	/**
	 * @param code
	 * @param message
	 */
	public void addError(String code, String message) {
		if(error==null) {
			error=new ArrayList<ClientError>();
		}
		error.add(new ClientError(code, message));
	}
	
}
