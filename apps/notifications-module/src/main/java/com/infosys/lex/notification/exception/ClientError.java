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

public class ClientError {

	private String code;
	
	private String message;

	
	/**
	 * get code parameter
	 * @return
	 */
	/**
	 * @return
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * set code parameter
	 * @param value
	 */
	/**
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * get message parameter
	 * @return
	 */
	/**
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * set message parameter
	 * @param msg
	 */
	/**
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * Constructor with parameters
	 * @param code
	 * @param message
	 */
	/**
	 * @param code
	 * @param message
	 */
	public ClientError(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	/**
	 * Constructor
	 */
	/**
	 * 
	 */
	public ClientError() {
		super();
	}

	@Override
	public String toString() {
		return "ClientError [code=" + code + ", message=" + message + "]";
	}
	
	
}
