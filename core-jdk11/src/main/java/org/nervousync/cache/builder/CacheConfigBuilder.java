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
package org.nervousync.cache.builder;

import org.nervousync.cache.config.CacheConfig;

/**
 * <h2 class="en">Cache configure builder</h2>
 * <h2 class="zh-CN">缓存配置构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2018-12-26 17:23 $
 */
public final class CacheConfigBuilder extends AbstractCacheConfigBuilder<CacheConfig> {

    /**
     * <h3 class="en">Constructor for cache configure builder</h3>
     * <h3 class="zh-CN">缓存配置构造器构建方法</h3>
     *
     * @param cacheConfig <span class="en">Current configure instance or null for generate new configure</span>
     *                    <span class="zh-CN">当前的缓存配置，如果传入null则生成一个新的配置</span>
     */
    private CacheConfigBuilder(final CacheConfig cacheConfig) {
        super(cacheConfig, cacheConfig);
    }

    /**
     * <h3 class="en">Static method for create new cache configure builder</h3>
     * <h3 class="zh-CN">静态方法用于创建新的缓存配置构造器</h3>
     */
    public static CacheConfigBuilder newBuilder() {
        return newBuilder(new CacheConfig());
    }

    /**
     * <h3 class="en">Static method for create cache configure builder</h3>
     * <h3 class="zh-CN">静态方法用于创建缓存配置构造器</h3>
     *
     * @param cacheConfig <span class="en">Current configure instance or null for generate new configure</span>
     *                    <span class="zh-CN">当前的缓存配置，如果传入null则生成一个新的配置</span>
     */
    public static CacheConfigBuilder newBuilder(final CacheConfig cacheConfig) {
        return new CacheConfigBuilder((cacheConfig == null) ? new CacheConfig() : cacheConfig);
    }

    /**
     * @see org.nervousync.builder.AbstractBuilder#build()
     */
    @Override
    protected void build() {
    }
}
