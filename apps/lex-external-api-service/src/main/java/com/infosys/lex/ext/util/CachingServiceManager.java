package com.infosys.lex.ext.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CachingServiceManager {
	@Autowired
	CacheManager cacheManager;

	public void evictSingleCacheValue(String cacheName, String cacheKey) {
		cacheManager.getCache(cacheName).evict(cacheKey);
	}

	public void evictAllCacheValues(String cacheName) {
		cacheManager.getCache(cacheName).clear();
	}
}
