/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CachingService {

	@Autowired
	CachingServiceManager cachingServiceManager;

	@Scheduled(fixedRate = 86400000)
	public void clearCache() {
		cachingServiceManager.evictAllCacheValues("mimeTypes");
	}

	@Scheduled(fixedRate = 3600000)
	public void clearInterestsCache() {
		cachingServiceManager.evictAllCacheValues("interests");
	}
	
	/**
	 * Mark as complete cache
	 */
	@Scheduled(fixedRate = 3600000)
	public void clearMACCache() {
		cachingServiceManager.evictAllCacheValues("macConfig");
	}
	
	/**
	 * app config  cache
	 */
	@Scheduled(fixedRate = 3600000)
	public void clearConfigKey() {
		cachingServiceManager.evictAllCacheValues("config_key_value");
	}
	
	// TnC cache
	@Scheduled(fixedRate = 86400000)
	public void clearTnCData() {
		cachingServiceManager.evictAllCacheValues("tncCache");
	}
	
	/**
	 * clear rootOrg content source cache
	 */
	@Scheduled(fixedRate = 3600000)
	public void clearContentSourceCache() {
		cachingServiceManager.evictAllCacheValues("root_org_content_source");
	}
	
	/**
	 * clear rootOrg content source details cache
	 */
	@Scheduled(fixedRate = 3600000)
	public void clearContentSourceDetailsCache() {
		cachingServiceManager.evictAllCacheValues("root_org_content_source_details");
	}
	
	/**
	 * clear rootOrg content source details for sourcename cache
	 */
	@Scheduled(fixedRate = 3600000)
	public void clearContentSourceDetailsForSourceNameCache() {
		cachingServiceManager.evictAllCacheValues("root_org_content_source_details_for_sourcename");
	}
}
