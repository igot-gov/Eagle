/**
Â© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (â€œProgramâ€�),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential

*/

package com.infosys.lex.notification.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.notification.dto.ModesDTO;
import com.infosys.lex.notification.service.TenantModeConfigurationService;;

@RestController
@RequestMapping("/v1")
public class TenantModeConfigurationController {

	@Autowired
	TenantModeConfigurationService modesConfigService;



	@PutMapping("/modes")
	public ResponseEntity<?> putTenantNotificationMode(@Valid @RequestBody List<ModesDTO> modes,
			@RequestHeader("rootOrg") String rootOrg, @RequestHeader("org") String org,
			@RequestParam("userId") String userId) throws Exception {

		modesConfigService.putTenantNotificationModes(rootOrg, org, modes, userId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/modes")
	public ResponseEntity<List<ModesDTO>> getTenantNotificationMode(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("org") String org, @RequestHeader("langCode") String language) throws Exception {

		return new ResponseEntity<List<ModesDTO>>(modesConfigService.getTenantNotificationModes(rootOrg, org, language),
				HttpStatus.OK);
	}

}
