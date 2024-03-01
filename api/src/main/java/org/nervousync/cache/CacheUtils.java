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
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * <h2 class="en-US">Cache utilities instance</h2>
 * <h2 class="zh-CN">缓存工具类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Nov 18, 2022 17:21:36 $
 */
public final class CacheUtils {

    private static final LoggerUtils.Logger LOGGER = LoggerUtils.getLogger(CacheUtils.class);
    private static CacheUtils INSTANCE = null;
    /**
     * <span class="en-US">Cache manager instance</span>
     * <span class="zh-CN">缓存管理器实例</span>
     */
    private final CacheManager cacheManager;

    /**
     * <h3 class="en-US">Constructor for cache utilities</h3>
     * <h3 class="zh-CN">缓存工具类构建方法</h3>
     */
    private CacheUtils(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        if (!this.register(CacheGlobals.DEFAULT_CACHE_NAME)) {
            LOGGER.warn("");
        }
    }

    /**
     * <h3 class="en-US">Retrieve singleton instance of CacheUtils</h3>
     * <h3 class="zh-CN">获取缓存工具类的单例实例</h3>
     *
     * @return <span class="en-US">Instance of cache utilities</span>
     * <span class="zh-CN">缓存工具类的单例实例</span>
     */
    public static CacheUtils getInstance() {
        if (CacheUtils.INSTANCE == null) {
            try {
                CacheUtils.initialize();
            } catch (CacheException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Stack_Message_Error", e);
                }
            }
        }
        return CacheUtils.INSTANCE;
    }

    /**
     * <h3 class="en-US">Initialize cache utilities instance</h3>
     * <h3 class="zh-CN">初始化缓存工具类</h3>
     *
     * @throws CacheException <span class="en-US">Not found cache manager implements class</span>
     *                        <span class="zh-CN">未找到缓存管理器的实现类</span>
     */
    public static void initialize() throws CacheException {
        if (CacheUtils.INSTANCE == null) {
            CacheUtils.INSTANCE = new CacheUtils(ServiceLoader.load(CacheManager.class)
                    .findFirst()
                    .orElseThrow(() -> new CacheException(0x000C00000001L)));
        }
    }

    /**
     * <h3 class="en-US">Register the cache with the specified cache identification code</h3>
     * <span class="en-US">
     *     Read the corresponding cache configuration information from the configuration information manager
     *     according to the specified cache identification code.
     * </span>
     * <h3 class="zh-CN">使用指定的缓存识别代码注册缓存</h3>
     * <span class="zh-CN">根据指定的缓存识别代码从配置信息管理器中读取相应的缓存配置信息</span>
     *
     * @param cacheName   <span class="en-US">Cache identify name</span>
     *                    <span class="zh-CN">缓存识别名称</span>
     * @return <span class="en-US">Register result, Boolean.TRUE for register succeed, Boolean.FALSE for register failed</span>
     * <span class="zh-CN">注册结果，成功返回Boolean.TRUE，失败返回Boolean.FALSE</span>
     */
    public boolean register(final String cacheName) {
        if (StringUtils.isEmpty(cacheName)) {
            return Boolean.FALSE;
        }
        return Optional.ofNullable(ConfigureManager.getInstance())
                .map(configureManager -> configureManager.readConfigure(CacheConfig.class, cacheName))
                .map(cacheConfig -> this.cacheManager.register(cacheName, cacheConfig))
                .orElse(Boolean.FALSE);
    }

    /**
     * <h3 class="en-US">Register cache</h3>
     * <h3 class="zh-CN">注册缓存</h3>
     *
     * @param cacheName   <span class="en-US">Cache identify name</span>
     *                    <span class="zh-CN">缓存识别名称</span>
     * @param cacheConfig <span class="en-US">Cache configure information</span>
     *                    <span class="zh-CN">缓存配置信息</span>
     * @return <span class="en-US">Register result, Boolean.TRUE for register succeed, Boolean.FALSE for register failed</span>
     * <span class="zh-CN">注册结果，成功返回Boolean.TRUE，失败返回Boolean.FALSE</span>
     */
    public boolean register(final String cacheName, final CacheConfig cacheConfig) {
        if (StringUtils.isEmpty(cacheName) || ObjectUtils.nullSafeEquals(CacheGlobals.DEFAULT_CACHE_NAME, cacheName)) {
            return Boolean.FALSE;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Register_Cache_Debug", cacheName, cacheConfig);
        }
        return this.cacheManager.register(cacheName, cacheConfig);
    }

    /**
     * <h3 class="en-US">Check given cache name was registered</h3>
     * <h3 class="zh-CN">使用指定的缓存名称、配置信息注册缓存</h3>
     *
     * @param cacheName <span class="en-US">Cache identify name</span>
     *                  <span class="zh-CN">缓存识别名称</span>
     */
    public boolean registered(final String cacheName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Check_Register_Cache_Debug", cacheName);
        }
        return this.cacheManager.registered(cacheName);
    }

    /**
     * <h3 class="en-US">Retrieve cache client by given cache name</h3>
     * <h3 class="zh-CN">根据给定的缓存识别名称获取缓存客户端</h3>
     *
     * @param cacheName <span class="en-US">Cache identify name</span>
     *                  <span class="zh-CN">缓存识别名称</span>
     * @return <span class="en-US">CacheClient instance or null if given cache name not registered</span>
     * <span class="zh-CN">缓存客户端实例，如果给定的缓存识别名称未找到，则返回null</span>
     */
    public CacheClient client(final String cacheName) {
        return this.cacheManager.client(cacheName);
    }

    /**
     * <h3 class="en-US">Deregister cache</h3>
     * <h3 class="zh-CN">取消注册缓存</h3>
     *
     * @param cacheName <span class="en-US">Cache identify name</span>
     *                  <span class="zh-CN">缓存识别名称</span>
     */
    public static void deregister(final String cacheName) {
        if (CacheUtils.INSTANCE == null) {
            return;
        }
        INSTANCE.cacheManager.deregister(cacheName);
    }

    /**
     * <h3 class="en-US">Destroy singleton instance</h3>
     * <h3 class="zh-CN">取消注册缓存</h3>
     */
    public static void destroy() {
        if (CacheUtils.INSTANCE != null) {
            CacheUtils.INSTANCE.cacheManager.destroy();
            CacheUtils.INSTANCE = null;
        }
    }
}
