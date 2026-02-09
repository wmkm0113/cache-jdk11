/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.cache.manager.impl;

import org.nervousync.cache.api.CacheClient;
import org.nervousync.cache.api.CacheManager;
import org.nervousync.cache.client.impl.CacheClientImpl;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.ProviderManager;
import org.nervousync.utils.core.StringUtils;
import org.nervousync.utils.logger.LoggerUtils;

import java.util.*;

/**
 * <h2 class="en-US">Cache manager implement class</h2>
 * <h2 class="zh-CN">缓存管理器的实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 26, 2018 15:22:21 $
 */
public final class CacheManagerImpl implements CacheManager {

	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(CacheManagerImpl.class);

	/**
	 * <span class="en-US">Registered cache agent instance map</span>
	 * <span class="zh-CN">注册的缓存实例与缓存名称的对应关系</span>
	 */
	private static final Hashtable<String, CacheClient> REGISTERED_CACHE = new Hashtable<>();

	@Override
	public boolean register(final String cacheName, final CacheConfig cacheConfig) {
		if (StringUtils.isEmpty(cacheName) || !ProviderManager.registeredProvider(cacheConfig.getProviderName())) {
			return Boolean.FALSE;
		}
		if (REGISTERED_CACHE.containsKey(cacheName)) {
			CacheClient cacheClient = REGISTERED_CACHE.get(cacheName);
			if (cacheClient.match(cacheConfig.getLastModified())) {
				LOGGER.warn("Override_Cache_Config", cacheName);
				return Boolean.TRUE;
			}
			cacheClient.destroy();
			REGISTERED_CACHE.remove(cacheName);
		}

		try {
			REGISTERED_CACHE.put(cacheName, new CacheClientImpl(cacheConfig));
			return Boolean.TRUE;
		} catch (CacheException e) {
			LOGGER.error("Register_Cache_Error");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack_Message_Error", e);
			}
			return Boolean.FALSE;
		}
	}

	@Override
	public boolean registered(String cacheName) {
		return REGISTERED_CACHE.containsKey(cacheName);
	}

	@Override
	public CacheClient client(final String cacheName) {
		return REGISTERED_CACHE.get(cacheName);
	}

	@Override
	public void deregister(final String cacheName) {
		Optional.ofNullable(REGISTERED_CACHE.remove(cacheName)).ifPresent(CacheClient::destroy);
	}

	@Override
	public void destroy() {
		REGISTERED_CACHE.values().forEach(CacheClient::destroy);
		REGISTERED_CACHE.clear();
	}
}
