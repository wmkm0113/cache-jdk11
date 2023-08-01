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

import org.nervousync.builder.AbstractBuilder;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.commons.Globals;
import org.nervousync.exceptions.builder.BuilderException;

/**
 * <h2 class="en">Cache server configure builder</h2>
 * <h2 class="zh-CN">缓存服务器配置构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2023-03-14 09:33 $
 */
public final class CacheServerConfigBuilder<T> extends AbstractBuilder<AbstractCacheConfigBuilder<T>> {

    /**
     * <span class="en">Cache server config instance</span>
     * <span class="zh-CN">缓存服务器配置信息</span>
     */
    private final CacheConfig.ServerConfig serverConfig;

    /**
     * <h3 class="en">Constructor for cache server configure builder</h3>
     * <h3 class="zh-CN">缓存服务器配置构造器构建方法</h3>
     *
     * @param parentBuilder     <span class="en">Parent builder instance</span>
     *                          <span class="zh-CN">上级构建器实例</span>
     * @param serverConfig      <span class="en">Current server configure instance or null for generate new configure</span>
     *                          <span class="zh-CN">当前的服务器缓存配置，如果传入null则生成一个新的配置</span>
     */
    private CacheServerConfigBuilder(final AbstractCacheConfigBuilder<T> parentBuilder,
                                     final CacheConfig.ServerConfig serverConfig) {
        super(parentBuilder);
        this.serverConfig = (serverConfig == null) ? new CacheConfig.ServerConfig() : serverConfig;
    }

    /**
     * <h3 class="en">Static method for create cache server configure builder</h3>
     * <h3 class="zh-CN">静态方法用于创建缓存服务器配置构造器</h3>
     *
     * @param parentBuilder     <span class="en">Parent builder instance</span>
     *                          <span class="zh-CN">上级构建器实例</span>
     * @param serverConfig      <span class="en">Current server configure instance or null for generate new configure</span>
     *                          <span class="zh-CN">当前的服务器缓存配置，如果传入null则生成一个新的配置</span>
     */
    public static <T> CacheServerConfigBuilder<T> newBuilder(final AbstractCacheConfigBuilder<T> parentBuilder,
                                                             final CacheConfig.ServerConfig serverConfig) {
        return new CacheServerConfigBuilder<>(parentBuilder, serverConfig);
    }

    /**
     * <h3 class="en">Configure cache server address by default port number</h3>
     * <h3 class="zh-CN">配置缓存服务器地址，使用默认端口号</h3>
     *
     * @param serverAddress     <span class="en">Server address</span>
     *                          <span class="zh-CN">服务器地址</span>
     *
     * @return  <span class="en">Current cache server configure builder</span>
     *          <span class="zh-CN">当前缓存服务器配置构建器</span>
     */
    public CacheServerConfigBuilder<T> serverConfig(final String serverAddress) {
        return this.serverConfig(serverAddress, Globals.DEFAULT_VALUE_INT);
    }

    /**
     * <h3 class="en">Configure cache server address and port number</h3>
     * <h3 class="zh-CN">配置缓存服务器地址和端口号</h3>
     *
     * @param serverAddress     <span class="en">Server address</span>
     *                          <span class="zh-CN">服务器地址</span>
     * @param serverPort        <span class="en">Server port</span>
     *                          <span class="zh-CN">服务器端口号</span>
     *
     * @return  <span class="en">Current cache server configure builder</span>
     *          <span class="zh-CN">当前缓存服务器配置构建器</span>
     */
    public CacheServerConfigBuilder<T> serverConfig(final String serverAddress, final int serverPort) {
        this.serverConfig.setServerAddress(serverAddress);
        this.serverConfig.setServerPort(serverPort);
        return this;
    }

    /**
     * <h3 class="en">Configure cache server weight</h3>
     * <h3 class="zh-CN">配置缓存服务器权重</h3>
     *
     * @param serverWeight      <span class="en">Server weight</span>
     *                          <span class="zh-CN">服务器权重</span>
     *
     * @return  <span class="en">Current cache server configure builder</span>
     *          <span class="zh-CN">当前缓存服务器配置构建器</span>
     */
    public CacheServerConfigBuilder<T> serverWeight(final int serverWeight) {
        this.serverConfig.setServerWeight(serverWeight);
        return this;
    }

    /**
     * <h3 class="en">Confirm configure information</h3>
     * <h3 class="zh-CN">确认配置信息</h3>
     *
     * @throws BuilderException <span class="en">Throws if cache server address is empty</span>
     *                          <span class="zh-CN">如果缓存服务器地址未配置，则抛出异常</span>
     */
    @Override
    protected void build() throws BuilderException {
        this.parentBuilder.serverConfig(this.serverConfig);
    }
}
