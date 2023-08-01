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
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.utils.LoggerUtils;

import java.util.ServiceLoader;

/**
 * <h2 class="en">Cache utilities instance</h2>
 * <h2 class="zh-CN">缓存工具类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2022-11-18 17:21 $
 */
public final class CacheUtils {

    private final LoggerUtils.Logger logger = LoggerUtils.getLogger(CacheUtils.class);
    private static CacheUtils INSTANCE = null;
    /**
     * <span class="en">Cache manager instance</span>
     * <span class="zh-CN">缓存管理器实例</span>
     */
    private final CacheManager cacheManager;
    /**
     * <h3 class="en">Constructor for cache utilities</h3>
     * <h3 class="zh-CN">缓存工具类构建方法</h3>
     */
    private CacheUtils(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    /**
     * <h3 class="en">Retrieve singleton instance of CacheUtils</h3>
     * <h3 class="zh-CN">获取缓存工具类的单例实例</h3>
     *
     * @return <span class="en">Instance of cache utilities</span>
     * <span class="zh-CN">缓存工具类的单例实例</span>
     */
    public static CacheUtils getInstance() throws CacheException {
        if (CacheUtils.INSTANCE == null) {
            CacheUtils.initialize();
        }
        return CacheUtils.INSTANCE;
    }
    /**
     * <h3 class="en">Initialize cache utilities instance</h3>
     * <h3 class="zh-CN">初始化缓存工具类</h3>
     *
     * @throws CacheException <span class="en">Not found cache manager implements class</span>
     *                   <span class="zh-CN">未找到缓存管理器的实现类</span>
     */
    public static void initialize() throws CacheException {
        if (CacheUtils.INSTANCE == null) {
            CacheUtils.INSTANCE = new CacheUtils(ServiceLoader.load(CacheManager.class)
                    .findFirst()
                    .orElseThrow(() -> new CacheException(0x000C00000001L, "Not_Found_Cache_Manager_Impl")));
        }
    }
    /**
     * <h3 class="en">Register cache</h3>
     * <h3 class="zh-CN">注册缓存</h3>
     *
     * @param cacheName   <span class="en">Cache identify name</span>
     *                    <span class="zh-CN">缓存识别名称</span>
     * @param cacheConfig <span class="en">Cache configure information</span>
     *                    <span class="zh-CN">缓存配置信息</span>
     * @return <span class="en">Register result, Boolean.TRUE for register succeed, Boolean.FALSE for register failed</span>
     * <span class="zh-CN">注册结果，成功返回Boolean.TRUE，失败返回Boolean.FALSE</span>
     */
    public boolean register(final String cacheName, final CacheConfig cacheConfig) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Register_Cache_Debug", cacheName, cacheConfig);
        }
        return this.cacheManager.register(cacheName, cacheConfig);
    }
    /**
     * <h3 class="en">Check given cache name was registered</h3>
     * <h3 class="zh-CN">使用指定的缓存名称、配置信息注册缓存</h3>
     *
     * @param cacheName <span class="en">Cache identify name</span>
     *                  <span class="zh-CN">缓存识别名称</span>
     */
    public boolean registered(final String cacheName) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Check_Register_Cache_Debug", cacheName);
        }
        return this.cacheManager.registered(cacheName);
    }
    /**
     * <h3 class="en">Retrieve cache client by given cache name</h3>
     * <h3 class="zh-CN">根据给定的缓存识别名称获取缓存客户端</h3>
     *
     * @param cacheName <span class="en">Cache identify name</span>
     *                  <span class="zh-CN">缓存识别名称</span>
     * @return <span class="en">CacheClient instance or null if given cache name not registered</span>
     * <span class="zh-CN">缓存客户端实例，如果给定的缓存识别名称未找到，则返回null</span>
     */
    public CacheClient client(final String cacheName) {
        return this.cacheManager.client(cacheName);
    }
    /**
     * <h3 class="en">Deregister cache</h3>
     * <h3 class="zh-CN">取消注册缓存</h3>
     *
     * @param cacheName <span class="en">Cache identify name</span>
     *                  <span class="zh-CN">缓存识别名称</span>
     */
    public static void deregister(final String cacheName) {
        if (CacheUtils.INSTANCE == null) {
            return;
        }
        INSTANCE.cacheManager.deregister(cacheName);
    }
    /**
     * <h3 class="en">Destroy singleton instance</h3>
     * <h3 class="zh-CN">取消注册缓存</h3>
     */
    public static void destroy() {
        if (CacheUtils.INSTANCE != null) {
            CacheUtils.INSTANCE.cacheManager.destroy();
            CacheUtils.INSTANCE = null;
        }
    }
}
