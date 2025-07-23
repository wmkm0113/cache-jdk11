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
package org.nervousync.cache.client.impl;

import org.nervousync.cache.api.CacheClient;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.ProviderManager;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.Globals;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.util.Optional;

/**
 * <h2 class="en-US">Cache client implement class</h2>
 * <h2 class="zh-CN">缓存对象的实现类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 26, 2018 14:17:27 $
 */
public final class CacheClientImpl implements CacheClient {

	/**
	 * <span class="en-US">Multilingual supported logger instance</span>
	 * <span class="zh-CN">多语言支持的日志对象</span>
	 */
	private final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

	/**
	 * <span class="en-US">Current cache provider instance</span>
	 * <span class="zh-CN">缓存适配器实例</span>
	 */
	private final AbstractProvider cacheProvider;
	/**
	 * <span class="en-US">Last modified timestamp</span>
	 * <span class="zh-CN">最后修改时间戳</span>
	 */
	private final long lastModified;

	/**
	 * Constructor for cache agent
	 *
	 * @param cacheConfig <span class="en-US">System cache config instance</span>
	 *                    <span class="zh-CN">系统缓存配置实例</span>
	 * @throws CacheException <span class="en-US">Generate instance of provider failed or provider implement class not extends with AbstractCacheProvider</span>
	 *                        <span class="zh-CN">缓存适配器实现类没有继承AbstractCacheProvider或初始化缓存适配器对象出错</span>
	 */
	public CacheClientImpl(final CacheConfig cacheConfig) throws CacheException {
		this.cacheProvider = Optional.ofNullable(ProviderManager.providerClass(cacheConfig.getProviderName()))
				.filter(AbstractProvider.class::isAssignableFrom)
				.map(providerClass -> (AbstractProvider) ObjectUtils.newInstance(providerClass))
				.orElseThrow(() -> new CacheException(0x000C00000003L));
		this.cacheProvider.initialize(cacheConfig);
		this.lastModified = cacheConfig.getLastModified();
	}

	@Override
	public boolean match(final long lastModified) {
		return lastModified != Globals.DEFAULT_VALUE_LONG && this.lastModified == lastModified;
	}

	@Override
	public void set(final String key, final String value) {
		this.logInfo(key, value);
		this.cacheProvider.set(key, value);
	}

	@Override
	public void set(final String key, final String value, final int expire) {
		this.logInfo(key, value);
		this.cacheProvider.set(key, value, expire);
	}

	@Override
	public void add(final String key, final String value) {
		this.logInfo(key, value);
		this.cacheProvider.add(key, value);
	}

	@Override
	public void add(final String key, final String value, final int expire) {
		this.logInfo(key, value);
		this.cacheProvider.add(key, value, expire);
	}

	@Override
	public void replace(final String key, final String value) {
		this.logInfo(key, value);
		this.cacheProvider.replace(key, value);
	}

	@Override
	public void replace(final String key, final String value, final int expire) {
		this.logInfo(key, value);
		this.cacheProvider.replace(key, value, expire);
	}

	@Override
	public void expire(final String key, final int expire) {
		this.cacheProvider.expire(key, expire);
	}

	@Override
	public void touch(final String... keys) {
		this.cacheProvider.touch(keys);
	}

	@Override
	public void delete(final String key) {
		this.cacheProvider.delete(key);
	}

	@Override
	public String get(final String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		return this.cacheProvider.get(key);
	}

	@Override
	public long incr(final String key, final long step) {
		if (StringUtils.isEmpty(key)) {
			return Globals.DEFAULT_VALUE_LONG;
		}
		return this.cacheProvider.incr(key, step);
	}

	@Override
	public long decr(final String key, final long step) {
		if (StringUtils.isEmpty(key)) {
			return Globals.DEFAULT_VALUE_LONG;
		}
		return this.cacheProvider.decr(key, step);
	}

	@Override
	public void destroy() {
		this.cacheProvider.destroy();
	}

	/**
	 * <h3 class="en-US">Logging cache key and value when debug mode was enabled</h3>
	 * <h3 class="zh-CN">当调试模式开启时，在日志中输出缓存键值和数据</h3>
	 *
	 * @param key   <span class="en-US">Cache key</span>
	 *              <span class="zh-CN">缓存键值</span>
	 * @param value <span class="en-US">Cache value</span>
	 *              <span class="zh-CN">缓存数据</span>
	 */
	private void logInfo(String key, Object value) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Info_Cache_Debug", key, value);
		}
	}
}
