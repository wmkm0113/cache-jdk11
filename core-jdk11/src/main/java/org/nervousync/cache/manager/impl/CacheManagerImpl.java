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
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <h2 class="en">Cache manager implement class</h2>
 * <h2 class="zh-CN">缓存管理器的实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2018-12-26 15:22 $
 */
public final class CacheManagerImpl implements CacheManager {

	/**
	 * <span class="en">Logger instance</span>
	 * <span class="zh-CN">日志实例</span>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheManagerImpl.class);

	/**
	 * <span class="en">Registered cache agent instance map</span>
	 * <span class="zh-CN">注册的缓存实例与缓存名称的对应关系</span>
	 */
	private static final Hashtable<String, CacheClient> REGISTERED_CACHE = new Hashtable<>();

	public CacheManagerImpl() {
	}

	/**
	 * <h3 class="en">Register cache instance by given cache name and config instance</h3>
	 * <h3 class="zh-CN">使用指定的缓存名称、配置信息注册缓存</h3>
	 *
	 * @param cacheName     <span class="en">Cache identify name</span>
	 *                      <span class="zh-CN">缓存识别名称</span>
	 * @param cacheConfig	<span class="en">Cache config instance</span>
	 *                      <span class="zh-CN">缓存配置信息</span>
	 */
	@Override
	public boolean register(final String cacheName, final CacheConfig cacheConfig) {
		if (StringUtils.isEmpty(cacheName)) {
			return Boolean.FALSE;
		}
		if (REGISTERED_CACHE.containsKey(cacheName)) {
			LOGGER.warn("Override cache config, cache name: {}", cacheName);
		}

		try {
			REGISTERED_CACHE.put(cacheName, new CacheClientImpl(cacheConfig));
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
