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
package org.nervousync.cache.provider.impl.redisson;

import org.nervousync.annotations.provider.Provider;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.Globals;
import org.nervousync.utils.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * <h2 class="en-US">Redis cache provider using Redisson</h2>
 * <h2 class="zh-CN">缓存客户端适配器，使用 Redisson 实现</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 23, 2020 13:43:49 $
 */
@Provider(name = "RedissonProvider", titleKey = "redisson.cache.provider.name")
public final class RedissonProviderImpl extends AbstractProvider {

	/**
	 * <span class="en-US">Redisson client instance object</span>
	 * <span class="zh-CN">Redisson客户端实例对象</span>
	 */
	private RedissonClient redissonClient = null;

	@Override
	public int defaultPort() {
		return 6379;
	}

	@Override
	public void set(final String key, final String value, final int expire) {
		this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING))
				.set(value, Duration.ofSeconds(this.expiryTime(expire)));
	}

	@Override
	public void add(final String key, final String value, final int expire) {
		this.set(key, value, expire);
	}

	@Override
	public void replace(final String key, final String value, final int expire) {
		this.set(key, value, expire);
	}

	@Override
	public void touch(final String... keys) {
		Arrays.asList(keys)
				.forEach(key -> this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING)).touch());
	}

	@Override
	public void delete(final String key) {
		this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING)).delete();
	}

	@Override
	public String get(final String key) {
		return (String) this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING)).get();
	}

	@Override
	public long incr(final String key, final long step) {
		return this.redissonClient.getAtomicLong(key).addAndGet(step);
	}

	@Override
	public long decr(final String key, final long step) {
		return this.redissonClient.getAtomicLong(key).addAndGet(step * -1L);
	}

	@Override
	public void destroy() {
		if (!this.redissonClient.isShutdown() && !this.redissonClient.isShuttingDown()) {
			this.redissonClient.shutdown();
		}
	}

	@Override
	public void expire(final String key, final int expire) {
		this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING))
				.expire(Duration.ofSeconds(this.expiryTime(expire)));
	}

	@Override
	protected void singletonMode(final CacheConfig.ServerConfig serverConfig,
	                             final String userName, final String passWord) {
		Config config = new Config();
		SingleServerConfig singleConfig = config.useSingleServer()
				.setAddress(this.serverAddress(serverConfig.getServerAddress(), serverConfig.getServerPort()))
				.setConnectionMinimumIdleSize(this.getClientPoolSize())
				.setConnectTimeout(this.getConnectTimeout() * 1000)
				.setConnectionPoolSize(this.getClientPoolSize())
				.setDatabase(0);
		if (StringUtils.notBlank(passWord)) {
			singleConfig.setPassword(passWord);
			if (StringUtils.notBlank(userName)) {
				singleConfig.setUsername(userName);
			}
		}
		config.setTransportMode(TransportMode.NIO);
		this.redissonClient = Redisson.create(config);
	}

	@Override
	protected void clusterMode(final List<CacheConfig.ServerConfig> serverConfigList,
	                           final String masterName, final String userName, final String passWord) {
		Config config = new Config();
		switch (this.getClusterMode()) {
			case Sentinel:
				SentinelServersConfig sentinelConfig = config.useSentinelServers()
						.setMasterName(masterName)
						.setSentinelUsername(StringUtils.notBlank(userName) ? userName : null)
						.setSentinelPassword(StringUtils.notBlank(passWord) ? userName : null)
						.setConnectTimeout(this.getConnectTimeout() * 1000)
						.setRetryAttempts(this.getRetryCount())
						.setSlaveConnectionPoolSize(this.getClientPoolSize())
						.setMasterConnectionPoolSize(this.getClientPoolSize());
				serverConfigList.forEach(serverConfig ->
						sentinelConfig.addSentinelAddress(this.serverAddress(serverConfig.getServerAddress(),
								serverConfig.getServerPort())));
				break;
			case Master_Slave:
				MasterSlaveServersConfig masterSlaveConfig = config.useMasterSlaveServers()
						.setUsername(StringUtils.notBlank(userName) ? userName : null)
						.setPassword(StringUtils.notBlank(passWord) ? userName : null)
						.setConnectTimeout(this.getConnectTimeout() * 1000)
						.setRetryAttempts(this.getRetryCount())
						.setSlaveConnectionPoolSize(this.getClientPoolSize())
						.setMasterConnectionPoolSize(this.getClientPoolSize())
						.setReadMode(ReadMode.SLAVE);
				serverConfigList.forEach(serverConfig -> {
					if (serverConfig.getServerAddress().equalsIgnoreCase(masterName)) {
						masterSlaveConfig.setMasterAddress(this.serverAddress(serverConfig.getServerAddress(),
								serverConfig.getServerPort()));
					} else {
						masterSlaveConfig.addSlaveAddress(this.serverAddress(serverConfig.getServerAddress(),
								serverConfig.getServerPort()));
					}
				});
				break;
			default:
				ClusterServersConfig clusterConfig = config.useClusterServers()
						.setUsername(StringUtils.notBlank(userName) ? userName : null)
						.setPassword(StringUtils.notBlank(passWord) ? userName : null)
						.setConnectTimeout(this.getConnectTimeout() * 1000)
						.setRetryAttempts(this.getRetryCount())
						.setSlaveConnectionPoolSize(this.getClientPoolSize())
						.setMasterConnectionPoolSize(this.getClientPoolSize());
				serverConfigList.forEach(serverConfig ->
						clusterConfig.addNodeAddress(this.serverAddress(serverConfig.getServerAddress(),
								serverConfig.getServerPort())));
				break;
		}
		config.setTransportMode(TransportMode.NIO);
		this.redissonClient = Redisson.create(config);
	}

	/**
	 * <h3 class="en-US">Generate connect string by given server address and port number</h3>
	 * <h3 class="zhs">根据给定的服务器地址和端口号生成连接字符串</h3>
	 *
	 * @param serverAddress <span class="en-US">Server address</span>
	 *                      <span class="zh-CN">服务器地址</span>
	 * @param serverPort    <span class="en-US">Server port number</span>
	 *                      <span class="zh-CN">服务器端口号</span>
	 * @return <span class="en-US">Connect string</span>
	 * <span class="zh-CN">连接字符串</span>
	 */
	private String serverAddress(final String serverAddress, final int serverPort) {
		return "redis://" + serverAddress + ":" + this.serverPort(serverPort);
	}
}
