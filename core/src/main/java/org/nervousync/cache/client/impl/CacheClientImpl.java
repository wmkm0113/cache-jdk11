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

import jakarta.annotation.Nonnull;
import org.nervousync.cache.api.CacheClient;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.ProviderManager;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.Globals;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.util.List;
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
	public boolean copy(@Nonnull final String source, @Nonnull final String destination) {
		return this.cacheProvider.copy(source, destination);
	}

	@Override
	public long del(@Nonnull final String... keys) {
		return this.cacheProvider.del(keys);
	}

	@Override
	public long exists(@Nonnull final String... keys) {
		return this.cacheProvider.exists(keys);
	}

	@Override
	public boolean set(@Nonnull final String key, @Nonnull final String value, final int expire) {
		this.logInfo(key, value);
		return this.cacheProvider.set(key, value, expire);
	}

	@Override
	public long setRange(@Nonnull final String key, final int offset, @Nonnull final String value) {
		return this.cacheProvider.setRange(key, offset, value);
	}

	@Override
	public long strLen(@Nonnull final String key) {
		return this.cacheProvider.strLen(key);
	}

	@Override
	public boolean add(@Nonnull final String key, @Nonnull final String value, final int expire) {
		this.logInfo(key, value);
		return this.cacheProvider.add(key, value, expire);
	}

	@Override
	public long append(@Nonnull final String key, @Nonnull final String append) {
		return this.cacheProvider.append(key, append);
	}

	@Override
	public boolean replace(@Nonnull final String key, @Nonnull final String value, final int expire) {
		this.logInfo(key, value);
		return this.cacheProvider.replace(key, value, expire);
	}

	@Override
	public void expire(@Nonnull final String key, final int expire) {
		this.cacheProvider.expire(key, expire);
	}

	@Override
	public List<String> keys(@Nonnull final String pattern) {
		return this.cacheProvider.keys(pattern);
	}

	@Override
	public boolean persist(@Nonnull final String key) {
		return this.cacheProvider.persist(key);
	}

	@Override
	public boolean rename(@Nonnull final String key, @Nonnull final String newKey) {
		return this.cacheProvider.rename(key, newKey);
	}

	@Override
	public long touch(@Nonnull String... keys) {
		return this.cacheProvider.touch(keys);
	}

	@Override
	public long ttl(@Nonnull final String key) {
		return this.cacheProvider.ttl(key);
	}

	@Override
	public String get(@Nonnull final String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		return this.cacheProvider.get(key);
	}

	@Override
	public String getDel(@Nonnull final String key) {
		return this.cacheProvider.getDel(key);
	}

	@Override
	public String getEx(@Nonnull final String key, final int expire) {
		return this.cacheProvider.getEx(key, expire);
	}

	@Override
	public String getRange(@Nonnull final String key, final int begin, final int end) {
		return this.cacheProvider.getRange(key, begin, end);
	}

	@Override
	public String getSet(@Nonnull final String key, @Nonnull final String value) {
		return this.cacheProvider.getSet(key, value);
	}

	@Override
	public long incr(@Nonnull final String key, final long step) {
		if (StringUtils.isEmpty(key)) {
			return Globals.DEFAULT_VALUE_LONG;
		}
		return this.cacheProvider.incr(key, step);
	}

	@Override
	public double incrFloat(@Nonnull final String key, final double step) {
		return this.cacheProvider.incrFloat(key, step);
	}

	@Override
	public String lcs(@Nonnull final String key1, @Nonnull final String key2) {
		return this.cacheProvider.lcs(key1, key2);
	}

	@Override
	public long lcsLen(@Nonnull final String key1, @Nonnull final String key2) {
		return this.cacheProvider.lcsLen(key1, key2);
	}

	@Override
	public List<String> mget(@Nonnull final String... keys) {
		return this.cacheProvider.mget(keys);
	}

	@Override
	public boolean mset(@Nonnull final String... keyvalues) {
		return this.cacheProvider.mset(keyvalues);
	}

	@Override
	public boolean msetnx(@Nonnull final String... keyvalues) {
		return this.cacheProvider.msetnx(keyvalues);
	}

	@Override
	public long decr(@Nonnull final String key, final long step) {
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
