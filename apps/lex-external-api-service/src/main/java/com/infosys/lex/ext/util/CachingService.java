package com.infosys.lex.ext.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.sunbird.common.models.util.ProjectLogger;

@Component
public class CachingService {

	@Autowired
	CachingServiceManager cachingServiceManager;

	@Scheduled(fixedRate = 86400000)
	public void clearCache() {
		ProjectLogger.log("Evicting cache");
		cachingServiceManager.evictAllCacheValues(LexConstants.JSON_PROCESSOR_CACHE);
	}
}
