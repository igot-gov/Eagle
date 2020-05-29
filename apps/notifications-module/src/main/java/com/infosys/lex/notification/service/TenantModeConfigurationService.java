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

package com.infosys.lex.notification.service;

import java.util.List;

import com.infosys.lex.notification.dto.ModesDTO;

public interface TenantModeConfigurationService {

	/**
	 * add tenant mode
	 * 
	 * @param modes
	 * @throws Exception
	 */
	public void putTenantNotificationModes(String rootOrg, String org, List<ModesDTO> modes, String userUUID)
			throws Exception;

	/**
	 * get tenant mode details
	 * 
	 * @param rootOrg
	 * @param org
	 * @return
	 */
	public List<ModesDTO> getTenantNotificationModes(String rootOrg, String org, String language);

}
