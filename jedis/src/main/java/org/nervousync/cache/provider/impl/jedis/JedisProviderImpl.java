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
package org.nervousync.cache.provider.impl.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.cache.config.CacheConfig.ServerConfig;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.Globals;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.StringUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.util.Pool;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <h2 class="en-US">Redis cache provider using Jedis</h2>
 * <h2 class="zh-CN">缓存客户端适配器，使用 Jedis 实现</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Apr 25, 2017 16:36:52 $
 */
@Provider(name = "JedisProvider", titleKey = "jedis.cache.provider.name")
public final class JedisProviderImpl extends AbstractProvider {

	/**
	 * <span class="en-US">Is single server mode flag</span>
	 * <span class="zh-CN">单一服务器标记</span>
	 */
	private boolean singleMode = Boolean.FALSE;
	/**
	 * <span class="en-US">Jedis client connection pool instance object</span>
	 * <span class="zh-CN">Jedis客户端连接池实例对象</span>
	 */
	private Pool<Jedis> jedisPool = null;
	/**
	 * <span class="en-US">Jedis write cluster instance object</span>
	 * <span class="zh-CN">Jedis写入集群实例对象</span>
	 */
	private JedisCluster writeCluster = null;
	/**
	 * <span class="en-US">Jedis read cluster instance object</span>
	 * <span class="zh-CN">Jedis读取集群实例对象</span>
	 */
	private JedisCluster readCluster = null;

	@Override
	public int defaultPort() {
		return 6379;
	}

	@Override
	public void set(String key, String value, int expiry) {
		this.process(key, value, expiry);
	}

	@Override
	public void add(String key, String value, int expiry) {
		this.process(key, value, expiry);
	}

	@Override
	public void replace(String key, String value, int expiry) {
		this.process(key, value, expiry);
	}

	@Override
	public void expire(String key, int expire) {
		if (this.singleMode) {
			Optional.ofNullable(this.singleClient())
					.ifPresent(jedis -> {
						jedis.expire(key, expire);
						jedis.close();
					});
		} else {
			this.writeCluster.expire(key, expire);
		}
	}

	@Override
	public void touch(String... keys) {
		if (this.singleMode) {
			Optional.ofNullable(this.singleClient())
					.ifPresent(jedis -> {
						jedis.touch(keys);
						jedis.close();
					});
		} else {
			this.writeCluster.touch(keys);
		}
	}

	@Override
	public void delete(String key) {
		if (this.singleMode) {
			Optional.ofNullable(this.singleClient())
					.ifPresent(jedis -> {
						jedis.del(key);
						jedis.close();
					});
		} else {
			this.writeCluster.del(key);
		}
	}

	@Override
	public String get(String key) {
		byte[] objectData;
		if (this.singleMode) {
			objectData = Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						byte[] readData = jedis.get(key.getBytes());
						jedis.close();
						return readData;
					})
					.orElse(null);
		} else {
			objectData = this.readCluster.get(key.getBytes());
		}
		return objectData == null ? Globals.DEFAULT_VALUE_STRING : ConvertUtils.toString(objectData);
	}

	@Override
	public long incr(String key, long step) {
		long result;
		if (this.singleMode) {
			result = Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long operateResult = jedis.incrBy(key, step);
						jedis.close();
						return operateResult;
					})
					.orElse(Globals.DEFAULT_VALUE_LONG);
		} else {
			result = this.readCluster.incrBy(key, step);
		}
		return result;
	}

	@Override
	public long decr(String key, long step) {
		long result;
		if (this.singleMode) {
			result = Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long operateResult = jedis.decrBy(key, step);
						jedis.close();
						return operateResult;
					})
					.orElse(Globals.DEFAULT_VALUE_LONG);
		} else {
			result = this.readCluster.decrBy(key, step);
		}
		return result;
	}

	@Override
	public void destroy() {
		if (this.jedisPool != null && !this.jedisPool.isClosed()) {
			this.jedisPool.close();
		}

		if (this.readCluster != null) {
			this.readCluster.close();
		}

		if (this.writeCluster != null) {
			this.writeCluster.close();
		}
	}

	@Override
	protected void singletonMode(final ServerConfig cachedServer, final String userName, final String passWord) {
		GenericObjectPoolConfig<Jedis> jedisPoolConfig = new GenericObjectPoolConfig<>();

		jedisPoolConfig.setMaxTotal(this.getMaximumClient());
		jedisPoolConfig.setMaxIdle(this.getClientPoolSize());
		jedisPoolConfig.setMaxWait(Duration.ofMillis(this.getConnectTimeout() * 1000L));
		jedisPoolConfig.setTestOnBorrow(Boolean.TRUE);
		jedisPoolConfig.setTestWhileIdle(Boolean.TRUE);

		int connectTimeout = this.getConnectTimeout() * 1000;

		if (StringUtils.isEmpty(passWord)) {
			this.jedisPool = new JedisPool(jedisPoolConfig, cachedServer.getServerAddress(),
					super.serverPort(cachedServer.getServerPort()), connectTimeout);
		} else {
			if (StringUtils.isEmpty(userName)) {
				this.jedisPool = new JedisPool(jedisPoolConfig, cachedServer.getServerAddress(),
						super.serverPort(cachedServer.getServerPort()), connectTimeout, passWord);
			} else {
				this.jedisPool = new JedisPool(jedisPoolConfig, cachedServer.getServerAddress(),
						super.serverPort(cachedServer.getServerPort()), connectTimeout, userName, passWord);
			}
		}
		this.singleMode = Boolean.TRUE;
	}

	@Override
	protected void clusterMode(final List<ServerConfig> serverConfigList, final String masterName,
	                           final String userName, final String passWord) {
		int connectTimeout = this.getConnectTimeout() * 1000;
		if (ClusterMode.Sentinel.equals(this.getClusterMode())) {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			this.configPool(jedisPoolConfig);

			Set<String> sentinelServers = new HashSet<>();
			serverConfigList.forEach(serverConfig ->
					sentinelServers.add(new HostAndPort(serverConfig.getServerAddress(),
							serverConfig.getServerPort()).toString()));
			if (StringUtils.isEmpty(passWord)) {
				this.jedisPool = new JedisSentinelPool(masterName, sentinelServers, jedisPoolConfig, connectTimeout);
			} else {
				if (StringUtils.isEmpty(userName)) {
					this.jedisPool = new JedisSentinelPool(masterName, sentinelServers, jedisPoolConfig,
							connectTimeout, userName, passWord, Globals.INITIALIZE_INT_VALUE);
				} else {
					this.jedisPool = new JedisSentinelPool(masterName, sentinelServers, jedisPoolConfig,
							connectTimeout, passWord);
				}
			}
		} else {
			GenericObjectPoolConfig<Connection> clusterConfig = new GenericObjectPoolConfig<>();
			this.configPool(clusterConfig);
			Set<HostAndPort> readServers = new HashSet<>();
			serverConfigList.stream()
					.filter(serverConfig -> !serverConfig.getServerAddress().equalsIgnoreCase(masterName))
					.forEach(serverConfig -> {
						HostAndPort server = new HostAndPort(serverConfig.getServerAddress(), serverConfig.getServerPort());
						readServers.add(server);
					});
			HostAndPort masterServer =
					serverConfigList.stream()
							.filter(serverConfig -> serverConfig.getServerAddress().equalsIgnoreCase(masterName))
							.findFirst()
							.map(serverConfig ->
									new HostAndPort(serverConfig.getServerAddress(), serverConfig.getServerPort()))
							.orElse(null);
			if (StringUtils.notBlank(passWord)) {
				DefaultJedisClientConfig.Builder clientBuilder =
						DefaultJedisClientConfig.builder().password(passWord)
								.connectionTimeoutMillis(connectTimeout);
				if (StringUtils.notBlank(userName)) {
					clientBuilder.clientName(userName);
				}
				this.readCluster =
						new JedisCluster(readServers, clientBuilder.build(), this.getRetryCount(), clusterConfig);
				this.writeCluster =
						new JedisCluster(masterServer, clientBuilder.build(), this.getRetryCount(), clusterConfig);
			} else {
				this.readCluster =
						new JedisCluster(readServers, connectTimeout, this.getRetryCount(), clusterConfig);
				this.writeCluster =
						new JedisCluster(masterServer, connectTimeout, this.getRetryCount(), clusterConfig);
			}
		}
		this.singleMode = Boolean.FALSE;
	}

	/**
	 * <h3 class="en-US">Get the client instance object of the single server</h3>
	 * <h3 class="zhs">获取单服务器的客户端实例对象</h3>
	 *
	 * @return <span class="en-US">Client instance object</span>
	 * <span class="zh-CN">客户端实例对象</span>
	 */
	private Jedis singleClient() {
		Jedis jedis = this.jedisPool.getResource();
		int retryCount = 0;
		while (jedis == null || !jedis.isConnected()) {
			if (retryCount >= this.getRetryCount()) {
				break;
			}
			retryCount++;
			jedis = this.jedisPool.getResource();
		}
		return jedis;
	}

	/**
	 * <h3 class="en-US">Process cache data</h3>
	 * <h3 class="zhs">处理缓存数据</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zhs">缓存键值</span>
	 * @param value  <span class="en-US">Cache value</span>
	 *               <span class="zhs">缓存数据</span>
	 * @param expiry <span class="en-US">Expire time</span>
	 *               <span class="zhs">过期时间</span>
	 */
	private void process(final String key, final String value, final int expiry) {
		if (this.singleMode) {
			Optional.ofNullable(this.singleClient())
					.ifPresent(jedis -> {
						jedis.setex(key.getBytes(), expiry, ConvertUtils.toByteArray(value));
						jedis.close();
					});
		} else {
			this.writeCluster.setex(key.getBytes(), expiry, ConvertUtils.toByteArray(value));
		}
	}

	/**
	 * <h3 class="en-US">Configure connection pool information</h3>
	 * <h3 class="zh-CN">设置连接池配置信息</h3>
	 *
	 * @param poolConfig <span class="en-US">Jedis connection pool configure</span>
	 *                   <span class="zh-CN">Jedis连接池配置</span>
	 */
	private void configPool(final GenericObjectPoolConfig<?> poolConfig) {
		int connectTimeout = this.getConnectTimeout() * 1000;
		poolConfig.setMaxTotal(this.getMaximumClient());
		poolConfig.setMaxIdle(this.getClientPoolSize());
		poolConfig.setMaxWait(Duration.ofMillis(connectTimeout));
		poolConfig.setTestOnBorrow(Boolean.TRUE);
		poolConfig.setTestWhileIdle(Boolean.TRUE);
	}
}
