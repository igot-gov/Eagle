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

public class NotificationSendDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<NotificationDigestDTO> data;

	private String page;

	/**
	 * @return
	 */
	public List<NotificationDigestDTO> getData() {
		return data;
	}

	/**
	 * @param data
	 */
	public void setData(List<NotificationDigestDTO> data) {
		this.data = data;
	}

	/**
	 * @return
	 */
	public String getPage() {
		return page;
	}

	/**
	 * @param page
	 */
	public void setPage(String page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return "NotificationSendDTO [data=" + data + ", page=" + page + "]";
	}

	/**
	 * @param data
	 * @param page
	 */
	public NotificationSendDTO(List<NotificationDigestDTO> data, String page) {
		super();
		this.data = data;
		this.page = page;
	}

	/**
	 * 
	 */
	public NotificationSendDTO() {
		super();
	}

}
