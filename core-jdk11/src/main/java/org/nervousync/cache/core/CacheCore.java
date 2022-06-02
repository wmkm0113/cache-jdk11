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
package org.nervousync.cache.core;

import org.nervousync.cache.annotation.CacheProvider;
import org.nervousync.cache.provider.Provider;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;

import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The type Cache core.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 11/21/2019 11:20 AM $
 */
public final class CacheCore {

	/**
	 * <span class="en">Logger instance</span>
	 * <span class="zhs">日志实例</span>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheCore.class);

	/**
	 * <span class="en">Default system cache name define</span>
	 * <span class="zhs">定义默认的系统缓存名称</span>
	 */
	private static final String SYSTEM_CACHE_NAME = "SystemCache";

	/**
	 * <span class="en">Registered cache provider map</span>
	 * <span class="zhs">注册的缓存实现类与名称对应关系</span>
	 */
	private static final Hashtable<String, Class<?>> REGISTERED_PROVIDERS = new Hashtable<>();

	/**
	 * <span class="en">Registered cache agent instance map</span>
	 * <span class="zhs">注册的缓存实例与缓存名称的对应关系</span>
	 */
	private static final Hashtable<String, CacheCore.Agent> REGISTERED_CACHE = new Hashtable<>();

	static {
		//  Register all cache providers by Java SPI
		ServiceLoader.load(Provider.class).forEach(provider -> {
			if (provider.getClass().isAnnotationPresent(CacheProvider.class)) {
				registerProvider(provider.getClass());
			}
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registered cache provider number: {}", REGISTERED_PROVIDERS.size());
		}
	}

	/**
	 * Constructor for CacheCore
	 */
	private CacheCore() {
	}

	/**
	 * <h3 class="en">Retrieve registered provider name list</h3>
	 * <h3 class="zhs">读取已注册的缓存适配器名称列表</h3>
	 *
	 * @return  <span class="en">Registered provider name list</span>
	 *          <span class="zhs">已注册的缓存适配器名称列表</span>
	 */
	public static List<String> registeredProviderNames() {
		return new ArrayList<>(REGISTERED_PROVIDERS.keySet());
	}

	/**
	 * <h3 class="en">Check register status of given provider name</h3>
	 * <h3 class="zhs">检查给定的缓存适配器名称是否已经注册</h3>
	 *
	 * @param providerName  <span class="en">Cache provider name</span>
	 *                      <span class="zhs">缓存适配器名称</span>
	 * @return  <span class="en">Register status</span>
	 *          <span class="zhs">注册状态</span>
	 */
	public static boolean registeredProvider(final String providerName) {
		if (StringUtils.isEmpty(providerName)) {
			return Boolean.FALSE;
		}
		return REGISTERED_PROVIDERS.containsKey(providerName);
	}

	/**
	 * <h3 class="en">Register cache provider implement class manual</h3>
	 * <h3 class="zhs">注册缓存适配器</h3>
	 *
	 * @param providerClass     <span class="en">Cache provider implements class</span>
	 *                          <span class="zhs">缓存适配器实现类</span>
	 */
	public static void registerProvider(final Class<?> providerClass) {
		if (providerClass != null && providerClass.isAnnotationPresent(CacheProvider.class)) {
			CacheProvider cacheProvider = providerClass.getAnnotation(CacheProvider.class);
			if (REGISTERED_PROVIDERS.containsKey(cacheProvider.name())) {
				LOGGER.warn("Override cache provider name: {}, replace class: {}, new class: {}",
						cacheProvider.name(), REGISTERED_PROVIDERS.get(cacheProvider.name()).getName(),
						providerClass.getName());
			}
			REGISTERED_PROVIDERS.put(cacheProvider.name(), providerClass);
		}
	}

	/**
	 * <h3 class="en">Remove registered cache provider implement class manual</h3>
	 * <h3 class="zhs">移除已经注册的缓存适配器实现类</h3>
	 *
	 * @param providerName  <span class="en">Cache provider name</span>
	 *                      <span class="zhs">缓存适配器名称</span>
	 */
	public static void removeProvider(final String providerName) {
		REGISTERED_PROVIDERS.remove(providerName);
	}

	/**
	 * <h3 class="en">Initialize system cache</h3>
	 * <h3 class="zhs">初始化系统缓存</h3>
	 *
	 * @param cacheConfig   <span class="en">System cache config instance</span>
	 *                      <span class="zhs">系统缓存配置实例</span>
	 * @return  <span class="en">Initialize result</span>
	 *          <span class="zhs">初始化结果</span>
	 */
	public boolean systemCache(final CacheConfig cacheConfig) {
		return registerCache(SYSTEM_CACHE_NAME, cacheConfig);
	}

	/**
	 * <h3 class="en">Initialize cache by given configure instance</h3>
	 * <h3 class="zhs">使用指定的缓存配置实例初始化缓存</h3>
	 *
	 * @param cacheName     <span class="en">Cache name, must not same as "SystemCache"</span>
	 *                      <span class="zhs">缓存名称，不允许为"SystemCache"</span>
	 * @param cacheConfig   <span class="en">System cache config instance</span>
	 *                      <span class="zhs">系统缓存配置实例</span>
	 * @return  <span class="en">Initialize result</span>
	 *          <span class="zhs">初始化结果</span>
	 */
	public static boolean registerCache(final String cacheName, final CacheConfig cacheConfig) {
		if (cacheName == null || cacheConfig == null || SYSTEM_CACHE_NAME.equalsIgnoreCase(cacheName)
				|| !REGISTERED_PROVIDERS.containsKey(cacheConfig.getProviderName())) {
			return Boolean.FALSE;
		}
		if (REGISTERED_CACHE.containsKey(cacheName)) {
			LOGGER.warn("Override cache config, cache name: {}", cacheName);
		}

		try {
			REGISTERED_CACHE.put(cacheName,
					new Agent(cacheConfig, REGISTERED_PROVIDERS.get(cacheConfig.getProviderName())));
			return Boolean.TRUE;
		} catch (CacheException e) {
			LOGGER.error("Generate nervousync cache instance error! ");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
			return Boolean.FALSE;
		}
	}

	/**
	 * <h3 class="en">Retrieve system agent instance</h3>
	 * <h3 class="zhs">获取系统缓存对象实例</h3>
	 *
	 * @return  <span class="en">System cache agent instance</span>
	 *          <span class="zhs">系统缓存对象实例</span>
	 */
	public static Optional<CacheCore.Agent> systemAgent() {
		return cacheAgent(SYSTEM_CACHE_NAME);
	}

	/**
	 * <h3 class="en">Retrieve cache agent instance by given cache name</h3>
	 * <h3 class="zhs">使用指定的缓存名称获取缓存对象实例</h3>
	 *
	 * @param cacheName     <span class="en">Cache name</span>
	 *                      <span class="zhs">缓存名称</span>
	 * @return  <span class="en">System cache agent instance</span>
	 *          <span class="zhs">系统缓存对象实例</span>
	 */
	public static Optional<CacheCore.Agent> cacheAgent(String cacheName) {
		return Optional.ofNullable(REGISTERED_CACHE.get(cacheName));
	}

	/**
	 * <h3 class="en">Destroy cache agent instance</h3>
	 * <h3 class="zhs">销毁缓存对象实例</h3>
	 *
	 * @param cacheName     <span class="en">Cache name</span>
	 *                      <span class="zhs">缓存名称</span>
	 */
	public static void destroyCache(String cacheName) {
		Optional.ofNullable(REGISTERED_CACHE.remove(cacheName)).ifPresent(Agent::destroy);
	}

	/**
	 * <h3 class="en">Destroy all cache agent instance</h3>
	 * <h3 class="zhs">销毁所有注册的缓存对象实例</h3>
	 */
	public static void destroy() {
		Iterator<Map.Entry<String, CacheCore.Agent>> iterator = REGISTERED_CACHE.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, CacheCore.Agent> entry = iterator.next();
			entry.getValue().destroy();
			iterator.remove();
		}
	}

	/**
	 * <h2 class="en">Cache agent</h2>
	 * <h2 class="zhs">缓存实例</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
	 * @version $Revision: 1.0 $ $Date: Jun 26, 2018 $
	 */
	public static final class Agent {

		/**
		 * <span class="en">Current cache provider instance</span>
		 * <span class="zhs">缓存实现对象</span>
		 */
		private final AbstractProvider cacheProvider;

		/**
		 * Constructor for cache agent
		 *
		 * @param cacheConfig           <span class="en">System cache config instance</span>
		 *                              <span class="zhs">系统缓存配置实例</span>
		 * @param providerImplClass     <span class="en">Cache provider implement class</span>
		 *                              <span class="zhs">缓存适配器实现类</span>
		 * @throws CacheException       <span class="en">Generate instance of provider failed or provider implement class not extends with AbstractCacheProvider</span>
		 *                              <span class="zhs">缓存适配器实现类没有继承AbstractCacheProvider或初始化缓存适配器对象出错</span>
		 */
		private Agent(CacheConfig cacheConfig, Class<?> providerImplClass) throws CacheException {
			if (providerImplClass != null && AbstractProvider.class.isAssignableFrom(providerImplClass)) {
				try {
					this.cacheProvider = (AbstractProvider)providerImplClass.getDeclaredConstructor().newInstance();
					this.cacheProvider.initialize(cacheConfig);
					return;
				} catch (ReflectiveOperationException e) {
					throw new CacheException(e);
				}
			}
			throw new CacheException("Provider implement class is invalid! ");
		}

		/**
		 * <h3 class="en">Set key-value to cache server by default expire time</h3>
		 * <h3 class="zhs">使用默认的过期时间设置缓存信息</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param value		<span class="en">Cache value</span>
		 *                  <span class="zhs">缓存数据</span>
		 */
		public void set(String key, String value) {
			this.logInfo(key, value);
			this.cacheProvider.set(key, value);
		}

		/**
		 * <h3 class="en">Set key-value to cache server and set expire time</h3>
		 * <h3 class="zhs">使用指定的过期时间设置缓存信息</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param value		<span class="en">Cache value</span>
		 *                  <span class="zhs">缓存数据</span>
		 * @param expire	<span class="en">Expire time</span>
		 *                  <span class="zhs">过期时间</span>
		 */
		public void set(String key, String value, int expire) {
			this.logInfo(key, value);
			this.cacheProvider.set(key, value, expire);
		}

		/**
		 * <h3 class="en">Add a new key-value to cache server by default expire time</h3>
		 * <h3 class="zhs">使用默认的过期时间添加缓存信息</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param value		<span class="en">Cache value</span>
		 *                  <span class="zhs">缓存数据</span>
		 */
		public void add(String key, String value) {
			this.logInfo(key, value);
			this.cacheProvider.add(key, value);
		}

		/**
		 * <h3 class="en">Add a new key-value to cache server and set expire time</h3>
		 * <h3 class="zhs">使用指定的过期时间添加缓存信息</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param value		<span class="en">Cache value</span>
		 *                  <span class="zhs">缓存数据</span>
		 * @param expire	<span class="en">Expire time</span>
		 *                  <span class="zhs">过期时间</span>
		 */
		public void add(String key, String value, int expire) {
			this.logInfo(key, value);
			this.cacheProvider.add(key, value, expire);
		}

		 /**
		 * <h3 class="en">Replace exists value of given key by given value by default expire time</h3>
		 * <h3 class="zhs">使用默认的过期时间替换已存在的缓存信息</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param value		<span class="en">Cache value</span>
		 *                  <span class="zhs">缓存数据</span>
		 */
		public void replace(String key, String value) {
			this.logInfo(key, value);
			this.cacheProvider.replace(key, value);
		}

		/**
		 * <h3 class="en">Replace exists value of given key by given value and set expire time</h3>
		 * <h3 class="zhs">使用指定的过期时间替换已存在的缓存信息</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param value		<span class="en">Cache value</span>
		 *                  <span class="zhs">缓存数据</span>
		 * @param expire	<span class="en">Expire time</span>
		 *                  <span class="zhs">过期时间</span>
		 */
		public void replace(String key, String value, int expire) {
			this.logInfo(key, value);
			this.cacheProvider.replace(key, value, expire);
		}

		/**
		 * <h3 class="en">Set expire time to new given expire value which cache key was given</h3>
		 * <h3 class="zhs">将指定的缓存键值过期时间设置为指定的新值</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param expire	<span class="en">New expire time</span>
		 *                  <span class="zhs">新的过期时间</span>
		 */
		public void expire(String key, int expire) {
			this.cacheProvider.expire(key, expire);
		}

		/**
		 * Operate touch to given keys
		 * @param keys      Keys
		 */
		public void touch(String... keys) {
			this.cacheProvider.touch(keys);
		}

		/**
		 * <h3 class="en">Remove cache key-value from cache server</h3>
		 * <h3 class="zhs">移除指定的缓存键值</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 */
		public void delete(String key) {
			this.cacheProvider.delete(key);
		}

		/**
		 * <h3 class="en">Read cache value from cache key which cache key was given</h3>
		 * <h3 class="zhs">读取指定缓存键值对应的缓存数据</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @return  <span class="en">Cache value or null if cache key was not exists or it was expired</span>
		 *          <span class="zhs">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
		 */
		public String get(String key) {
			if (StringUtils.isEmpty(key)) {
				return null;
			}
			return this.cacheProvider.get(key);
		}

		/**
		 * <h3 class="en">Increment data by given cache key and value</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param step      <span class="en">Increment step value</span>
		 *                  <span class="zhs">自增步进值</span>
		 * @return  <span class="en">Operate result</span>
		 *          <span class="zhs">操作结果</span>
		 */
		public long incr(String key, long step) {
			if (StringUtils.isEmpty(key)) {
				return Globals.DEFAULT_VALUE_LONG;
			}
			return this.cacheProvider.incr(key, step);
		}

		/**
		 * <h3 class="en">Decrement data by given cache key and value</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param step      <span class="en">Decrement step value</span>
		 *                  <span class="zhs">自减步进值</span>
		 * @return  <span class="en">Operate result</span>
		 *          <span class="zhs">操作结果</span>
		 */
		public long decr(String key, long step) {
			if (StringUtils.isEmpty(key)) {
				return Globals.DEFAULT_VALUE_LONG;
			}
			return this.cacheProvider.decr(key, step);
		}

		/**
		 * <h3 class="en">Destroy agent instance</h3>
		 * <h3 class="zhs">销毁缓存对象</h3>
		 */
		public void destroy() {
			this.cacheProvider.destroy();
		}

		/**
		 * <h3 class="en">Logging cache key and value when debug mode was enabled</h3>
		 * <h3 class="zhs">当调试模式开启时，在日志中输出缓存键值和数据</h3>
		 *
		 * @param key       <span class="en">Cache key</span>
		 *                  <span class="zhs">缓存键值</span>
		 * @param value		<span class="en">Cache value</span>
		 *                  <span class="zhs">缓存数据</span>
		 */
		private void logInfo(String key, Object value) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Cached key: {}", key);
				LOGGER.debug("Cached value: {}", value);
			}
		}
	}
}
