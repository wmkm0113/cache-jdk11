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
package org.nervousync.cache.provider.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Nonnull;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.CacheProvider;
import org.nervousync.utils.LoggerUtils;

import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.config.CacheConfig.ServerConfig;
import org.nervousync.commons.Globals;

/**
 * <h2 class="en-US">Abstract provider class, all providers must extend this class</h2>
 * <h2 class="zh-CN">缓存适配器抽象类，所有缓存适配器实现类必须继承本抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Apr 25, 2017 15:01:30 $
 */
public abstract class AbstractProvider implements CacheProvider {

	/**
	 * <span class="en-US">Multilingual supported logger instance</span>
	 * <span class="zh-CN">多语言支持的日志对象</span>
	 */
	protected final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

	/**
	 * <span class="en-US">Server connect timeout</span>
	 * <span class="zh-CN">缓存服务器的连接超时时间</span>
	 */
	private int connectTimeout = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Client pool size</span>
	 * <span class="zh-CN">连接池大小</span>
	 */
	private int clientPoolSize = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Connect retry count</span>
	 * <span class="zh-CN">连接超时重试次数</span>
	 */
	private int retryCount = CacheGlobals.DEFAULT_RETRY_COUNT;
	/**
	 * <span class="en-US">Limit size of generated client instance</span>
	 * <span class="zh-CN">客户端实例阈值</span>
	 */
	private int maximumClient = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Default expire time</span>
	 * <span class="zh-CN">默认过期时间</span>
	 */
	private int expireTime = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en-US">Cluster Mode</span>
	 * <span class="zh-CN">集群类型</span>
	 */
	private ClusterMode clusterMode = ClusterMode.Singleton;

	/**
	 * <h3 class="en-US">Initialize cache agent</h3>
	 * <h3 class="zh-CN">初始化缓存实例</h3>
	 *
	 * @param cacheConfig <span class="en-US">Cache config instance</span>
	 *                    <span class="zh-CN">缓存配置实例</span>
	 * @throws CacheException <span class="en-US">If initialize connection error</span>
	 *                        <span class="zh-CN">连接服务器失败时抛出异常</span>
	 */
	public void initialize(final CacheConfig cacheConfig) throws CacheException {
		this.connectTimeout = cacheConfig.getConnectTimeout();
		this.clientPoolSize = cacheConfig.getClientPoolSize();
		this.retryCount = cacheConfig.getRetryCount();
		this.maximumClient = cacheConfig.getMaximumClient();
		this.expireTime = cacheConfig.getExpireTime();
		this.clusterMode = cacheConfig.getClusterMode();
		List<ServerConfig> serverConfigList = cacheConfig.getServerConfigList();
		switch (serverConfigList.size()) {
			case 0:
				throw new CacheException(0x000C00000005L);
			case 1:
				this.singletonMode(serverConfigList.get(0), cacheConfig.getUserName(), cacheConfig.getPassWord());
				break;
			default:
				this.clusterMode(serverConfigList, cacheConfig.getMasterName(),
						cacheConfig.getUserName(), cacheConfig.getPassWord());
				break;
		}
		this.info();
	}

	/**
	 * <h3 class="en-US">Retrieve server connect timeout</h3>
	 * <h3 class="zh-CN">读取缓存服务器的连接超时时间</h3>
	 *
	 * @return <span class="en-US">Connect timeout</span>
	 * <span class="zh-CN">连接超时时间</span>
	 */
	public int getConnectTimeout() {
		return this.connectTimeout;
	}

	/**
	 * <h3 class="en-US">Retrieve connect client pool size</h3>
	 * <h3 class="zh-CN">读取客户端连接池的大小</h3>
	 *
	 * @return <span class="en-US">Client pool size</span>
	 * <span class="zh-CN">连接池大小</span>
	 */
	public int getClientPoolSize() {
		return this.clientPoolSize;
	}

	/**
	 * <h3 class="en-US">Retrieve server connect retry count</h3>
	 * <h3 class="zh-CN">读取缓存服务器的连接超时重试次数</h3>
	 *
	 * @return <span class="en-US">Connect retry count</span>
	 * <span class="zh-CN">连接超时重试次数</span>
	 */
	public int getRetryCount() {
		return this.retryCount;
	}

	/**
	 * <h3 class="en-US">Retrieve limit size of generated client instance</h3>
	 * <h3 class="zh-CN">读取允许创建的客户端实例阈值</h3>
	 *
	 * @return <span class="en-US">Limit size of generated client instance</span>
	 * <span class="zh-CN">客户端实例阈值</span>
	 */
	public int getMaximumClient() {
		return this.maximumClient;
	}

	/**
	 * <h3 class="en-US">Cache configure cluster mode</h3>
	 * <h3 class="zh-CN">缓存配置的集群类型</h3>
	 *
	 * @return <span class="en-US">Cluster mode</span>
	 * <span class="zh-CN">集群类型</span>
	 */
	protected ClusterMode getClusterMode() {
		return this.clusterMode;
	}

	/**
	 * <h3 class="en-US">Print server information</h3>
	 * <h3 class="zh-CN">打印服务器信息</h3>
	 */
	protected abstract void info();

	/**
	 * <h3 class="en-US">Initialize cache server connections</h3>
	 * <h3 class="zh-CN">初始化缓存服务器连接池</h3>
	 *
	 * @param serverConfig <span class="en-US">cache server configure</span>
	 *                     <span class="zh-CN">缓存服务器配置信息</span>
	 * @param userName     <span class="en-US">Authenticate username</span>
	 *                     <span class="zh-CN">身份认证用户名</span>
	 * @param passWord     <span class="en-US">Authenticate password</span>
	 *                     <span class="zh-CN">身份认证密码</span>
	 * @throws CacheException <span class="en-US">If initialize connection error</span>
	 *                        <span class="zh-CN">连接服务器失败时抛出异常</span>
	 */
	protected abstract void singletonMode(final ServerConfig serverConfig,
	                                      final String userName, final String passWord) throws CacheException;

	/**
	 * <h3 class="en-US">Initialize cache server connections</h3>
	 * <h3 class="zh-CN">初始化缓存服务器连接池</h3>
	 *
	 * @param serverConfigList <span class="en-US">cache server list</span>
	 *                         <span class="zh-CN">缓存服务器配置列表</span>
	 * @param masterName       <span class="en-US">Master name</span>
	 *                         <span class="zh-CN">主服务器名称</span>
	 * @param userName         <span class="en-US">Authenticate username</span>
	 *                         <span class="zh-CN">身份认证用户名</span>
	 * @param passWord         <span class="en-US">Authenticate password</span>
	 *                         <span class="zh-CN">身份认证密码</span>
	 * @throws CacheException <span class="en-US">If initialize connection error</span>
	 *                        <span class="zh-CN">连接服务器失败时抛出异常</span>
	 */
	protected abstract void clusterMode(final List<ServerConfig> serverConfigList, final String masterName,
	                                    final String userName, final String passWord) throws CacheException;

	/**
	 * <h3 class="en-US">Set expire time to new given expire value which cache key was given</h3>
	 * <h3 class="zh-CN">将指定的缓存键值过期时间设置为指定的新值</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @param expire <span class="en-US">New expire time</span>
	 *               <span class="zh-CN">新的过期时间</span>
	 */
	public abstract void expire(@Nonnull final String key, final int expire);

	/**
	 * <h3 class="en-US">Get the server port</h3>
	 * <h3 class="zh-CN">获取服务器端口号</h3>
	 *
	 * @param serverPort <span class="en-US">Server port</span>
	 *                   <span class="zh-CN">服务器端口号</span>
	 * @return <span class="en-US">Server port</span>
	 * <span class="zh-CN">服务器端口号</span>
	 */
	protected final int serverPort(final int serverPort) {
		return serverPort == Globals.DEFAULT_VALUE_INT ? this.defaultPort() : serverPort;
	}

	/**
	 * <h3 class="en-US">Get the expiry time</h3>
	 * <h3 class="zh-CN">获取过期时间</h3>
	 *
	 * @param expiry <span class="en-US">Expiry time</span>
	 *               <span class="zh-CN">过期时间</span>
	 * @return <span class="en-US">Expiry time</span>
	 * <span class="zh-CN">过期时间</span>
	 */
	protected final int expiryTime(final int expiry) {
		return (expiry == Globals.DEFAULT_VALUE_INT) ? this.expireTime : expiry;
	}

	/**
	 * <h3 class="en-US">Convert the key-value array to key-value mapping table</h3>
	 * <h3 class="zh-CN">转换键值对数组为键值对映射表</h3>
	 *
	 * @param keyvalues <span class="en-US">Key-value array</span>
	 *                  <span class="zh-CN">键值对数组</span>
	 * @return <span class="en-US">Key-value mapping table</span>
	 * <span class="zh-CN">键值对映射表</span>
	 */
	protected final Map<String, String> dataMap(@Nonnull final String... keyvalues) {
		if (keyvalues.length % 2 != 0) {
			return Map.of();
		}
		Map<String, String> dataMap = new HashMap<>();
		for (int i = 0; i < keyvalues.length; i += 2) {
			dataMap.put(keyvalues[i], keyvalues[i + 1]);
		}
		return dataMap;
	}
}
