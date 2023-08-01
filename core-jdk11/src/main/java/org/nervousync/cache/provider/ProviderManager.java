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
package org.nervousync.cache.provider;

import org.nervousync.cache.annotation.CacheProvider;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.util.*;

/**
 * <h2 class="en">Cache provider manager</h2>
 * <h2 class="zh-CN">缓存适配器管理器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 9/16/2020 10:28 AM $
 */
public final class ProviderManager {

	/**
	 * <span class="en">Logger instance</span>
	 * <span class="zhs">日志实例</span>
	 */
	private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(ProviderManager.class);

	/**
	 * <span class="en">Registered cache provider map</span>
	 * <span class="zhs">注册的缓存实现类与名称对应关系</span>
	 */
	private static final Hashtable<String, Class<?>> REGISTERED_PROVIDERS = new Hashtable<>();

	static {
		//  Register all cache providers by Java SPI
		ServiceLoader.load(Provider.class)
				.stream()
				.filter(provider -> provider.get().getClass().isAnnotationPresent(CacheProvider.class))
				.forEach(provider -> registerProvider(provider.get().getClass()));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registered_Providers_Count_Cache_Debug", REGISTERED_PROVIDERS.size());
		}
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
	 * <h3 class="en">Retrieve provider class by given provider name</h3>
	 * <h3 class="zhs">根据指定的适配器名称获取注册的适配器类</h3>
	 *
	 * @param providerName  <span class="en">Cache provider name</span>
	 *                      <span class="zhs">缓存适配器名称</span>
	 * @return  <span class="en">Register provider class</span>
	 *          <span class="zhs">注册适配器类</span>
	 */
	public static Class<?> providerClass(final String providerName) {
		if (StringUtils.isEmpty(providerName)) {
			return null;
		}
		return REGISTERED_PROVIDERS.get(providerName);
	}

	/**
	 * <h3 class="en">Register cache provider implement class manual</h3>
	 * <h3 class="zhs">注册缓存适配器</h3>
	 *
	 * @param providerClass     <span class="en">Cache provider implements class</span>
	 *                          <span class="zhs">缓存适配器实现类</span>
	 */
	private static void registerProvider(final Class<?> providerClass) {
		Optional.ofNullable(providerClass.getAnnotation(CacheProvider.class))
				.ifPresent(cacheProvider -> {
					if (REGISTERED_PROVIDERS.containsKey(cacheProvider.name())) {
						LOGGER.warn("Override_Cache_Provider",
								cacheProvider.name(), REGISTERED_PROVIDERS.get(cacheProvider.name()).getName(),
								providerClass.getName());
					}
					REGISTERED_PROVIDERS.put(cacheProvider.name(), providerClass);
				});
	}
}
