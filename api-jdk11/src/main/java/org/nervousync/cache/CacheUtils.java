package org.nervousync.cache;

import org.nervousync.cache.api.CacheClient;
import org.nervousync.cache.api.CacheManager;

import java.util.Optional;
import java.util.ServiceLoader;

public final class CacheUtils {

	private static final CacheManager CACHE_MANAGER;

	static {
		CACHE_MANAGER = ServiceLoader.load(CacheManager.class).findFirst().orElse(null);
	}

	private CacheUtils() {
	}

	public static boolean register(final String cacheName, final Object cacheConfig) {
		return Optional.ofNullable(CACHE_MANAGER)
				.map(cacheManager -> cacheManager.register(cacheName, cacheConfig))
				.orElse(Boolean.FALSE);
	}

	public static CacheClient client(final String cacheName) {
		return Optional.ofNullable(CACHE_MANAGER)
				.map(cacheManager -> cacheManager.client(cacheName))
				.orElse(null);
	}

	public static void deregister(final String cacheName) {
		Optional.ofNullable(CACHE_MANAGER).ifPresent(cacheManager -> cacheManager.deregister(cacheName));
	}

	public static void destroy() {
		Optional.ofNullable(CACHE_MANAGER).ifPresent(CacheManager::destroy);
	}
}
