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
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2018-12-26 14:17 $
 */
public final class CacheClientImpl implements CacheClient {

    /**
     * <span class="en-US">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
    private final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

    /**
     * <span class="en-US">Current cache provider instance</span>
     * <span class="zh-CN">缓存适配器实例</span>
     */
    private final AbstractProvider cacheProvider;

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
    }

    /**
     * <h3 class="en-US">Set key-value to cache server by default expire time</h3>
     * <h3 class="zh-CN">使用默认的过期时间设置缓存信息</h3>
     *
     * @param key   <span class="en-US">Cache key</span>
     *              <span class="zh-CN">缓存键值</span>
     * @param value <span class="en-US">Cache value</span>
     *              <span class="zh-CN">缓存数据</span>
     */
    public void set(final String key, final String value) {
        this.logInfo(key, value);
        this.cacheProvider.set(key, value);
    }

    /**
     * <h3 class="en-US">Set key-value to cache server and set expire time</h3>
     * <h3 class="zh-CN">使用指定的过期时间设置缓存信息</h3>
     *
     * @param key    <span class="en-US">Cache key</span>
     *               <span class="zh-CN">缓存键值</span>
     * @param value  <span class="en-US">Cache value</span>
     *               <span class="zh-CN">缓存数据</span>
     * @param expire <span class="en-US">Expire time</span>
     *               <span class="zh-CN">过期时间</span>
     */
    public void set(final String key, final String value, final int expire) {
        this.logInfo(key, value);
        this.cacheProvider.set(key, value, expire);
    }

    /**
     * <h3 class="en-US">Add a new key-value to cache server by default expire time</h3>
     * <h3 class="zh-CN">使用默认的过期时间添加缓存信息</h3>
     *
     * @param key   <span class="en-US">Cache key</span>
     *              <span class="zh-CN">缓存键值</span>
     * @param value <span class="en-US">Cache value</span>
     *              <span class="zh-CN">缓存数据</span>
     */
    public void add(final String key, final String value) {
        this.logInfo(key, value);
        this.cacheProvider.add(key, value);
    }

    /**
     * <h3 class="en-US">Add a new key-value to cache server and set expire time</h3>
     * <h3 class="zh-CN">使用指定的过期时间添加缓存信息</h3>
     *
     * @param key    <span class="en-US">Cache key</span>
     *               <span class="zh-CN">缓存键值</span>
     * @param value  <span class="en-US">Cache value</span>
     *               <span class="zh-CN">缓存数据</span>
     * @param expire <span class="en-US">Expire time</span>
     *               <span class="zh-CN">过期时间</span>
     */
    public void add(final String key, final String value, final int expire) {
        this.logInfo(key, value);
        this.cacheProvider.add(key, value, expire);
    }

    /**
     * <h3 class="en-US">Replace exists value of given key by given value by default expire time</h3>
     * <h3 class="zh-CN">使用默认的过期时间替换已存在的缓存信息</h3>
     *
     * @param key   <span class="en-US">Cache key</span>
     *              <span class="zh-CN">缓存键值</span>
     * @param value <span class="en-US">Cache value</span>
     *              <span class="zh-CN">缓存数据</span>
     */
    public void replace(final String key, final String value) {
        this.logInfo(key, value);
        this.cacheProvider.replace(key, value);
    }

    /**
     * <h3 class="en-US">Replace exists value of given key by given value and set expire time</h3>
     * <h3 class="zh-CN">使用指定的过期时间替换已存在的缓存信息</h3>
     *
     * @param key    <span class="en-US">Cache key</span>
     *               <span class="zh-CN">缓存键值</span>
     * @param value  <span class="en-US">Cache value</span>
     *               <span class="zh-CN">缓存数据</span>
     * @param expire <span class="en-US">Expire time</span>
     *               <span class="zh-CN">过期时间</span>
     */
    public void replace(final String key, final String value, final int expire) {
        this.logInfo(key, value);
        this.cacheProvider.replace(key, value, expire);
    }

    /**
     * <h3 class="en-US">Set expire time to new given expire value which cache key was given</h3>
     * <h3 class="zh-CN">将指定的缓存键值过期时间设置为指定的新值</h3>
     *
     * @param key    <span class="en-US">Cache key</span>
     *               <span class="zh-CN">缓存键值</span>
     * @param expire <span class="en-US">New expire time</span>
     *               <span class="zh-CN">新的过期时间</span>
     */
    public void expire(final String key, final int expire) {
        this.cacheProvider.expire(key, expire);
    }

    /**
     * <h3 class="en-US">Execute touch operate which cache key was given</h3>
     * <h3 class="zh-CN">修改指定缓存键值的最后访问时间</h3>
     *
     * @param keys <span class="en-US">Cache keys array strings</span>
     *             <span class="zh-CN">缓存键值数组</span>
     */
    public void touch(final String... keys) {
        this.cacheProvider.touch(keys);
    }

    /**
     * <h3 class="en-US">Remove cache key-value from cache server</h3>
     * <h3 class="zh-CN">移除指定的缓存键值</h3>
     *
     * @param key <span class="en-US">Cache key</span>
     *            <span class="zh-CN">缓存键值</span>
     */
    public void delete(final String key) {
        this.cacheProvider.delete(key);
    }

    /**
     * <h3 class="en-US">Read cache value from cache key which cache key was given</h3>
     * <h3 class="zh-CN">读取指定缓存键值对应的缓存数据</h3>
     *
     * @param key <span class="en-US">Cache key</span>
     *            <span class="zh-CN">缓存键值</span>
     * @return <span class="en-US">Cache value or null if cache key was not exists or it was expired</span>
     * <span class="zh-CN">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
     */
    public String get(final String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return this.cacheProvider.get(key);
    }

    /**
     * <h3 class="en-US">Increment data by given cache key and value</h3>
     *
     * @param key  <span class="en-US">Cache key</span>
     *             <span class="zh-CN">缓存键值</span>
     * @param step <span class="en-US">Increment step value</span>
     *             <span class="zh-CN">自增步进值</span>
     * @return <span class="en-US">Operate result</span>
     * <span class="zh-CN">操作结果</span>
     */
    public long incr(final String key, final long step) {
        if (StringUtils.isEmpty(key)) {
            return Globals.DEFAULT_VALUE_LONG;
        }
        return this.cacheProvider.incr(key, step);
    }

    /**
     * <h3 class="en-US">Decrement data by given cache key and value</h3>
     *
     * @param key  <span class="en-US">Cache key</span>
     *             <span class="zh-CN">缓存键值</span>
     * @param step <span class="en-US">Decrement step value</span>
     *             <span class="zh-CN">自减步进值</span>
     * @return <span class="en-US">Operate result</span>
     * <span class="zh-CN">操作结果</span>
     */
    public long decr(final String key, final long step) {
        if (StringUtils.isEmpty(key)) {
            return Globals.DEFAULT_VALUE_LONG;
        }
        return this.cacheProvider.decr(key, step);
    }

    /**
     * <h3 class="en-US">Destroy agent instance</h3>
     * <h3 class="zh-CN">销毁缓存对象</h3>
     */
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
