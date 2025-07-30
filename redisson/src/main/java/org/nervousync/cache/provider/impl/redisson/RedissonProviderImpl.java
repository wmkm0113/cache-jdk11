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

import jakarta.annotation.Nonnull;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.Globals;
import org.nervousync.utils.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;
import org.redisson.api.redisnode.RedisNode;
import org.redisson.api.redisnode.RedisNodes;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
	public boolean copy(@Nonnull final String source, @Nonnull final String destination) {
		return this.redissonClient.getBucket(source).copy(destination);
	}

	@Override
	public long del(@Nonnull final String... keys) {
		return this.redissonClient.getKeys().delete(keys);
	}

	@Override
	public long exists(@Nonnull final String... keys) {
		return this.redissonClient.getKeys().countExists(keys);
	}

	@Override
	public void destroy() {
		if (!this.redissonClient.isShutdown() && !this.redissonClient.isShuttingDown()) {
			this.redissonClient.shutdown();
		}
	}

	@Override
	public void expire(@Nonnull final String key, final int expiry) {
		this.redissonClient.getBucket(key).expire(Duration.ofSeconds(super.expiryTime(expiry)));
	}

	@Override
	public List<String> keys(@Nonnull final String pattern) {
		return StreamSupport.stream(
				this.redissonClient.getKeys().getKeys(KeysScanOptions.defaults().pattern(pattern)).spliterator(),
						Boolean.FALSE)
				.collect(Collectors.toList());
	}

	@Override
	public boolean persist(@Nonnull final String key) {
		return this.redissonClient.getBucket(key).clearExpire();
	}

	@Override
	public boolean rename(@Nonnull final String key, @Nonnull final String newKey) {
		this.redissonClient.getKeys().rename(key, newKey);
		return Boolean.TRUE;
	}

	@Override
	public long touch(@Nonnull final String... keys) {
		return this.redissonClient.getKeys().touch(keys);
	}

	@Override
	public long ttl(@Nonnull final String key) {
		return this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING)).remainTimeToLive();
	}

	@Override
	public boolean add(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		return this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING))
				.setIfAbsent(value, Duration.ofSeconds(super.expiryTime(expiry)));
	}

	@Override
	public long append(@Nonnull final String key, @Nonnull final String append) {
		try (OutputStream outputStream = this.redissonClient.getBinaryStream(key).getOutputStream()) {
			outputStream.write(append.getBytes(Globals.DEFAULT_ENCODING));
		} catch (IOException ignore) {
		}
		return this.strLen(key);
	}

	@Override
	public long decr(@Nonnull final String key, final long step) {
		return this.redissonClient.getAtomicLong(key).addAndGet(step * -1);
	}

	@Override
	public String get(@Nonnull final String key) {
		RBucket<String> bucket = this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING));
		return bucket.get();
	}

	@Override
	public String getDel(@Nonnull final String key) {
		RBucket<String> bucket = this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING));
		return bucket.getAndDelete();
	}

	@Override
	public String getEx(@Nonnull final String key, final int expiry) {
		RBucket<String> bucket = this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING));
		return bucket.getAndExpire(Duration.ofSeconds(super.expiryTime(expiry)));
	}

	@Override
	public String getRange(@Nonnull final String key, final int begin, final int end) {
		RBucket<String> bucket = this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING));
		return bucket.get().substring(begin, end);
	}

	@Override
	public String getSet(@Nonnull final String key, @Nonnull final String value) {
		RBucket<String> bucket = this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING));
		return bucket.getAndSet(value);
	}

	@Override
	public long incr(@Nonnull final String key, final long step) {
		return this.redissonClient.getAtomicLong(key).addAndGet(step);
	}

	@Override
	public double incrFloat(@Nonnull final String key, final double step) {
		return this.redissonClient.getAtomicDouble(key).addAndGet(step);
	}

	@Override
	public String lcs(@Nonnull final String key1, @Nonnull final String key2) {
		RBucket<String> bucket = this.redissonClient.getBucket(key1, new StringCodec(Globals.DEFAULT_ENCODING));
		return bucket.findCommon(key2);
	}

	@Override
	public long lcsLen(@Nonnull final String key1, @Nonnull final String key2) {
		RBucket<String> bucket = this.redissonClient.getBucket(key1, new StringCodec(Globals.DEFAULT_ENCODING));
		return bucket.findCommonLength(key2);
	}

	@Override
	public List<String> mget(@Nonnull final String... keys) {
		List<String> valueList = new ArrayList<>();
		this.redissonClient.getBuckets()
				.get(keys)
				.values()
				.stream()
				.filter(value -> value instanceof String)
				.forEach(value -> valueList.add((String) value));
		return valueList;
	}

	@Override
	public boolean mset(@Nonnull final String... keyvalues) {
		Map<String, String> dataMap = this.dataMap(keyvalues);
		if (dataMap.isEmpty()) {
			return Boolean.FALSE;
		}
		this.redissonClient.getBuckets().set(dataMap);
		return Boolean.TRUE;
	}

	@Override
	public boolean msetnx(@Nonnull final String... keyvalues) {
		Map<String, String> dataMap = this.dataMap(keyvalues);
		if (dataMap.isEmpty()) {
			return Boolean.FALSE;
		}
		return this.redissonClient.getBuckets().trySet(dataMap);
	}

	@Override
	public boolean replace(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		return this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING))
				.setIfExists(value, Duration.ofSeconds(super.expiryTime(expiry)));
	}

	@Override
	public boolean set(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING))
				.set(value, Duration.ofSeconds(super.expiryTime(expiry)));
		return Boolean.TRUE;
	}

	@Override
	public long setRange(@Nonnull final String key, final long offset, @Nonnull final String value) {
		try (SeekableByteChannel channel = this.redissonClient.getBinaryStream(key).getChannel()) {
			channel.position(offset).write(ByteBuffer.wrap(value.getBytes(Globals.DEFAULT_ENCODING)));
		} catch (IOException e) {
			return Globals.DEFAULT_VALUE_LONG;
		}
		return this.strLen(key);
	}

	@Override
	public long strLen(@Nonnull final String key) {
		return this.redissonClient.getBucket(key, new StringCodec(Globals.DEFAULT_ENCODING)).size();
	}

	@Override
	protected void info() {
		List<RedisNode> redisNodes = new ArrayList<>();
		switch (this.getClusterMode()) {
			case Sentinel:
				redisNodes.add(this.redissonClient.getRedisNodes(RedisNodes.SENTINEL_MASTER_SLAVE).getMaster());
				redisNodes.addAll(this.redissonClient.getRedisNodes(RedisNodes.SENTINEL_MASTER_SLAVE).getSlaves());
				break;
			case Master_Slave:
				redisNodes.add(this.redissonClient.getRedisNodes(RedisNodes.MASTER_SLAVE).getMaster());
				redisNodes.addAll(this.redissonClient.getRedisNodes(RedisNodes.MASTER_SLAVE).getSlaves());
				break;
			case Cluster:
				redisNodes.addAll(this.redissonClient.getRedisNodes(RedisNodes.CLUSTER).getMasters());
				redisNodes.addAll(this.redissonClient.getRedisNodes(RedisNodes.CLUSTER).getSlaves());
				break;
			default:
				redisNodes.add(this.redissonClient.getRedisNodes(RedisNodes.SINGLE).getInstance());
				break;
		}
		redisNodes.forEach(clusterNode -> this.logger.debug("Server_Info", clusterNode.info(RedisNode.InfoSection.ALL)));
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
