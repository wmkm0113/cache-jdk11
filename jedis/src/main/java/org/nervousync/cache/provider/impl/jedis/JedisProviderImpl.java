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

import jakarta.annotation.Nonnull;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.cache.config.CacheConfig.ServerConfig;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.Globals;
import org.nervousync.utils.StringUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.LCSParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.util.Pool;

import java.time.Duration;
import java.util.*;

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
	public boolean copy(@Nonnull final String source, @Nonnull final String destination) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						boolean result = jedis.copy(source, destination, Boolean.FALSE);
						jedis.close();
						return result;
					})
					.orElse(Boolean.FALSE);
		} else {
			return this.writeCluster.copy(source, destination, Boolean.FALSE);
		}
	}

	@Override
	public long del(@Nonnull final String... keys) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long result = jedis.del(keys);
						jedis.close();
						return result;
					})
					.orElse(0L);
		} else {
			return this.writeCluster.del(keys);
		}
	}

	@Override
	public long exists(@Nonnull final String... keys) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long result = jedis.exists(keys);
						jedis.close();
						return result;
					})
					.orElse(0L);
		} else {
			return this.readCluster.exists(keys);
		}
	}

	@Override
	public boolean set(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						boolean result =
								"OK".equalsIgnoreCase(
										jedis.set(key, value, SetParams.setParams().ex(super.expiryTime(expiry))));
						jedis.close();
						return result;
					})
					.orElse(Boolean.FALSE);
		} else {
			return "OK".equalsIgnoreCase(this.writeCluster.set(key, value, SetParams.setParams().ex(expiry)));
		}
	}

	@Override
	public long setRange(@Nonnull final String key, final int offset, @Nonnull final String value) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long result = jedis.setrange(key, offset, value);
						jedis.close();
						return result;
					})
					.orElse(0L);
		} else {
			return this.writeCluster.setrange(key, offset, value);
		}
	}

	@Override
	public long strLen(@Nonnull final String key) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long result = jedis.strlen(key);
						jedis.close();
						return result;
					})
					.orElse(0L);
		} else {
			return this.readCluster.strlen(key);
		}
	}

	@Override
	public boolean add(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						boolean result =
								"OK".equalsIgnoreCase(
										jedis.set(key, value, SetParams.setParams().nx().ex(super.expiryTime(expiry))));
						jedis.close();
						return result;
					})
					.orElse(Boolean.FALSE);
		} else {
			return "OK".equalsIgnoreCase(this.writeCluster.set(key, value,
					SetParams.setParams().nx().ex(super.expiryTime(expiry))));
		}
	}

	@Override
	public long append(@Nonnull final String key, @Nonnull final String append) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long result = jedis.append(key, append);
						jedis.close();
						return result;
					})
					.orElse(0L);
		} else {
			return this.writeCluster.append(key, append);
		}
	}

	@Override
	public boolean replace(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						boolean result =
								"OK".equalsIgnoreCase(
										jedis.set(key, value, SetParams.setParams().xx().ex(super.expiryTime(expiry))));
						jedis.close();
						return result;
					})
					.orElse(Boolean.FALSE);
		} else {
			return "OK".equalsIgnoreCase(
					this.writeCluster.set(key, value, SetParams.setParams().xx().ex(super.expiryTime(expiry))));
		}
	}

	@Override
	public void expire(@Nonnull final String key, final int expiry) {
		if (this.singleMode) {
			Optional.ofNullable(this.singleClient())
					.ifPresent(jedis -> {
						jedis.expire(key, super.expiryTime(expiry));
						jedis.close();
					});
		} else {
			this.writeCluster.expire(key, super.expiryTime(expiry));
		}
	}

	@Override
	public List<String> keys(@Nonnull final String pattern) {
		final List<String> keyList = new ArrayList<>();
		if (this.singleMode) {
			Optional.ofNullable(this.singleClient())
					.ifPresent(jedis -> {
						keyList.addAll(jedis.keys(pattern));
						jedis.close();
					});
		} else {
			keyList.addAll(this.readCluster.keys(pattern));
		}
		return keyList;
	}

	@Override
	public boolean persist(@Nonnull final String key) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						boolean result = (jedis.persist(key) == 1L);
						jedis.close();
						return result;
					})
					.orElse(Boolean.FALSE);
		} else {
			return this.writeCluster.persist(key) == 1L;
		}
	}

	@Override
	public boolean rename(@Nonnull final String key, @Nonnull final String newKey) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						boolean result = "OK".equalsIgnoreCase(jedis.rename(key, newKey));
						jedis.close();
						return result;
					})
					.orElse(Boolean.FALSE);
		} else {
			return "OK".equalsIgnoreCase(this.writeCluster.rename(key, newKey));
		}
	}

	@Override
	public long touch(@Nonnull final String... keys) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long count = jedis.touch(keys);
						jedis.close();
						return count;
					})
					.orElse(0L);
		} else {
			return this.writeCluster.touch(keys);
		}
	}

	@Override
	public long ttl(@Nonnull final String key) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long count = jedis.ttl(key);
						jedis.close();
						return count;
					})
					.orElse(0L);
		} else {
			return this.readCluster.ttl(key);
		}
	}

	@Override
	public String get(@Nonnull final String key) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						String string = jedis.get(key);
						jedis.close();
						return string;
					})
					.orElse(Globals.DEFAULT_VALUE_STRING);
		} else {
			return this.readCluster.get(key);
		}
	}

	@Override
	public String getDel(@Nonnull final String key) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						String string = jedis.getDel(key);
						jedis.close();
						return string;
					})
					.orElse(Globals.DEFAULT_VALUE_STRING);
		} else {
			return this.writeCluster.getDel(key);
		}
	}

	@Override
	public String getEx(@Nonnull final String key, final int expiry) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						String string = jedis.getEx(key, GetExParams.getExParams().ex(super.expiryTime(expiry)));
						jedis.close();
						return string;
					})
					.orElse(Globals.DEFAULT_VALUE_STRING);
		} else {
			return this.writeCluster.getEx(key, GetExParams.getExParams().ex(super.expiryTime(expiry)));
		}
	}

	@Override
	public String getRange(@Nonnull final String key, final int begin, final int end) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						String string = jedis.getrange(key, begin, end);
						jedis.close();
						return string;
					})
					.orElse(Globals.DEFAULT_VALUE_STRING);
		} else {
			return this.readCluster.getrange(key, begin, end);
		}
	}

	@Override
	public String getSet(@Nonnull final String key, @Nonnull final String value) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						String string = jedis.setGet(key, value);
						jedis.close();
						return string;
					})
					.orElse(Globals.DEFAULT_VALUE_STRING);
		} else {
			return this.writeCluster.setGet(key, value);
		}
	}

	@Override
	public long incr(@Nonnull final String key, final long step) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long operateResult = jedis.incrBy(key, step);
						jedis.close();
						return operateResult;
					})
					.orElse(Globals.DEFAULT_VALUE_LONG);
		} else {
			return this.writeCluster.incrBy(key, step);
		}
	}

	@Override
	public double incrFloat(@Nonnull final String key, final double step) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						double count = jedis.incrByFloat(key, step);
						jedis.close();
						return count;
					})
					.orElse(0d);
		} else {
			return this.writeCluster.incrByFloat(key, step);
		}
	}

	@Override
	public String lcs(@Nonnull final String key1, @Nonnull final String key2) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						String string = jedis.lcs(key1, key2, LCSParams.LCSParams()).getMatchString();
						jedis.close();
						return string;
					})
					.orElse(Globals.DEFAULT_VALUE_STRING);
		} else {
			return this.readCluster.lcs(key1, key2, LCSParams.LCSParams()).getMatchString();
		}
	}

	@Override
	public long lcsLen(@Nonnull final String key1, @Nonnull final String key2) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long length = jedis.lcs(key1, key2, LCSParams.LCSParams()).getLen();
						jedis.close();
						return length;
					})
					.orElse(0L);
		} else {
			return this.readCluster.lcs(key1, key2, LCSParams.LCSParams()).getLen();
		}
	}

	@Override
	public List<String> mget(@Nonnull final String... keys) {
		final List<String> keyList = new ArrayList<>();
		if (this.singleMode) {
			Optional.ofNullable(this.singleClient())
					.ifPresent(jedis -> {
						keyList.addAll(jedis.mget(keys));
						jedis.close();
					});
		} else {
			keyList.addAll(this.readCluster.mget(keys));
		}
		return keyList;
	}

	@Override
	public boolean mset(@Nonnull final String... keyvalues) {
		if (keyvalues.length % 2 != 0) {
			return Boolean.FALSE;
		}
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						boolean result = "OK".equalsIgnoreCase(jedis.mset(keyvalues));
						jedis.close();
						return result;
					})
					.orElse(Boolean.FALSE);
		} else {
			return "OK".equalsIgnoreCase(this.writeCluster.mset(keyvalues));
		}
	}

	@Override
	public boolean msetnx(@Nonnull final String... keyvalues) {
		if (keyvalues.length % 2 != 0) {
			return Boolean.FALSE;
		}
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						boolean result = jedis.msetnx(keyvalues) == 1L;
						jedis.close();
						return result;
					})
					.orElse(Boolean.FALSE);
		} else {
			return this.writeCluster.msetnx(keyvalues) == 1L;
		}
	}

	@Override
	public long decr(@Nonnull final String key, long step) {
		if (this.singleMode) {
			return Optional.ofNullable(this.singleClient())
					.map(jedis -> {
						long operateResult = jedis.decrBy(key, step);
						jedis.close();
						return operateResult;
					})
					.orElse(Globals.DEFAULT_VALUE_LONG);
		} else {
			return this.readCluster.decrBy(key, step);
		}
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
	protected void info() {
		String string;
		if (this.singleMode) {
			string = Optional.ofNullable(this.singleClient())
					.map(Jedis::info)
					.orElse(Globals.DEFAULT_VALUE_STRING);
		} else {
			string = this.readCluster.info();
		}
		this.logger.debug("Server_Info", super.infoMap(string));
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

			Set<HostAndPort> sentinelServers = new HashSet<>();
			serverConfigList.forEach(serverConfig ->
					Optional.ofNullable(this.serverInfo(serverConfig)).ifPresent(sentinelServers::add));
			DefaultJedisClientConfig.Builder clientBuilder =
					DefaultJedisClientConfig.builder().connectionTimeoutMillis(connectTimeout);
			if (StringUtils.notBlank(passWord)) {
				clientBuilder.password(passWord);
				if (StringUtils.notBlank(userName)) {
					clientBuilder.clientName(userName);
				}
			}
			JedisClientConfig clientConfig = clientBuilder.build();
			this.jedisPool = new JedisSentinelPool(masterName, sentinelServers, jedisPoolConfig, clientConfig, clientConfig);
		} else {
			GenericObjectPoolConfig<Connection> clusterConfig = new GenericObjectPoolConfig<>();
			this.configPool(clusterConfig);
			HostAndPort masterServer = null;
			Set<HostAndPort> readServers = new HashSet<>();
			for (ServerConfig serverConfig : serverConfigList) {
				if (serverConfig == null) {
					continue;
				}
				if (serverConfig.getServerAddress().equalsIgnoreCase(masterName)) {
					masterServer = this.serverInfo(serverConfig);
				} else {
					readServers.add(this.serverInfo(serverConfig));
				}
			}
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

	/**
	 * <h3 class="en-US">Generate the HostAndPort instance object what will use at connecting to the Redis server</h3>
	 * <h3 class="zhs">生成连接使用的服务器信息实例对象</h3>
	 *
	 * @param serverConfig <span class="en-US">Cache server config information</span>
	 *                     <span class="zh-CN">缓存服务器配置信息</span>
	 * @return <span class="en-US">RedisURI instance object</span>
	 * <span class="zh-CN">RedisURI 实例对象</span>
	 */
	private HostAndPort serverInfo(final ServerConfig serverConfig) {
		if (serverConfig == null) {
			return null;
		}
		return new HostAndPort(serverConfig.getServerAddress(), super.serverPort(serverConfig.getServerPort()));
	}
}
