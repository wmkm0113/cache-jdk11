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
import org.nervousync.annotations.provider.Provider;
import org.nervousync.cache.config.CacheConfig.ServerConfig;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.utils.core.StringUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.LCSParams;
import redis.clients.jedis.params.SetParams;

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
	 * <span class="en-US">Jedis write cluster instance object</span>
	 * <span class="zh-CN">Jedis写入集群实例对象</span>
	 */
	private UnifiedJedis writeNode = null;
	/**
	 * <span class="en-US">Jedis read cluster instance object</span>
	 * <span class="zh-CN">Jedis读取集群实例对象</span>
	 */
	private RedisClusterClient readCluster = null;

	@Override
	public int defaultPort() {
		return 6379;
	}

	@Override
	public boolean copy(@Nonnull final String source, @Nonnull final String destination) {
		return this.node(Boolean.TRUE).copy(source, destination, Boolean.FALSE);
	}

	@Override
	public long del(@Nonnull final String... keys) {
		return this.node(Boolean.TRUE).del(keys);
	}

	@Override
	public long exists(@Nonnull final String... keys) {
		return this.node(Boolean.FALSE).exists(keys);
	}

	@Override
	public boolean set(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		return "OK".equalsIgnoreCase(this.node(Boolean.TRUE).set(key, value, SetParams.setParams().ex(super.expiryTime(expiry))));
	}

	@Override
	public long setRange(@Nonnull final String key, final int offset, @Nonnull final String value) {
		return this.node(Boolean.TRUE).setrange(key, offset, value);
	}

	@Override
	public long strLen(@Nonnull final String key) {
		return this.node(Boolean.FALSE).strlen(key);
	}

	@Override
	public boolean add(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		return "OK".equalsIgnoreCase(this.node(Boolean.TRUE).set(key, value,
				SetParams.setParams().nx().ex(super.expiryTime(expiry))));
	}

	@Override
	public long append(@Nonnull final String key, @Nonnull final String append) {
		return this.node(Boolean.TRUE).append(key, append);
	}

	@Override
	public boolean replace(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		return "OK".equalsIgnoreCase(
				this.node(Boolean.TRUE).set(key, value, SetParams.setParams().xx().ex(super.expiryTime(expiry))));
	}

	@Override
	public void expire(@Nonnull final String key, final int expiry) {
		this.node(Boolean.TRUE).expire(key, super.expiryTime(expiry));
	}

	@Override
	public List<String> keys(@Nonnull final String pattern) {
		return new ArrayList<>(this.node(Boolean.FALSE).keys(pattern));
	}

	@Override
	public boolean persist(@Nonnull final String key) {
		return this.node(Boolean.TRUE).persist(key) == 1L;
	}

	@Override
	public boolean rename(@Nonnull final String key, @Nonnull final String newKey) {
		return "OK".equalsIgnoreCase(this.node(Boolean.TRUE).rename(key, newKey));
	}

	@Override
	public long touch(@Nonnull final String... keys) {
		return this.node(Boolean.TRUE).touch(keys);
	}

	@Override
	public long ttl(@Nonnull final String key) {
		return this.node(Boolean.FALSE).ttl(key);
	}

	@Override
	public String get(@Nonnull final String key) {
		return this.node(Boolean.FALSE).get(key);
	}

	@Override
	public String getDel(@Nonnull final String key) {
		return this.node(Boolean.TRUE).getDel(key);
	}

	@Override
	public String getEx(@Nonnull final String key, final int expiry) {
		return this.node(Boolean.TRUE).getEx(key, GetExParams.getExParams().ex(super.expiryTime(expiry)));
	}

	@Override
	public String getRange(@Nonnull final String key, final int begin, final int end) {
		return this.node(Boolean.FALSE).getrange(key, begin, end);
	}

	@Override
	public String getSet(@Nonnull final String key, @Nonnull final String value) {
		return this.node(Boolean.TRUE).setGet(key, value);
	}

	@Override
	public long incr(@Nonnull final String key, final long step) {
		return this.node(Boolean.TRUE).incrBy(key, step);
	}

	@Override
	public double incrFloat(@Nonnull final String key, final double step) {
		return this.node(Boolean.TRUE).incrByFloat(key, step);
	}

	@Override
	public String lcs(@Nonnull final String key1, @Nonnull final String key2) {
		return this.node(Boolean.FALSE).lcs(key1, key2, LCSParams.LCSParams()).getMatchString();
	}

	@Override
	public long lcsLen(@Nonnull final String key1, @Nonnull final String key2) {
		return this.node(Boolean.FALSE).lcs(key1, key2, LCSParams.LCSParams()).getLen();
	}

	@Override
	public List<String> mget(@Nonnull final String... keys) {
		return this.node(Boolean.FALSE).mget(keys);
	}

	@Override
	public boolean mset(@Nonnull final String... keyvalues) {
		if (keyvalues.length % 2 != 0) {
			return Boolean.FALSE;
		}
		return "OK".equalsIgnoreCase(this.node(Boolean.TRUE).mset(keyvalues));
	}

	@Override
	public boolean msetnx(@Nonnull final String... keyvalues) {
		if (keyvalues.length % 2 != 0) {
			return Boolean.FALSE;
		}
		return this.node(Boolean.TRUE).msetnx(keyvalues) == 1L;
	}

	@Override
	public long decr(@Nonnull final String key, long step) {
		return this.node(Boolean.FALSE).decrBy(key, step);
	}

	@Override
	public void destroy() {
		if (this.readCluster != null) {
			this.readCluster.close();
		}

		if (this.writeNode != null) {
			this.writeNode.close();
		}
	}

	@Override
	protected void info() {
		this.logger.debug("Server_Info", super.infoMap(this.node(Boolean.FALSE).info()));
	}

	@Override
	protected void singletonMode(final ServerConfig cachedServer, final String userName, final String passWord) {
		ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
		this.configPool(poolConfig);
		DefaultJedisClientConfig.Builder clientBuilder =
				DefaultJedisClientConfig.builder().connectionTimeoutMillis(this.getConnectTimeout() * 1000);
		if (StringUtils.notBlank(passWord)) {
			clientBuilder.password(passWord);
			if (StringUtils.notBlank(userName)) {
				clientBuilder.clientName(userName);
			}
		}
		this.writeNode = RedisClient.builder()
				.hostAndPort(cachedServer.getServerAddress(), super.serverPort(cachedServer.getServerPort()))
				.clientConfig(clientBuilder.build())
				.poolConfig(poolConfig)
				.build();
	}

	@Override
	protected void clusterMode(final List<ServerConfig> serverConfigList, final String masterName,
	                           final String userName, final String passWord) {
		DefaultJedisClientConfig.Builder clientBuilder =
				DefaultJedisClientConfig.builder().connectionTimeoutMillis(this.getConnectTimeout() * 1000);
		if (StringUtils.notBlank(passWord)) {
			clientBuilder.password(passWord);
			if (StringUtils.notBlank(userName)) {
				clientBuilder.clientName(userName);
			}
		}
		ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
		this.configPool(poolConfig);
		if (ClusterMode.Sentinel.equals(this.getClusterMode())) {
			Set<HostAndPort> sentinelServers = new HashSet<>();
			serverConfigList.forEach(serverConfig ->
					Optional.ofNullable(this.serverInfo(serverConfig)).ifPresent(sentinelServers::add));
			this.writeNode = RedisSentinelClient.builder().masterName(masterName)
					.sentinels(sentinelServers)
					.clientConfig(clientBuilder.build())
					.poolConfig(poolConfig)
					.build();
		} else {
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
			DefaultJedisClientConfig config = clientBuilder.build();
			RedisClusterClient.Builder readClientBuilder = RedisClusterClient.builder();
			if (this.getRetryCount() > 0) {
				readClientBuilder.maxAttempts(this.getRetryCount());
			}
			this.readCluster = readClientBuilder.nodes(readServers).clientConfig(config).poolConfig(poolConfig).build();
			this.writeNode = RedisClient.builder().hostAndPort(masterServer).clientConfig(config).poolConfig(poolConfig).build();
		}
	}

	private UnifiedJedis node(final boolean write) {
		if (write) {
			return this.writeNode;
		}
		return ClusterMode.Cluster.equals(this.getClusterMode()) ? this.readCluster : this.writeNode;
	}

	/**
	 * <h3 class="en-US">Configure connection pool information</h3>
	 * <h3 class="zh-CN">设置连接池配置信息</h3>
	 *
	 * @param poolConfig <span class="en-US">Jedis connection pool configure</span>
	 *                   <span class="zh-CN">Jedis连接池配置</span>
	 */
	private void configPool(final ConnectionPoolConfig poolConfig) {
		int connectTimeout = this.getConnectTimeout() * 1000;
		poolConfig.setMaxTotal(this.getMaximumClient());
		poolConfig.setMaxIdle(this.getClientPoolSize());
		poolConfig.setMaxWait(Duration.ofMillis(connectTimeout));
		poolConfig.setTestOnBorrow(Boolean.TRUE);
		poolConfig.setTestWhileIdle(Boolean.TRUE);
	}

	/**
	 * <h3 class="en-US">Generate the HostAndPort instance object which will use at connecting to the Redis server</h3>
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
