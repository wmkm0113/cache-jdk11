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

import jakarta.annotation.Nonnull;
import org.nervousync.builder.AbstractBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.commons.Globals;
import org.nervousync.configs.ConfigureManager;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * <h2 class="en-US">Abstract cache configure builder</h2>
 * <h2 class="zh-CN">缓存配置构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2023-03-14 09:18 $
 */
public final class CacheConfigBuilder {

    private final String cacheName;
    /**
     * <span class="en-US">Cache config instance</span>
     * <span class="zh-CN">缓存配置信息</span>
     */
    private final CacheConfig cacheConfig;

    /**
     * <h3 class="en-US">Constructor for cache configure builder</h3>
     * <h3 class="zh-CN">缓存配置构造器构建方法</h3>
     *
     * @param cacheConfig <span class="en-US">Current configure instance or null for generate new configure</span>
     *                    <span class="zh-CN">当前的缓存配置，如果传入null则生成一个新的配置</span>
     */
    private CacheConfigBuilder(@Nonnull final String cacheName, @Nonnull final CacheConfig cacheConfig) {
        if (StringUtils.isEmpty(cacheName)) {
            this.cacheName = CacheGlobals.DEFAULT_CACHE_NAME;
        } else {
            this.cacheName = cacheName;
        }
        this.cacheConfig = cacheConfig;
    }

    /**
     * <h3 class="en-US">Static method for create new cache configure builder</h3>
     * <h3 class="zh-CN">静态方法用于创建新的缓存配置构造器</h3>
     */
    public static CacheConfigBuilder newBuilder() {
        return newBuilder(CacheGlobals.DEFAULT_CACHE_NAME);
    }

    /**
     * <h3 class="en-US">Static method for create cache configure builder</h3>
     * <h3 class="zh-CN">静态方法用于创建缓存配置构造器</h3>
     *
     * @param cacheName <span class="en-US">Cache identify name</span>
     *                  <span class="zh-CN">缓存识别名称</span>
     */
    public static CacheConfigBuilder newBuilder(final String cacheName) {
        CacheConfig cacheConfig =
                Optional.ofNullable(ConfigureManager.getInstance())
                        .map(configureManager -> {
                            if (StringUtils.isEmpty(cacheName)
                                    || ObjectUtils.nullSafeEquals(CacheGlobals.DEFAULT_CACHE_NAME, cacheName)) {
                                return configureManager.readConfigure(CacheConfig.class);
                            } else {
                                return configureManager.readConfigure(CacheConfig.class, cacheName);
                            }
                        })
                        .orElse(new CacheConfig());
        return new CacheConfigBuilder(cacheName, cacheConfig);
    }

    /**
     * <h3 class="en-US">Configure cache provider</h3>
     * <h3 class="zh-CN">设置缓存适配器</h3>
     *
     * @param providerName <span class="en-US">Cache provider name</span>
     *                     <span class="zh-CN">缓存适配器名称</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder providerName(final String providerName) {
        if (StringUtils.notBlank(providerName)) {
            this.cacheConfig.setProviderName(providerName);
        }
        return this;
    }

    /**
     * <h3 class="en-US">Configure server connect timeout</h3>
     * <h3 class="zh-CN">设置缓存服务器的连接超时时间</h3>
     *
     * @param connectTimeout <span class="en-US">Connect timeout</span>
     *                       <span class="zh-CN">连接超时时间</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder connectTimeout(final int connectTimeout) {
        if (connectTimeout > 0) {
            this.cacheConfig.setConnectTimeout(connectTimeout);
        } else {
            this.cacheConfig.setConnectTimeout(CacheGlobals.DEFAULT_CONNECTION_TIMEOUT);
        }
        return this;
    }

    /**
     * <h3 class="en-US">Configure default expire time, setting -1 for never expire</h3>
     * <h3 class="zh-CN">设置缓存的默认过期时间，设置为-1则永不过期</h3>
     *
     * @param expireTime <span class="en-US">Default expire time</span>
     *                   <span class="zh-CN">默认过期时间</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder expireTime(final int expireTime) {
        if (expireTime > 0) {
            this.cacheConfig.setExpireTime(expireTime);
        } else {
            this.cacheConfig.setExpireTime(CacheGlobals.DEFAULT_EXPIRE_TIME);
        }
        return this;
    }

    /**
     * <h3 class="en-US">Configure connect client pool size</h3>
     * <h3 class="zh-CN">设置客户端连接池的大小</h3>
     *
     * @param clientPoolSize <span class="en-US">Client pool size</span>
     *                       <span class="zh-CN">连接池大小</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder clientPoolSize(final int clientPoolSize) {
        if (clientPoolSize > 0) {
            this.cacheConfig.setClientPoolSize(clientPoolSize);
        } else {
            this.cacheConfig.setClientPoolSize(CacheGlobals.DEFAULT_CLIENT_POOL_SIZE);
        }
        return this;
    }

    /**
     * <h3 class="en-US">Configure limit size of generated client instance</h3>
     * <h3 class="zh-CN">设置允许创建的客户端实例阈值</h3>
     *
     * @param maximumClient <span class="en-US">Limit size of generated client instance</span>
     *                      <span class="zh-CN">客户端实例阈值</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder maximumClient(final int maximumClient) {
        if (maximumClient > 0) {
            this.cacheConfig.setMaximumClient(maximumClient);
        } else {
            this.cacheConfig.setMaximumClient(CacheGlobals.DEFAULT_MAXIMUM_CLIENT);
        }
        return this;
    }

    /**
     * <h3 class="en-US">Configure connection timeout retry count</h3>
     * <h3 class="zh-CN">设置连接超时后的重试次数</h3>
     *
     * @param retryCount <span class="en-US">Connect retry count</span>
     *                   <span class="zh-CN">连接超时重试次数</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder retryCount(final int retryCount) {
        if (retryCount > 0) {
            this.cacheConfig.setRetryCount(retryCount);
        } else {
            this.cacheConfig.setRetryCount(CacheGlobals.DEFAULT_RETRY_COUNT);
        }
        return this;
    }

    /**
     * <h3 class="en-US">Configure cache server authorization information</h3>
     * <h3 class="zh-CN">设置缓存服务器的用户名和密码</h3>
     *
     * @param userName <span class="en-US">Cache server username</span>
     *                 <span class="zh-CN">缓存服务器用户名</span>
     * @param passWord <span class="en-US">Cache server password</span>
     *                 <span class="zh-CN">缓存服务器密码</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder authorization(final String userName, final String passWord) {
        this.cacheConfig.setUserName(userName);
        this.cacheConfig.setPassWord(StringUtils.notBlank(passWord) ? passWord : Globals.DEFAULT_VALUE_STRING);
        return this;
    }

    /**
     * <h3 class="en-US">Configure cache cluster mode</h3>
     * <h3 class="zh-CN">设置缓存服务器的集群类型</h3>
     *
     * @param clusterMode <span class="en-US">Cache Cluster Mode</span>
     *                    <span class="zh-CN">缓存集群类型</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     * @see ClusterMode
     */
    public CacheConfigBuilder clusterMode(final ClusterMode clusterMode) {
        this.cacheConfig.setClusterMode(clusterMode.toString());
        return this;
    }

    /**
     * <h3 class="en-US">Configure cache cluster mode</h3>
     * <h3 class="zh-CN">设置缓存服务器的集群类型</h3>
     *
     * @param masterName <span class="en-US">Master server name</span>
     *                   <span class="zh-CN">主服务器名称</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder masterName(final String masterName) {
        this.cacheConfig.setMasterName(masterName);
        return this;
    }

    /**
     * <h3 class="en-US">Configure cache server information</h3>
     * <h3 class="zh-CN">设置缓存服务器相关信息</h3>
     *
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public ServerConfigBuilder serverBuilder() {
        return ServerConfigBuilder.newBuilder(this, new CacheConfig.ServerConfig());
    }

    /**
     * <h3 class="en-US">Configure cache server information</h3>
     * <h3 class="zh-CN">设置缓存服务器相关信息</h3>
     *
     * @param serverAddress <span class="en-US">Cache server address</span>
     *                      <span class="zh-CN">缓存服务器地址</span>
     * @param serverPort    <span class="en-US">Cache server port</span>
     *                      <span class="zh-CN">缓存服务器端口号</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public ServerConfigBuilder serverBuilder(final String serverAddress, final int serverPort) {
        return ServerConfigBuilder.newBuilder(this,
                this.cacheConfig.getServerConfigList()
                        .stream()
                        .filter(existsConfig -> existsConfig.match(serverAddress, serverPort))
                        .findFirst()
                        .orElse(new CacheConfig.ServerConfig()));
    }

    /**
     * <h3 class="en-US">Remove cache server information</h3>
     * <h3 class="zh-CN">删除缓存服务器信息</h3>
     *
     * @param serverAddress <span class="en-US">Cache server address</span>
     *                      <span class="zh-CN">缓存服务器地址</span>
     * @param serverPort    <span class="en-US">Cache server port</span>
     *                      <span class="zh-CN">缓存服务器端口号</span>
     * @return <span class="en-US">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder removeServer(final String serverAddress, final int serverPort) {
        List<CacheConfig.ServerConfig> serverConfigList = this.cacheConfig.getServerConfigList();
        if (serverConfigList.removeIf(serverConfig -> serverConfig.match(serverAddress, serverPort))) {
            this.cacheConfig.setServerConfigList(serverConfigList);
        }
        return this;
    }

    /**
     * <h3 class="en-US">Confirm configuration information and save</h3>
     * <h3 class="zh-CN">确认配置信息并保存</h3>
     *
     * @return <span class="en-US">Operate result</span>
     * <span class="zh-CN">操作结果</span>
     */
    public boolean confirm() {
        return Optional.ofNullable(ConfigureManager.getInstance())
                .map(configureManager -> {
                    if (ObjectUtils.nullSafeEquals(CacheGlobals.DEFAULT_CACHE_NAME, cacheName)) {
                        return configureManager.saveConfigure(this.cacheConfig);
                    } else {
                        return configureManager.saveConfigure(this.cacheConfig, this.cacheName);
                    }
                })
                .orElse(Boolean.FALSE);
    }

    /**
     * <h3 class="en-US">Upsert cache server information</h3>
     * <h3 class="zh-CN">更新缓存服务器信息</h3>
     *
     * @param serverConfig <span class="en-US">Cache server configure information</span>
     *                     <span class="zh-CN">缓存服务器配置信息</span>
     * @throws BuilderException <span class="en-US">Throws if cache server address is empty</span>
     *                          <span class="zh-CN">如果缓存服务器地址未配置，则抛出异常</span>
     */
    void serverConfig(final CacheConfig.ServerConfig serverConfig) throws BuilderException {
        if (serverConfig == null) {
            return;
        }
        if (StringUtils.isEmpty(serverConfig.getServerAddress())) {
            throw new BuilderException(0x000C00000002L, "Server_Address_Cache_Error");
        }
        List<CacheConfig.ServerConfig> serverConfigList = this.cacheConfig.getServerConfigList();
        if (serverConfigList.stream().anyMatch(existsConfig -> existsConfig.match(serverConfig))) {
            serverConfigList.replaceAll(existsConfig -> {
                if (existsConfig.match(serverConfig)) {
                    return serverConfig;
                }
                return existsConfig;
            });
        } else {
            serverConfigList.add(serverConfig);
        }
        this.cacheConfig.setServerConfigList(serverConfigList);
    }

    /**
     * <h2 class="en-US">Cache server configure builder</h2>
     * <h2 class="zh-CN">缓存服务器配置构建器</h2>
     *
     * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
     * @version $Revision: 1.0 $ $Date: 2023-03-14 09:33 $
     */
    public static final class ServerConfigBuilder extends AbstractBuilder<CacheConfigBuilder> {

        /**
         * <span class="en-US">Cache server config instance</span>
         * <span class="zh-CN">缓存服务器配置信息</span>
         */
        private final CacheConfig.ServerConfig serverConfig;

        /**
         * <h3 class="en-US">Constructor for cache server configure builder</h3>
         * <h3 class="zh-CN">缓存服务器配置构造器构建方法</h3>
         *
         * @param parentBuilder <span class="en-US">Parent builder instance</span>
         *                      <span class="zh-CN">上级构建器实例</span>
         * @param serverConfig  <span class="en-US">Current server configure instance or null for generate new configure</span>
         *                      <span class="zh-CN">当前的服务器缓存配置，如果传入null则生成一个新的配置</span>
         */
        private ServerConfigBuilder(CacheConfigBuilder parentBuilder,
                                    final CacheConfig.ServerConfig serverConfig) {
            super(parentBuilder);
            this.serverConfig = (serverConfig == null) ? new CacheConfig.ServerConfig() : serverConfig;
        }

        /**
         * <h3 class="en-US">Static method for create cache server configure builder</h3>
         * <h3 class="zh-CN">静态方法用于创建缓存服务器配置构造器</h3>
         *
         * @param parentBuilder <span class="en-US">Parent builder instance</span>
         *                      <span class="zh-CN">上级构建器实例</span>
         * @param serverConfig  <span class="en-US">Current server configure instance or null for generate new configure</span>
         *                      <span class="zh-CN">当前的服务器缓存配置，如果传入null则生成一个新的配置</span>
         */
        public static ServerConfigBuilder newBuilder(final CacheConfigBuilder parentBuilder,
                                                     final CacheConfig.ServerConfig serverConfig) {
            return new ServerConfigBuilder(parentBuilder, serverConfig);
        }

        /**
         * <h3 class="en-US">Configure cache server address by default port number</h3>
         * <h3 class="zh-CN">配置缓存服务器地址，使用默认端口号</h3>
         *
         * @param serverAddress <span class="en-US">Server address</span>
         *                      <span class="zh-CN">服务器地址</span>
         * @return <span class="en-US">Current cache server configure builder</span>
         * <span class="zh-CN">当前缓存服务器配置构建器</span>
         */
        public ServerConfigBuilder serverConfig(final String serverAddress) {
            return this.serverConfig(serverAddress, Globals.DEFAULT_VALUE_INT);
        }

        /**
         * <h3 class="en-US">Configure cache server address and port number</h3>
         * <h3 class="zh-CN">配置缓存服务器地址和端口号</h3>
         *
         * @param serverAddress <span class="en-US">Server address</span>
         *                      <span class="zh-CN">服务器地址</span>
         * @param serverPort    <span class="en-US">Server port</span>
         *                      <span class="zh-CN">服务器端口号</span>
         * @return <span class="en-US">Current cache server configure builder</span>
         * <span class="zh-CN">当前缓存服务器配置构建器</span>
         */
        public ServerConfigBuilder serverConfig(final String serverAddress, final int serverPort) {
            this.serverConfig.setServerAddress(serverAddress);
            this.serverConfig.setServerPort(serverPort);
            return this;
        }

        /**
         * <h3 class="en-US">Configure cache server weight</h3>
         * <h3 class="zh-CN">配置缓存服务器权重</h3>
         *
         * @param serverWeight <span class="en-US">Server weight</span>
         *                     <span class="zh-CN">服务器权重</span>
         * @return <span class="en-US">Current cache server configure builder</span>
         * <span class="zh-CN">当前缓存服务器配置构建器</span>
         */
        public ServerConfigBuilder serverWeight(final int serverWeight) {
            this.serverConfig.setServerWeight(serverWeight);
            return this;
        }

        /**
         * <h3 class="en-US">Confirm configure information</h3>
         * <h3 class="zh-CN">确认配置信息</h3>
         *
         * @throws BuilderException <span class="en-US">Throws if cache server address is empty</span>
         *                          <span class="zh-CN">如果缓存服务器地址未配置，则抛出异常</span>
         */
        @Override
        protected void build() throws BuilderException {
            this.parentBuilder.serverConfig(this.serverConfig);
        }
    }
}
