package com.infosys.lex.notification.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CachingService {

	@Autowired
	CachingServiceManager cachingServiceManager;


	
	/**
	 * app config  cache
	 */
	@Scheduled(fixedRate = 3600000)
	public void clearConfigKey() {
		cachingServiceManager.evictAllCacheValues("config_key_value");
	}
	
	
	
	
}
