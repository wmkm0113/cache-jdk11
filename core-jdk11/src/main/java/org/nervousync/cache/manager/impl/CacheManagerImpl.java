package org.nervousync.cache.manager.impl;

import org.nervousync.cache.api.CacheClient;
import org.nervousync.cache.api.CacheManager;
import org.nervousync.cache.client.impl.CacheClientImpl;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.ProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class CacheManagerImpl implements CacheManager {

	/**
	 * <span class="en">Logger instance</span>
	 * <span class="zhs">日志实例</span>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheManagerImpl.class);

	/**
	 * <span class="en">Registered cache agent instance map</span>
	 * <span class="zhs">注册的缓存实例与缓存名称的对应关系</span>
	 */
	private static final Hashtable<String, CacheClient> REGISTERED_CACHE = new Hashtable<>();

	public CacheManagerImpl() {
	}

	@Override
	public boolean register(String cacheName, Object cacheConfig) {
		if (!(cacheConfig instanceof CacheConfig)) {
			return Boolean.FALSE;
		}
		if (cacheName == null || !ProviderManager.registeredProvider(((CacheConfig) cacheConfig).getProviderName())) {
			return Boolean.FALSE;
		}
		if (REGISTERED_CACHE.containsKey(cacheName)) {
			LOGGER.warn("Override cache config, cache name: {}", cacheName);
		}

		try {
			REGISTERED_CACHE.put(cacheName,
					new CacheClientImpl((CacheConfig) cacheConfig,
							ProviderManager.providerClass(((CacheConfig)cacheConfig).getProviderName())));
			return Boolean.TRUE;
		} catch (CacheException e) {
			LOGGER.error("Generate nervousync cache instance error! ");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
			return Boolean.FALSE;
		}
	}

	@Override
	public CacheClient client(String cacheName) {
		return REGISTERED_CACHE.get(cacheName);
	}

	@Override
	public void deregister(String cacheName) {
		Optional.ofNullable(REGISTERED_CACHE.remove(cacheName)).ifPresent(CacheClient::destroy);
	}

	@Override
	public void destroy() {
		REGISTERED_CACHE.values().forEach(CacheClient::destroy);
		REGISTERED_CACHE.clear();
	}
}
