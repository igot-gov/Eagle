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

package com.infosys.lex.notification.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.notification.dto.ModesDTO;
import com.infosys.lex.notification.entity.TenantMode;
import com.infosys.lex.notification.entity.TenantModePrimaryKey;
import com.infosys.lex.notification.projection.ModesProjection;
import com.infosys.lex.notification.properties.ApplicationServerProperties;
import com.infosys.lex.notification.repository.ModesRepository;
import com.infosys.lex.notification.service.TenantModeConfigurationService;
import com.infosys.lex.notification.util.ProjectCommonUtil;

@Service
public class TenantModeConfigurationServiceImpl implements TenantModeConfigurationService {

	@Autowired
	private ModesRepository modesRepository;

	@Autowired
	ApplicationServerProperties appServerProp;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.notification.service.TenantModeConfigurationService#
	 * putTenantNotificationMode(com.infosys.lex.notification.model.ModesModel)
	 */
	@Override
	public void putTenantNotificationModes(String rootOrg, String org, List<ModesDTO> modes, String userId)
			throws Exception {

		Date date = new Date();
		Timestamp lastUpdatedDate = new Timestamp(date.getTime());
		List<TenantMode> tenantModeList = new ArrayList<TenantMode>();
		for (ModesDTO mode : modes) {
			tenantModeList.add(new TenantMode(new TenantModePrimaryKey(rootOrg, org, mode.getModeId()),
					mode.getStatus(), mode.getIcon(), userId, lastUpdatedDate));
		}
		modesRepository.saveAll(tenantModeList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.notification.service.TenantModeConfigurationService#
	 * getTenantNotificationMode(java.lang.String, java.lang.String)
	 */
	@Override
	public List<ModesDTO> getTenantNotificationModes(String rootOrg, String org, String language) {

		// get tenant data from db
		Map<String, ModesDTO> modesMap = new HashMap<>();

		List<String> languages = ProjectCommonUtil.convertLanguagePreferencesToList(language);

		String langSelected = "";

		for (ModesProjection tenantMode : modesRepository.fetchTenantModesByLanguages(rootOrg, org, languages)) {
			for (String preferedLanguage : languages) {
				if (tenantMode.getLanguage().equals(preferedLanguage)) {
					langSelected = preferedLanguage;
					ModesDTO status = new ModesDTO();
					status.setModeName(tenantMode.getModeName());
					status.setModeId(tenantMode.getModeId());
					status.setStatus(tenantMode.getModeActivated());

					modesMap.put(tenantMode.getModeId(), status);
				}
			}
		}
		// get default data from db and update tenant data
		for (ModesProjection tenantMode : modesRepository.fetchTenantModesByLanguages(
				appServerProp.getNotificationAdminRootOrg(), appServerProp.getNotificationAdminOrg(), languages)) {

			for (String preferedLanguage : languages) {
				if (preferedLanguage.equals(langSelected)) {
					// update tenant data
					if (modesMap.containsKey(tenantMode.getModeId())) {
						modesMap.get(tenantMode.getModeId()).setDisabled(!tenantMode.getModeActivated());
					}
					// getting tenant data from default values
					else {
						ModesDTO status = new ModesDTO();
						status.setDisabled(!tenantMode.getModeActivated());
						status.setModeId(tenantMode.getModeId());
						status.setModeName(tenantMode.getModeName());
						status.setIcon(tenantMode.getIconId());
						if (tenantMode.getModeActivated()) {
							status.setStatus(!tenantMode.getModeActivated());
						} else {
							status.setStatus(tenantMode.getModeActivated());
						}
						modesMap.put(tenantMode.getModeId(), status);
					}
					modesMap.get(tenantMode.getModeId()).setIcon(tenantMode.getIconId());
				}
			}
		}
		Object[] keys = modesMap.keySet().toArray();
		Arrays.sort(keys, Collections.reverseOrder());

		List<ModesDTO> output = new ArrayList<>();
		for (Object modeId : keys) {
			if (modeId.toString().equalsIgnoreCase("inApp")) {
				continue;
			} else {
				if (!modesMap.get(modeId).getDisabled()
						|| rootOrg.equalsIgnoreCase(appServerProp.getNotificationAdminRootOrg())) {
					output.add(modesMap.get(modeId));
				}
			}
		}
		return output;
	}
}