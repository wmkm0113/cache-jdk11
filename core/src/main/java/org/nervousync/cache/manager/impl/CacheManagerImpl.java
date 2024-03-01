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
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

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

	public CacheManagerImpl() {
	}

	/**
	 * <h3 class="en-US">Register cache instance by given cache name and config instance</h3>
	 * <h3 class="zh-CN">使用指定的缓存名称、配置信息注册缓存</h3>
	 *
	 * @param cacheName     <span class="en-US">Cache identify name</span>
	 *                      <span class="zh-CN">缓存识别名称</span>
	 * @param cacheConfig	<span class="en-US">Cache config instance</span>
	 *                      <span class="zh-CN">缓存配置信息</span>
	 */
	@Override
	public boolean register(final String cacheName, final CacheConfig cacheConfig) {
		if (StringUtils.isEmpty(cacheName) || !ProviderManager.registeredProvider(cacheConfig.getProviderName())) {
			return Boolean.FALSE;
		}
		if (REGISTERED_CACHE.containsKey(cacheName)) {
			LOGGER.warn("Override_Cache_Config", cacheName);
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

	/**
	 * <h3 class="en-US">Check given cache name was registered</h3>
	 * <h3 class="zh-CN">使用指定的缓存名称、配置信息注册缓存</h3>
	 *
	 * @param cacheName     <span class="en-US">Cache identify name</span>
	 *                      <span class="zh-CN">缓存识别名称</span>
	 */
	@Override
	public boolean registered(String cacheName) {
		return REGISTERED_CACHE.containsKey(cacheName);
	}

	/**
	 * <h3 class="en-US">Retrieve cache client instance by given cache name</h3>
	 * <h3 class="zh-CN">使用指定的缓存名称获取缓存操作客户端</h3>
	 *
	 * @param cacheName     <span class="en-US">Cache identify name</span>
	 *                      <span class="zh-CN">缓存识别名称</span>
	 * @return  <span class="en-US">Cache client instance or null if cache name not registered</span>
	 *          <span class="zh-CN">缓存客户端实例，若缓存名称未注册则返回null</span>
	 */
	@Override
	public CacheClient client(final String cacheName) {
		return REGISTERED_CACHE.get(cacheName);
	}

	/**
	 * <h3 class="en-US">Remove cache instance from registered list</h3>
	 * <h3 class="zh-CN">移除指定的缓存</h3>
	 *
	 * @param cacheName     <span class="en-US">Cache identify name</span>
	 *                      <span class="zh-CN">缓存识别名称</span>
	 */
	@Override
	public void deregister(final String cacheName) {
		Optional.ofNullable(REGISTERED_CACHE.remove(cacheName)).ifPresent(CacheClient::destroy);
	}

	/**
	 * <h3 class="en-US">Destroy manager instance</h3>
	 * <h3 class="zh-CN">销毁当前的管理实例</h3>
	 */
	@Override
	public void destroy() {
		REGISTERED_CACHE.values().forEach(CacheClient::destroy);
		REGISTERED_CACHE.clear();
	}
}
