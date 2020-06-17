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
package com.infosys.lexauthoringservices.exception;

public class ClientError {

	private String code;

	private Object message;

	/**
	 * Constructor
	 */
	public ClientError() {
		super();
	}

	/**
	 * Constructor with parameters
	 * 
	 * @param code
	 * @param message
	 */
	public ClientError(String code, Object message) {
		super();
		this.code = code;
		this.message = message;
	}

	/**
	 * set code parameter
	 * 
	 * @param value
	 */
	public void setCode(String value) {
		code = value;

	}

	/**
	 * set message parameter
	 * 
	 * @param msg
	 */
	public void setMessage(Object msg) {
		message = msg;

	}

	/**
	 * get message parameter
	 * 
	 * @return
	 */
	public Object getMessage() {
		return message;
	}

	/**
	 * get code parameter
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "ClientErrorInfo [code=" + code + ", message=" + message + "]";
	}

}
