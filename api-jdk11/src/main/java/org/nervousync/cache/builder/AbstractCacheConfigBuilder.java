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
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.commons.Globals;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.security.factory.SecureConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * <h2 class="en">Abstract cache configure builder</h2>
 * <h2 class="zh-CN">缓存配置构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2023-03-14 09:18 $
 */
public abstract class AbstractCacheConfigBuilder<T> extends AbstractBuilder<T> {

    /**
     * <span class="en">Cache config instance</span>
     * <span class="zh-CN">缓存配置信息</span>
     */
    protected final CacheConfig cacheConfig;

    /**
     * <h3 class="en">Constructor for cache configure builder</h3>
     * <h3 class="zh-CN">缓存配置构造器构建方法</h3>
     *
     * @param parentBuilder <span class="en">Parent builder instance</span>
     *                      <span class="zh-CN">上级构建器实例</span>
     * @param cacheConfig   <span class="en">Current configure instance or null for generate new configure</span>
     *                      <span class="zh-CN">当前的缓存配置，如果传入null则生成一个新的配置</span>
     */
    protected AbstractCacheConfigBuilder(final T parentBuilder, final CacheConfig cacheConfig) {
        super(parentBuilder);
        this.cacheConfig = (cacheConfig == null) ? new CacheConfig() : cacheConfig;
    }

    /**
     * <h3 class="en">Configure cache provider</h3>
     * <h3 class="zh-CN">设置缓存适配器</h3>
     *
     * @param providerName <span class="en">Cache provider name</span>
     *                     <span class="zh-CN">缓存适配器名称</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> providerName(final String providerName) {
        if (StringUtils.notBlank(providerName)) {
            this.cacheConfig.setProviderName(providerName);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure secure name for protect password</h3>
     * <h3 class="zh-CN">设置用于保护密码的安全配置名称</h3>
     *
     * @param secureName <span class="en">Secure name</span>
     *                   <span class="zh-CN">安全配置名称</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> secureName(final String secureName) {
        if (SecureFactory.initialized()) {
            if (StringUtils.notBlank(this.cacheConfig.getSecureName())
                    || !Objects.equals(this.cacheConfig.getSecureName(), secureName)) {
                this.cacheConfig.setPassWord(
                        SecureFactory.update(this.cacheConfig.getPassWord(),
                                this.cacheConfig.getSecureName(), secureName));
            }
            if (this.cacheConfig.getSecureConfig() != null
                    && !Objects.equals(this.cacheConfig.getSecureName(), secureName)) {
                SecureFactory.deregister(this.cacheConfig.getSecureName());
            }
            this.cacheConfig.setSecureConfig(null);
            this.cacheConfig.setSecureName(StringUtils.notBlank(secureName) ? secureName : Globals.DEFAULT_VALUE_STRING);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure secure config information</h3>
     * <h3 class="zh-CN">设置用于保护密码的安全配置</h3>
     *
     * @param secureName   <span class="en">Secure name</span>
     *                     <span class="zh-CN">安全配置名称</span>
     * @param secureConfig <span class="en">Secure config information</span>
     *                     <span class="zh-CN">安全配置信息</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> secureConfig(final String secureName, final SecureConfig secureConfig) {
        if (SecureFactory.initialized() && StringUtils.notBlank(secureName) && secureConfig != null) {
            if (StringUtils.notBlank(this.cacheConfig.getPassWord())) {
                SecureFactory.register(Globals.DEFAULT_TEMPORARY_SECURE_NAME, secureConfig);
                String passWord = this.cacheConfig.getPassWord();
                if (StringUtils.notBlank(this.cacheConfig.getSecureName())) {
                    passWord = SecureFactory.update(passWord, this.cacheConfig.getSecureName(),
                            Globals.DEFAULT_TEMPORARY_SECURE_NAME);
                } else {
                    passWord = SecureFactory.encrypt(passWord, Globals.DEFAULT_TEMPORARY_SECURE_NAME);
                }
                this.cacheConfig.setPassWord(passWord);
                SecureFactory.deregister(Globals.DEFAULT_TEMPORARY_SECURE_NAME);
            }
            this.cacheConfig.setSecureName(secureName);
            this.cacheConfig.setSecureConfig(secureConfig);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure server connect timeout</h3>
     * <h3 class="zh-CN">设置缓存服务器的连接超时时间</h3>
     *
     * @param connectTimeout <span class="en">Connect timeout</span>
     *                       <span class="zh-CN">连接超时时间</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> connectTimeout(final int connectTimeout) {
        if (connectTimeout > 0) {
            this.cacheConfig.setConnectTimeout(connectTimeout);
        } else {
            this.cacheConfig.setConnectTimeout(CacheGlobals.DEFAULT_CONNECTION_TIMEOUT);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure default expire time, setting -1 for never expire</h3>
     * <h3 class="zh-CN">设置缓存的默认过期时间，设置为-1则永不过期</h3>
     *
     * @param expireTime <span class="en">Default expire time</span>
     *                   <span class="zh-CN">默认过期时间</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> expireTime(final int expireTime) {
        if (expireTime > 0) {
            this.cacheConfig.setExpireTime(expireTime);
        } else {
            this.cacheConfig.setExpireTime(CacheGlobals.DEFAULT_EXPIRE_TIME);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure connect client pool size</h3>
     * <h3 class="zh-CN">设置客户端连接池的大小</h3>
     *
     * @param clientPoolSize <span class="en">Client pool size</span>
     *                       <span class="zh-CN">连接池大小</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> clientPoolSize(final int clientPoolSize) {
        if (clientPoolSize > 0) {
            this.cacheConfig.setClientPoolSize(clientPoolSize);
        } else {
            this.cacheConfig.setClientPoolSize(CacheGlobals.DEFAULT_CLIENT_POOL_SIZE);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure limit size of generated client instance</h3>
     * <h3 class="zh-CN">设置允许创建的客户端实例阈值</h3>
     *
     * @param maximumClient <span class="en">Limit size of generated client instance</span>
     *                      <span class="zh-CN">客户端实例阈值</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> maximumClient(final int maximumClient) {
        if (maximumClient > 0) {
            this.cacheConfig.setMaximumClient(maximumClient);
        } else {
            this.cacheConfig.setMaximumClient(CacheGlobals.DEFAULT_MAXIMUM_CLIENT);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure connection timeout retry count</h3>
     * <h3 class="zh-CN">设置连接超时后的重试次数</h3>
     *
     * @param retryCount <span class="en">Connect retry count</span>
     *                   <span class="zh-CN">连接超时重试次数</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> retryCount(final int retryCount) {
        if (retryCount > 0) {
            this.cacheConfig.setRetryCount(retryCount);
        } else {
            this.cacheConfig.setRetryCount(CacheGlobals.DEFAULT_RETRY_COUNT);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure cache server authorization information</h3>
     * <h3 class="zh-CN">设置缓存服务器的用户名和密码</h3>
     *
     * @param userName <span class="en">Cache server username</span>
     *                 <span class="zh-CN">缓存服务器用户名</span>
     * @param passWord <span class="en">Cache server password</span>
     *                 <span class="zh-CN">缓存服务器密码</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> authorization(final String userName, final String passWord) {
        this.cacheConfig.setUserName(userName);
        if (StringUtils.notBlank(passWord)) {
            String encPassword;
            if (StringUtils.notBlank(this.cacheConfig.getSecureName())) {
                encPassword = SecureFactory.encrypt(this.cacheConfig.getSecureName(), passWord);
            } else {
                encPassword = passWord;
            }
            this.cacheConfig.setPassWord(encPassword);
        } else {
            this.cacheConfig.setPassWord(Globals.DEFAULT_VALUE_STRING);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure cache cluster mode</h3>
     * <h3 class="zh-CN">设置缓存服务器的集群类型</h3>
     *
     * @param clusterMode <span class="en">Cache Cluster Mode</span>
     *                    <span class="zh-CN">缓存集群类型</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     * @see ClusterMode
     */
    public final AbstractCacheConfigBuilder<T> clusterMode(final ClusterMode clusterMode) {
        this.cacheConfig.setClusterMode(clusterMode.toString());
        return this;
    }

    /**
     * <h3 class="en">Configure cache cluster mode</h3>
     * <h3 class="zh-CN">设置缓存服务器的集群类型</h3>
     *
     * @param masterName <span class="en">Master server name</span>
     *                   <span class="zh-CN">主服务器名称</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> masterName(final String masterName) {
        this.cacheConfig.setMasterName(masterName);
        return this;
    }

    /**
     * <h3 class="en">Configure cache server information</h3>
     * <h3 class="zh-CN">设置缓存服务器相关信息</h3>
     *
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final CacheServerConfigBuilder<T> serverBuilder() {
        return CacheServerConfigBuilder.newBuilder(this, new CacheConfig.ServerConfig());
    }

    /**
     * <h3 class="en">Configure cache server information</h3>
     * <h3 class="zh-CN">设置缓存服务器相关信息</h3>
     *
     * @param serverAddress <span class="en">Cache server address</span>
     *                      <span class="zh-CN">缓存服务器地址</span>
     * @param serverPort    <span class="en">Cache server port</span>
     *                      <span class="zh-CN">缓存服务器端口号</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final CacheServerConfigBuilder<T> serverBuilder(final String serverAddress, final int serverPort) {
        return CacheServerConfigBuilder.newBuilder(this,
                this.cacheConfig.getServerConfigList()
                        .stream()
                        .filter(existsConfig -> existsConfig.match(serverAddress, serverPort))
                        .findFirst()
                        .orElse(new CacheConfig.ServerConfig()));
    }

    /**
     * <h3 class="en">Remove cache server information</h3>
     * <h3 class="zh-CN">删除缓存服务器信息</h3>
     *
     * @param serverAddress <span class="en">Cache server address</span>
     *                      <span class="zh-CN">缓存服务器地址</span>
     * @param serverPort    <span class="en">Cache server port</span>
     *                      <span class="zh-CN">缓存服务器端口号</span>
     * @return <span class="en">Current cache configure builder</span>
     * <span class="zh-CN">当前缓存配置构建器</span>
     */
    public final AbstractCacheConfigBuilder<T> removeServer(final String serverAddress, final int serverPort) {
        List<CacheConfig.ServerConfig> serverConfigList = this.cacheConfig.getServerConfigList();
        if (serverConfigList.removeIf(serverConfig -> serverConfig.match(serverAddress, serverPort))) {
            this.cacheConfig.setServerConfigList(serverConfigList);
        }
        return this;
    }

    /**
     * <h3 class="en">Upsert cache server information</h3>
     * <h3 class="zh-CN">更新缓存服务器信息</h3>
     *
     * @param serverConfig <span class="en">Cache server configure information</span>
     *                     <span class="zh-CN">缓存服务器配置信息</span>
     * @throws BuilderException <span class="en">Throws if cache server address is empty</span>
     *                          <span class="zh-CN">如果缓存服务器地址未配置，则抛出异常</span>
     */
    final void serverConfig(final CacheConfig.ServerConfig serverConfig) throws BuilderException {
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
}
