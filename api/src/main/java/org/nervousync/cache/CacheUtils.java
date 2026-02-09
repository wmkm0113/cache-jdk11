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
package org.nervousync.cache;

import org.nervousync.cache.api.CacheClient;
import org.nervousync.cache.api.CacheManager;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.configs.ConfigureManager;
import org.nervousync.utils.core.ObjectUtils;
import org.nervousync.utils.core.StringUtils;
import org.nervousync.utils.logger.LoggerUtils;

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * <h2 class="en-US">Cache utilities instance</h2>
 * <h2 class="zh-CN">缓存工具类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 18, 2022 17:21:36 $
 */
@SuppressWarnings("unused")
public final class CacheUtils {

	/**
	 * <span class="en-US">Multilingual supported logger instance</span>
	 * <span class="zh-CN">多语言支持的日志对象</span>
	 */
	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(CacheUtils.class);
	/**
	 * <span class="en-US">Cache utilities singleton instance object</span>
	 * <span class="zh-CN">缓存工具类单例实例对象</span>
	 */
	private static CacheUtils INSTANCE = null;
	/**
	 * <span class="en-US">Cache manager instance</span>
	 * <span class="zh-CN">缓存管理器实例</span>
	 */
	private final CacheManager cacheManager;
	/**
	 * <span class="en-US">Cache manager instance</span>
	 * <span class="zh-CN">配置信息管理器实例</span>
	 */
	private final ConfigureManager configureManager;

	/**
	 * <h3 class="en-US">Constructor for cache utilities</h3>
	 * <h3 class="zh-CN">缓存工具类构建方法</h3>
	 */
	private CacheUtils(final CacheManager cacheManager) {
		this.cacheManager = cacheManager;
		if (!this.registerCache(CacheGlobals.DEFAULT_CACHE_NAME)) {
			LOGGER.warn("Register_Default_Failed");
		}
		this.configureManager = ConfigureManager.getInstance();
	}

	/**
	 * <h3 class="en-US">Retrieve singleton instance of CacheUtils</h3>
	 * <h3 class="zh-CN">获取缓存工具类的单例实例</h3>
	 *
	 * @return <span class="en-US">Instance of cache utilities</span>
	 * <span class="zh-CN">缓存工具类的单例实例</span>
	 */
	public static CacheUtils getInstance() {
		initialize();
		return CacheUtils.INSTANCE;
	}

	/**
	 * <h3 class="en-US">Register the cache with the specified cache identification code</h3>
	 * <span class="en-US">
	 * Read the corresponding cache configuration information from the configuration information manager
	 * according to the specified cache identification code.
	 * </span>
	 * <h3 class="zh-CN">使用指定的缓存识别代码注册缓存</h3>
	 * <span class="zh-CN">根据指定的缓存识别代码从配置信息管理器中读取相应的缓存配置信息</span>
	 *
	 * @param cacheName <span class="en-US">Cache identifies name</span>
	 *                  <span class="zh-CN">缓存识别名称</span>
	 * @return <span class="en-US">Register result, <code>true</code> for register succeed, <code>false</code> for register failed</span>
	 * <span class="zh-CN">注册结果，成功返回Boolean.TRUE，失败返回Boolean.FALSE</span>
	 */
	public static boolean register(final String cacheName) {
		initialize();
		if (StringUtils.isEmpty(cacheName) || INSTANCE == null) {
			return Boolean.FALSE;
		}
		return INSTANCE.registerCache(cacheName);
	}

	/**
	 * <h3 class="en-US">Register cache</h3>
	 * <h3 class="zh-CN">注册缓存</h3>
	 *
	 * @param cacheName   <span class="en-US">Cache identifies name</span>
	 *                    <span class="zh-CN">缓存识别名称</span>
	 * @param cacheConfig <span class="en-US">Cache configure information</span>
	 *                    <span class="zh-CN">缓存配置信息</span>
	 * @return <span class="en-US">Register result, <code>true</code> for register succeed, <code>false</code> for register failed</span>
	 * <span class="zh-CN">注册结果，成功返回Boolean.TRUE，失败返回Boolean.FALSE</span>
	 */
	public static boolean register(final String cacheName, final CacheConfig cacheConfig) {
		initialize();
		if (INSTANCE == null || StringUtils.isEmpty(cacheName)
				|| ObjectUtils.nullSafeEquals(CacheGlobals.DEFAULT_CACHE_NAME, cacheName)) {
			return Boolean.FALSE;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Register_Cache_Debug", cacheName, cacheConfig);
		}
		return INSTANCE.cacheManager.register(cacheName, cacheConfig);
	}

	/**
	 * <h3 class="en-US">Check given cache name was registered</h3>
	 * <h3 class="zh-CN">使用指定的缓存名称、配置信息注册缓存</h3>
	 *
	 * @param cacheName <span class="en-US">Cache identifies name</span>
	 *                  <span class="zh-CN">缓存识别名称</span>
	 */
	public static boolean registered(final String cacheName) {
		initialize();
		if (INSTANCE == null || StringUtils.isEmpty(cacheName)) {
			return Boolean.FALSE;
		}
		if (INSTANCE.cacheManager.registered(cacheName)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Check_Register_Cache_Debug", cacheName);
			}
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * <h3 class="en-US">Retrieve the cache client by given cache name</h3>
	 * <h3 class="zh-CN">根据给定的缓存识别名称获取缓存客户端</h3>
	 *
	 * @param cacheName <span class="en-US">Cache identifies name</span>
	 *                  <span class="zh-CN">缓存识别名称</span>
	 * @return <span class="en-US">CacheClient instance or null if given cache name not registered</span>
	 * <span class="zh-CN">缓存客户端实例，如果给定的缓存识别名称未找到，则返回null</span>
	 */
	public static CacheClient client(final String cacheName) {
		initialize();
		if (INSTANCE == null || StringUtils.isEmpty(cacheName)) {
			return null;
		}
		return INSTANCE.cacheManager.client(cacheName);
	}

	/**
	 * <h3 class="en-US">Deregister cache</h3>
	 * <h3 class="zh-CN">取消注册缓存</h3>
	 *
	 * @param cacheName <span class="en-US">Cache identifies name</span>
	 *                  <span class="zh-CN">缓存识别名称</span>
	 */
	public static void deregister(final String cacheName) {
		initialize();
		if (INSTANCE == null) {
			return;
		}
		INSTANCE.cacheManager.deregister(cacheName);
	}

	/**
	 * <h3 class="en-US">Destroy singleton instance</h3>
	 * <h3 class="zh-CN">取消注册缓存</h3>
	 */
	public static void destroy() {
		if (INSTANCE != null) {
			INSTANCE.cacheManager.destroy();
			INSTANCE = null;
		}
	}

	/**
	 * <h3 class="en-US">Register the cache with the specified cache identification code</h3>
	 * <span class="en-US">
	 * Read the corresponding cache configuration information from the configuration information manager
	 * according to the specified cache identification code.
	 * </span>
	 * <h3 class="zh-CN">使用指定的缓存识别代码注册缓存</h3>
	 * <span class="zh-CN">根据指定的缓存识别代码从配置信息管理器中读取相应的缓存配置信息</span>
	 *
	 * @param cacheName <span class="en-US">Cache identifies name</span>
	 *                  <span class="zh-CN">缓存识别名称</span>
	 * @return <span class="en-US">Register result, <code>true</code> for register succeed, <code>false</code> for register failed</span>
	 * <span class="zh-CN">注册结果，成功返回Boolean.TRUE，失败返回Boolean.FALSE</span>
	 */
	private boolean registerCache(final String cacheName) {
		if (StringUtils.isEmpty(cacheName) || this.configureManager == null) {
			return Boolean.FALSE;
		}
		return Optional.ofNullable(this.configureManager.readConfigure(CacheConfig.class, cacheName))
				.map(cacheConfig -> INSTANCE.cacheManager.register(cacheName, cacheConfig))
				.orElse(Boolean.FALSE);
	}

	private static void initialize() {
		if (INSTANCE == null) {
			synchronized (CacheUtils.class) {
				try {
					INSTANCE = ServiceLoader.load(CacheManager.class)
							.findFirst()
							.map(CacheUtils::new)
							.orElseThrow(() -> new CacheException(0x000C00000001L));
				} catch (CacheException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Stack_Message_Error", e);
					}
				}
			}
		}
	}
}
