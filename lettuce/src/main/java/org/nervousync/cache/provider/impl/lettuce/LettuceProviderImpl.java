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

package org.nervousync.cache.provider.impl.lettuce;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import jakarta.annotation.Nonnull;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.cache.config.CacheConfig.ServerConfig;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.Globals;
import org.nervousync.utils.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <h2 class="en-US">Redis cache provider using Lettuce</h2>
 * <h2 class="zh-CN">缓存客户端适配器，使用 Lettuce 实现</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Aug 25, 2020 16:07:35 $
 */
@Provider(name = "LettuceProvider", titleKey = "lettuce.cache.provider.name")
public final class LettuceProviderImpl extends AbstractProvider {

	/**
	 * <span class="en-US">Redis client instance object</span>
	 * <span class="zh-CN">Redis客户端实例对象</span>
	 */
	private AbstractRedisClient redisClient;

	/**
	 * <span class="en-US">Redis cluster connection instance object</span>
	 * <span class="zh-CN">Redis集群连接实例对象</span>
	 */
	private StatefulRedisClusterConnection<String, String> clusterConnection = null;
	/**
	 * <span class="en-US">Redis connection instance object</span>
	 * <span class="zh-CN">Redis连接实例对象</span>
	 */
	private StatefulRedisConnection<String, String> redisConnection = null;
	/**
	 * <span class="en-US">Redis command executor instance object</span>
	 * <span class="zh-CN">Redis命令执行器实例对象</span>
	 */
	private RedisClusterCommands<String, String> redisCommands = null;

	@Override
	public int defaultPort() {
		return 6379;
	}

	@Override
	public boolean copy(@Nonnull final String source, @Nonnull final String destination) {
		return this.redisCommands.copy(source, destination);
	}

	@Override
	public long del(@Nonnull final String... keys) {
		return this.redisCommands.del(keys);
	}

	@Override
	public long exists(@Nonnull final String... keys) {
		return this.redisCommands.exists(keys);
	}

	@Override
	public void expire(@Nonnull final String key, final int expiry) {
		this.redisCommands.expire(key, super.expiryTime(expiry));
	}

	@Override
	public List<String> keys(@Nonnull final String pattern) {
		return this.redisCommands.keys(pattern);
	}

	@Override
	public boolean persist(@Nonnull final String key) {
		return this.redisCommands.persist(key);
	}

	@Override
	public boolean rename(@Nonnull final String key, @Nonnull final String newKey) {
		return "OK".equalsIgnoreCase(this.redisCommands.rename(key, newKey));
	}

	@Override
	public long touch(@Nonnull final String... keys) {
		return this.redisCommands.touch(keys);
	}

	@Override
	public long ttl(@Nonnull final String key) {
		return this.redisCommands.ttl(key);
	}

	@Override
	public boolean add(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		return "OK".equalsIgnoreCase(this.redisCommands.set(key, value, SetArgs.Builder.nx().ex(super.expiryTime(expiry))));
	}

	@Override
	public long append(@Nonnull final String key, @Nonnull final String append) {
		return this.redisCommands.append(key, append);
	}

	@Override
	public long decr(@Nonnull final String key, final long step) {
		return this.redisCommands.decrby(key, step);
	}

	@Override
	public String get(@Nonnull final String key) {
		return this.redisCommands.get(key);
	}

	@Override
	public String getDel(@Nonnull final String key) {
		return this.redisCommands.getdel(key);
	}

	@Override
	public String getEx(@Nonnull final String key, final int expiry) {
		return this.redisCommands.getex(key, GetExArgs.Builder.ex(super.expiryTime(expiry)));
	}

	@Override
	public String getRange(@Nonnull final String key, final int begin, final int end) {
		return this.redisCommands.getrange(key, begin, end);
	}

	@Override
	public String getSet(@Nonnull final String key, @Nonnull final String value) {
		return this.redisCommands.getset(key, value);
	}

	@Override
	public long incr(@Nonnull final String key, final long step) {
		return this.redisCommands.incrby(key, step);
	}

	@Override
	public double incrFloat(@Nonnull final String key, final double step) {
		return this.redisCommands.incrbyfloat(key, step);
	}

	@Override
	public String lcs(@Nonnull final String key1, @Nonnull final String key2) {
		return this.redisCommands.lcs(LcsArgs.Builder.keys(key1, key2)).getMatchString();
	}

	@Override
	public long lcsLen(@Nonnull final String key1, @Nonnull final String key2) {
		return this.redisCommands.lcs(LcsArgs.Builder.keys(key1, key2)).getLen();
	}

	@Override
	public List<String> mget(@Nonnull final String... keys) {
		List<String> valueList = new ArrayList<>();
		this.redisCommands.mget(keys).forEach(keyValue -> valueList.add(keyValue.getValue()));
		return valueList;
	}

	@Override
	public boolean mset(@Nonnull final String... keyvalues) {
		Map<String, String> dataMap = this.dataMap(keyvalues);
		if (dataMap.isEmpty()) {
			return Boolean.FALSE;
		}
		return "OK".equalsIgnoreCase(this.redisCommands.mset(dataMap));
	}

	@Override
	public boolean msetnx(@Nonnull final String... keyvalues) {
		Map<String, String> dataMap = this.dataMap(keyvalues);
		if (dataMap.isEmpty()) {
			return Boolean.FALSE;
		}
		return this.redisCommands.msetnx(dataMap);
	}

	@Override
	public boolean replace(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		return "OK".equalsIgnoreCase(
				this.redisCommands.set(key, value,
						SetArgs.Builder.xx().ex(Duration.ofSeconds(super.expiryTime(expiry)))));
	}

	@Override
	public boolean set(@Nonnull final String key, @Nonnull final String value, final int expiry) {
		return "OK".equalsIgnoreCase(
				this.redisCommands.set(key, value, SetArgs.Builder.ex(Duration.ofSeconds(super.expiryTime(expiry)))));
	}

	@Override
	public long setRange(@Nonnull final String key, final int offset, @Nonnull final String value) {
		return this.redisCommands.setrange(key, offset, value);
	}

	@Override
	public long strLen(@Nonnull final String key) {
		return this.redisCommands.strlen(key);
	}

	@Override
	public void destroy() {
		if (this.redisConnection != null) {
			this.redisConnection.close();
			this.redisConnection = null;
		}
		if (this.clusterConnection != null) {
			this.clusterConnection.close();
			this.clusterConnection = null;
		}
		this.redisClient.close();
		this.redisClient.shutdown();
	}

	@Override
	protected void info() {
		this.logger.debug("Server_Info", super.infoMap(this.redisCommands.info()));
	}

	@Override
	protected void singletonMode(final ServerConfig serverConfig, final String userName, final String passWord) {
		this.redisClient = RedisClient.create(this.create(serverConfig, userName, passWord));
		this.redisConnection = ((RedisClient) this.redisClient).connect(StringCodec.UTF8);
		this.redisCommands = this.redisConnection.sync();
	}

	@Override
	protected void clusterMode(final List<ServerConfig> serverConfigList, final String masterName,
	                           final String userName, final String passWord) {
		if (serverConfigList.isEmpty()) {
			return;
		}
		if (serverConfigList.size() == 1) {
			this.singletonMode(serverConfigList.get(0), userName, passWord);
			return;
		}

		switch (this.getClusterMode()) {
			case Sentinel:
				RedisURI.Builder sentinelBuilder = RedisURI.builder()
						.withTimeout(Duration.ofMillis(this.getConnectTimeout() * 1000L))
						.withSentinelMasterId(masterName);
				serverConfigList.forEach(serverConfig ->
						sentinelBuilder.withSentinel(this.create(serverConfig, userName, passWord)));
				this.redisClient = RedisClient.create(sentinelBuilder.build());
				this.redisConnection = ((RedisClient) this.redisClient).connect(StringCodec.UTF8);
				this.redisCommands = this.redisConnection.sync();
				break;
			case Master_Slave:
				List<RedisURI> masterList = new ArrayList<>(serverConfigList.size());
				List<RedisURI> slaveList = new ArrayList<>(serverConfigList.size());
				serverConfigList.forEach(serverConfig -> {
					if (serverConfig.getServerAddress().equalsIgnoreCase(masterName)) {
						masterList.add(this.create(serverConfig, userName, passWord));
					} else {
						slaveList.add(this.create(serverConfig, userName, passWord));
					}
				});
				List<RedisURI> serverList = new ArrayList<>();
				serverList.addAll(masterList);
				serverList.addAll(slaveList);
				this.redisClient = RedisClient.create();
				this.redisConnection = MasterReplica.connect((RedisClient) this.redisClient, StringCodec.UTF8, serverList);
				((StatefulRedisMasterReplicaConnection<String, String>) this.redisConnection).setReadFrom(ReadFrom.REPLICA);
				this.redisCommands = this.redisConnection.sync();
				break;
			case Cluster:
				List<RedisURI> clusterList = new ArrayList<>(serverConfigList.size());
				serverConfigList.forEach(serverConfig -> clusterList.add(this.create(serverConfig, userName, passWord)));
				this.redisClient = RedisClusterClient.create(clusterList);
				((RedisClusterClient) this.redisClient)
						.setOptions(ClusterClientOptions.builder().autoReconnect(Boolean.TRUE).maxRedirects(1).build());
				this.clusterConnection = ((RedisClusterClient) this.redisClient).connect(StringCodec.UTF8);
				this.redisCommands = this.clusterConnection.sync();
				break;
		}
	}

	/**
	 * <h3 class="en-US">Generate the RedisURI instance object what will use at connecting to the Redis server</h3>
	 * <h3 class="zhs">生成连接使用的 RedisURI 实例对象</h3>
	 *
	 * @param serverConfig <span class="en-US">Cache server config information</span>
	 *                     <span class="zh-CN">缓存服务器配置信息</span>
	 * @param userName     <span class="en-US">Authenticate username</span>
	 *                     <span class="zh-CN">用于身份验证的用户名</span>
	 * @param passWord     <span class="en-US">Authenticate password</span>
	 *                     <span class="zh-CN">用于身份验证的密码</span>
	 * @return <span class="en-US">RedisURI instance object</span>
	 * <span class="zh-CN">RedisURI 实例对象</span>
	 */
	private RedisURI create(final ServerConfig serverConfig, final String userName, final String passWord) {
		RedisURI.Builder serverBuilder = RedisURI.builder()
				.withTimeout(Duration.ofMillis(this.getConnectTimeout() * 1000L))
				.withHost(serverConfig.getServerAddress())
				.withPort(super.serverPort(serverConfig.getServerPort()))
				.withLibraryName(Globals.DEFAULT_VALUE_STRING)
				.withLibraryVersion(Globals.DEFAULT_VALUE_STRING);
		if (StringUtils.notBlank(passWord)) {
			if (StringUtils.isEmpty(userName)) {
				serverBuilder.withPassword(passWord.toCharArray());
			} else {
				serverBuilder.withAuthentication(userName, passWord.toCharArray());
			}
		}
		return serverBuilder.build();
	}
}
