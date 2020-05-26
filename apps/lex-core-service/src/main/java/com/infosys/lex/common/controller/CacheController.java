/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.core.config.CachingServiceManager;

@RestController
@CrossOrigin(origins = "*")
public class CacheController {
	@Autowired
	CachingServiceManager cacheManager;

	@PutMapping("/v1/evict-cache/{cacheName}")
	public ResponseEntity<String> evictCache(@PathVariable(name="cacheName") String cacheName, 
			@RequestParam(defaultValue = "all", name="cacheKey", required = false) String cacheKey){
		HttpStatus status = HttpStatus.ACCEPTED;
		if(!"all".equals(cacheKey)) {
			cacheManager.evictSingleCacheValue(cacheName, cacheKey);
		}
		else {
			cacheManager.evictAllCacheValues(cacheName);
		}
		return new ResponseEntity<>(status);
	}

}
