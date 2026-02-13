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
import org.nervousync.builder.ParentBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.commons.Globals;
import org.nervousync.configs.ConfigureManager;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.core.DateTimeUtils;
import org.nervousync.utils.core.ObjectUtils;
import org.nervousync.utils.core.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <h2 class="en-US">Abstract cache configure builder</h2>
 * <h2 class="zh-CN">缓存配置构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 14, 2023 09:18:12 $
 */
public final class CacheConfigBuilder<P extends ParentBuilder> extends AbstractBuilder<P, CacheConfig> {

	/**
	 * <span class="en-US">Cache config instance</span>
	 * <span class="zh-CN">缓存配置信息</span>
	 */
	private final CacheConfig cacheConfig;
	/**
	 * <span class="en-US">Cache server list</span>
	 * <span class="en-US">缓存服务器列表</span>
	 */
	private final List<CacheConfig.ServerConfig> serverConfigList = new ArrayList<>();
	/**
	 * <h2 class="en-US">Configure the information modified flag</h2>
	 * <h2 class="zh-CN">配置信息修改标记</h2>
	 */
	private boolean modified = Boolean.FALSE;

	/**
	 * <h3 class="en-US">Constructor for cache configure builder</h3>
	 * <h3 class="zh-CN">缓存配置构造器构建方法</h3>
	 *
	 * @param parentBuilder <span class="en-US">Parent builder instance object</span>
	 *                      <span class="zh-CN">父构建器实例对象</span>
	 * @param cacheConfig   <span class="en-US">Current configure instance or null for generate new configure</span>
	 *                      <span class="zh-CN">当前的缓存配置，如果传入null则生成一个新的配置</span>
	 */
	private CacheConfigBuilder(final P parentBuilder, @Nonnull final CacheConfig cacheConfig) {
		super(parentBuilder);
		this.cacheConfig = cacheConfig;
	}

	/**
	 * <h3 class="en-US">Static method for create new cache configure builder</h3>
	 * <h3 class="zh-CN">静态方法用于创建新的缓存配置构造器</h3>
	 */
	public static <P extends ParentBuilder> CacheConfigBuilder<P> newBuilder() {
		return newBuilder(CacheGlobals.DEFAULT_CACHE_NAME);
	}

	/**
	 * <h3 class="en-US">Static method for create new cache configure builder</h3>
	 * <h3 class="zh-CN">静态方法用于创建新的缓存配置构造器</h3>
	 *
	 * @param cacheName <span class="en-US">Cache identifies name</span>
	 *                  <span class="zh-CN">缓存识别名称</span>
	 */
	public static <P extends ParentBuilder> CacheConfigBuilder<P> newBuilder(final String cacheName) {
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
		return newBuilder(cacheConfig);
	}

	/**
	 * <h3 class="en-US">Static method for create new cache configure builder</h3>
	 * <h3 class="zh-CN">静态方法用于创建新的缓存配置构造器</h3>
	 *
	 * @param cacheConfig <span class="en-US">Current configure instance or null for generate new configure</span>
	 *                    <span class="zh-CN">当前的缓存配置，如果传入null则生成一个新的配置</span>
	 */
	public static <P extends ParentBuilder> CacheConfigBuilder<P> newBuilder(final CacheConfig cacheConfig) {
		return newBuilder(null, cacheConfig);
	}

	/**
	 * <h3 class="en-US">Static method for creation cache configure builder</h3>
	 * <h3 class="zh-CN">静态方法用于创建缓存配置构造器</h3>
	 *
	 * @param cacheConfig <span class="en-US">Current configure instance or null for generate new configure</span>
	 *                    <span class="zh-CN">当前的缓存配置，如果传入null则生成一个新的配置</span>
	 */
	public static <P extends ParentBuilder> CacheConfigBuilder<P> newBuilder(final P parentBuilder, final CacheConfig cacheConfig) {
		return new CacheConfigBuilder<>(parentBuilder, (cacheConfig == null) ? new CacheConfig() : cacheConfig);
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
	public CacheConfigBuilder<P> providerName(final String providerName) {
		if (StringUtils.isEmpty(providerName)
				|| ObjectUtils.nullSafeEquals(this.cacheConfig.getProviderName(), providerName)) {
			return this;
		}
		this.cacheConfig.setProviderName(providerName);
		this.modified = Boolean.TRUE;
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
	public CacheConfigBuilder<P> connectTimeout(final int connectTimeout) {
		if (connectTimeout <= 0 || this.cacheConfig.getConnectTimeout() == connectTimeout) {
			return this;
		}
		this.cacheConfig.setConnectTimeout(connectTimeout);
		this.modified = Boolean.TRUE;
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
	public CacheConfigBuilder<P> expireTime(final int expireTime) {
		if (expireTime <= 0 || this.cacheConfig.getExpireTime() == expireTime) {
			return this;
		}
		this.cacheConfig.setExpireTime(expireTime);
		this.modified = Boolean.TRUE;
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
	public CacheConfigBuilder<P> clientPoolSize(final int clientPoolSize) {
		if (clientPoolSize <= 0 || this.cacheConfig.getClientPoolSize() == clientPoolSize) {
			return this;
		}
		this.cacheConfig.setClientPoolSize(clientPoolSize);
		this.modified = Boolean.TRUE;
		return this;
	}

	/**
	 * <h3 class="en-US">Configure the limit size of the generated client instance</h3>
	 * <h3 class="zh-CN">设置允许创建的客户端实例阈值</h3>
	 *
	 * @param maximumClient <span class="en-US">Limit size of generated client instance</span>
	 *                      <span class="zh-CN">客户端实例阈值</span>
	 * @return <span class="en-US">Current cache configure builder</span>
	 * <span class="zh-CN">当前缓存配置构建器</span>
	 */
	public CacheConfigBuilder<P> maximumClient(final int maximumClient) {
		if (maximumClient <= 0 || this.cacheConfig.getMaximumClient() == maximumClient) {
			return this;
		}
		this.cacheConfig.setMaximumClient(maximumClient);
		this.modified = Boolean.TRUE;
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
	public CacheConfigBuilder<P> retryCount(final int retryCount) {
		if (retryCount <= 0 || this.cacheConfig.getRetryCount() == retryCount) {
			return this;
		}
		this.cacheConfig.setRetryCount(retryCount);
		this.modified = Boolean.TRUE;
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
	public CacheConfigBuilder<P> authorization(final String userName, final String passWord) {
		if (StringUtils.notBlank(userName) && !ObjectUtils.nullSafeEquals(this.cacheConfig.getUserName(), userName)) {
			this.cacheConfig.setUserName(userName);
			this.modified = Boolean.TRUE;
		}
		if (!ObjectUtils.nullSafeEquals(this.cacheConfig.getPassWord(), passWord)) {
			this.cacheConfig.setPassWord(StringUtils.notBlank(passWord) ? passWord : Globals.DEFAULT_VALUE_STRING);
			this.modified = Boolean.TRUE;
		}
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
	public CacheConfigBuilder<P> clusterMode(final ClusterMode clusterMode) {
		if (ObjectUtils.nullSafeEquals(this.cacheConfig.getClusterMode(), clusterMode)) {
			return this;
		}
		this.cacheConfig.setClusterMode(clusterMode);
		this.modified = Boolean.TRUE;
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
	public CacheConfigBuilder<P> masterName(final String masterName) {
		if (ObjectUtils.nullSafeEquals(this.cacheConfig.getMasterName(), masterName)) {
			return this;
		}
		this.cacheConfig.setMasterName(masterName);
		this.modified = Boolean.TRUE;
		return this;
	}

	/**
	 * <h3 class="en-US">Configure cache server information</h3>
	 * <h3 class="zh-CN">设置缓存服务器相关信息</h3>
	 *
	 * @param serverAddress <span class="en-US">Cache server address</span>
	 *                      <span class="zh-CN">缓存服务器地址</span>
	 * @return <span class="en-US">Current cache configure builder</span>
	 * <span class="zh-CN">当前缓存配置构建器</span>
	 */
	public ServerConfigBuilder<CacheConfigBuilder<P>> serverBuilder(@Nonnull final String serverAddress) {
		return this.serverBuilder(serverAddress, Globals.DEFAULT_VALUE_INT);
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
	public ServerConfigBuilder<CacheConfigBuilder<P>> serverBuilder(@Nonnull final String serverAddress,
	                                                                final int serverPort) {
		return ServerConfigBuilder.newBuilder(this,
				this.cacheConfig.getServerConfigList()
						.stream()
						.filter(existsConfig -> existsConfig.match(serverAddress, serverPort))
						.findFirst()
						.orElse(CacheConfig.newInstance(serverAddress, serverPort)));
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
	public CacheConfigBuilder<P> removeServer(final String serverAddress, final int serverPort) {
		List<CacheConfig.ServerConfig> serverConfigList = this.cacheConfig.getServerConfigList();
		if (serverConfigList.removeIf(serverConfig -> serverConfig.match(serverAddress, serverPort))) {
			this.cacheConfig.setServerConfigList(serverConfigList);
		}
		return this;
	}

	@Override
	public CacheConfig build() {
		List<CacheConfig.ServerConfig> existConfigs = this.cacheConfig.getServerConfigList();
		boolean modified = Boolean.FALSE;
		if (existConfigs.size() != this.serverConfigList.size()) {
			modified = Boolean.TRUE;
		} else {
			for (CacheConfig.ServerConfig existConfig : existConfigs) {
				if (this.serverConfigList.stream().noneMatch(existConfig::match)) {
					modified = Boolean.TRUE;
					break;
				}
			}
			for (CacheConfig.ServerConfig serverConfig : this.serverConfigList) {
				if (this.serverConfigList.stream().noneMatch(serverConfig::match)) {
					modified = Boolean.TRUE;
					break;
				}
			}
		}
		if (modified) {
			this.cacheConfig.setServerConfigList(this.serverConfigList);
			this.modified = Boolean.TRUE;
		}
		if (this.modified) {
			this.cacheConfig.setLastModified(DateTimeUtils.currentUTCTimeMillis());
		}
		return this.cacheConfig;
	}

	@Override
	public void confirm(final Object object) throws BuilderException {
		if (!(object instanceof CacheConfig.ServerConfig)) {
			return;
		}
		CacheConfig.ServerConfig serverConfig = (CacheConfig.ServerConfig) object;
		if (StringUtils.isEmpty(serverConfig.getServerAddress())) {
			throw new BuilderException(0x000C00000002L);
		}
		if (this.serverConfigList.stream().anyMatch(existsConfig -> existsConfig.match(serverConfig))) {
			this.serverConfigList.replaceAll(existsConfig -> {
				if (existsConfig.match(serverConfig)
						&& existsConfig.getLastModified() != serverConfig.getLastModified()) {
					this.modified = Boolean.TRUE;
					return serverConfig;
				}
				return existsConfig;
			});
		} else {
			this.serverConfigList.add(serverConfig);
			this.modified = Boolean.TRUE;
		}
		this.cacheConfig.setServerConfigList(serverConfigList);
	}

	/**
	 * <h2 class="en-US">Cache server configure builder</h2>
	 * <h2 class="zh-CN">缓存服务器配置构建器</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
	 * @version $Revision: 1.0.0 $ $Date: 2023-03-14 09:33 $
	 */
	public static final class ServerConfigBuilder<P extends ParentBuilder> extends AbstractBuilder<P, CacheConfig.ServerConfig> {

		/**
		 * <span class="en-US">Cache server config instance</span>
		 * <span class="zh-CN">缓存服务器配置信息</span>
		 */
		private final CacheConfig.ServerConfig serverConfig;
		/**
		 * <h2 class="en-US">Configure the information modified flag</h2>
		 * <h2 class="zh-CN">配置信息修改标记</h2>
		 */
		private boolean modified = Boolean.FALSE;

		/**
		 * <h3 class="en-US">Constructor for cache server configure builder</h3>
		 * <h3 class="zh-CN">缓存服务器配置构造器构建方法</h3>
		 *
		 * @param parentBuilder <span class="en-US">Parent builder instance</span>
		 *                      <span class="zh-CN">上级构建器实例</span>
		 * @param serverConfig  <span class="en-US">Current server configure instance or null for generate new configure</span>
		 *                      <span class="zh-CN">当前的服务器缓存配置，如果传入null则生成一个新的配置</span>
		 */
		private ServerConfigBuilder(final P parentBuilder, final CacheConfig.ServerConfig serverConfig) {
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
		public static <P extends ParentBuilder> ServerConfigBuilder<P> newBuilder(final P parentBuilder,
		                                             final CacheConfig.ServerConfig serverConfig) {
			return new ServerConfigBuilder<>(parentBuilder, serverConfig);
		}

		/**
		 * <h3 class="en-US">Configure cache server port number</h3>
		 * <h3 class="zh-CN">配置缓存服务器端口号</h3>
		 *
		 * @param serverPort <span class="en-US">Server port</span>
		 *                   <span class="zh-CN">服务器端口号</span>
		 * @return <span class="en-US">Current cache server configure builder</span>
		 * <span class="zh-CN">当前缓存服务器配置构建器</span>
		 */
		public ServerConfigBuilder<P> port(final int serverPort) {
			if (this.serverConfig.getServerPort() != serverPort) {
				this.serverConfig.setServerPort(serverPort);
				this.modified = Boolean.TRUE;
			}
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
		public ServerConfigBuilder<P> weight(final int serverWeight) {
			if (this.serverConfig.getServerWeight() == serverWeight) {
				return this;
			}
			this.serverConfig.setServerWeight(serverWeight);
			this.modified = Boolean.TRUE;
			return this;
		}

		@Override
		public CacheConfig.ServerConfig build() {
			if (this.modified) {
				this.serverConfig.setLastModified(DateTimeUtils.currentUTCTimeMillis());
			}
			return this.serverConfig;
		}
	}
}
