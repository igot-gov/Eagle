package com.infosys.lex.notification.controller;

import java.util.List;
import java.util.Map;

import javax.security.sasl.AuthenticationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.notification.dto.EventsDTO;
import com.infosys.lex.notification.service.TenantEventConfigurationService;

@RestController
@CrossOrigin(origins = "*")
public class TenantEventConfigurationController {

	@Autowired
	TenantEventConfigurationService tenantConfigService;

	/**
	 * This API will be called when a tenant admin reaches the 2nd step to fetch all
	 * events that can be configured based on default config of modes and events and
	 * tenant's own config of modes.
	 * 
	 * @param rootOrg
	 * @param org
	 * @param language
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/events")
	public ResponseEntity<?> getTenantConfigurableEvents(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("org") String org, @RequestHeader(required = false, name = "langCode") String language)
			throws Exception {

		return new ResponseEntity<>(tenantConfigService.getTenantConfigurableEvents(rootOrg, org, language),
				HttpStatus.OK);
	}

	/**
	 * This API will be called when a tenant admin configures an event in the 2nd
	 * step (that is, configure events) of notification configuration.
	 * 
	 * @param events
	 * @param rootOrg
	 * @param org
	 * @param userUUID
	 * @return
	 * @throws AuthenticationException
	 */
	@PutMapping("/v1/events")
	public ResponseEntity<?> configureTenantEvent(@Valid @RequestBody List<EventsDTO> events,
			@RequestHeader("rootOrg") String rootOrg, @RequestHeader("org") String org,
			@RequestParam(required = true, name = "userId") String userId) throws AuthenticationException {

		tenantConfigService.configureTenantNotificationEvents(rootOrg, org, events, userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}


	/**
	 *  This API will be called when a tenant
	 * admin reaches on the 3rd step (that is, where configured events are shown) of
	 * notification configuration.
	 * 
	 * @param events
	 * @param rootOrg
	 * @param org
	 * @return
	 * @throws AuthenticationException
	 */
	@GetMapping("/v1/events/configured")
	public ResponseEntity<?> getAllConfiguredTenantEvents(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("org") String org, @RequestHeader(required = false, name = "langCode") String language)
			throws Exception {

		return new ResponseEntity<List<Map<String, Object>>>(
				tenantConfigService.getTenantConfiguredEvents(rootOrg, org, language), HttpStatus.OK);
	}
}