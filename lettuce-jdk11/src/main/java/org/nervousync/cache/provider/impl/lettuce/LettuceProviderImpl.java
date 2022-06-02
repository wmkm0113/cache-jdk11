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

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.nervousync.cache.annotation.CacheProvider;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.cache.config.CacheServer;
import org.nervousync.utils.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.Vector;

/**
 * The type Lettuce provider.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 8/25/2020 4:07 PM $
 */
@CacheProvider(name = "LettuceProvider", defaultPort = 6379)
public final class LettuceProviderImpl extends AbstractProvider {

	/**
	 * Is single server mode
	 */
	private boolean singleMode = Boolean.FALSE;

	private StatefulRedisClusterConnection<String, String> clusterConnection = null;
	private RedisAdvancedClusterCommands<String, String> clusterCommands = null;

	private StatefulRedisConnection<String, String> singleConnection = null;
	private RedisCommands<String, String> singleCommands = null;

	/**
	 * Instantiates a new Lettuce provider.
	 *
	 * @throws CacheException the cache exception
	 */
	public LettuceProviderImpl() throws CacheException {
		super();
	}

	@Override
	protected void initializeConnection(final List<CacheServer> serverConfigList,
	                                    final String userName, final String passWord) {
		if (serverConfigList.isEmpty()) {
			return;
		}
		this.singleMode = (serverConfigList.size() == 1);
		if (serverConfigList.size() > 1) {
			Vector<RedisURI> vector = new Vector<>(serverConfigList.size());
			serverConfigList.forEach(cacheServer -> {
				RedisURI.Builder builder = RedisURI.builder()
						.withHost(cacheServer.getServerAddress())
						.withPort(cacheServer.getServerPort())
						.withTimeout(Duration.ofMillis(this.getConnectTimeout() * 1000L));
				if (StringUtils.notBlank(passWord)) {
					if (StringUtils.isEmpty(userName)) {
						builder.withPassword(passWord.toCharArray());
					} else {
						builder.withAuthentication(userName, passWord.toCharArray());
					}
				}
				vector.add(builder.build());
			});
			RedisClusterClient clusterClient = RedisClusterClient.create(vector);
			this.clusterConnection = clusterClient.connect();
			this.clusterCommands = this.clusterConnection.sync();
		} else {
			CacheServer cacheServer = serverConfigList.get(0);
			RedisURI.Builder builder = RedisURI.builder()
					.withHost(cacheServer.getServerAddress())
					.withPort(cacheServer.getServerPort());
			if (StringUtils.notBlank(passWord)) {
				if (StringUtils.isEmpty(userName)) {
					builder.withPassword(passWord.toCharArray());
				} else {
					builder.withAuthentication(userName, passWord.toCharArray());
				}
			}
			RedisClient redisClient = RedisClient.create(builder.build());
			this.singleConnection = redisClient.connect();
			this.singleCommands = this.singleConnection.sync();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#set(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void set(String key, String value, int expiry) {
		this.process(key, value, expiry);
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#add(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void add(String key, String value, int expiry) {
		this.process(key, value, expiry);
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#replace(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void replace(String key, String value, int expiry) {
		this.process(key, value, expiry);
	}

	@Override
	public void expire(String key, int expire) {
		if (this.singleMode) {
			this.singleCommands.expire(key, expire);
		} else {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("TTL: {}", this.clusterCommands.ttl(key));
			}
			this.clusterCommands.expire(key, expire);
		}
	}

	@Override
	public void touch(String... keys) {
		if (this.singleMode) {
			this.singleCommands.touch(keys);
		} else {
			this.clusterCommands.touch(keys);
		}
	}

	@Override
	public void delete(String key) {
		if (this.singleMode) {
			this.singleCommands.del(key);
		} else {
			this.clusterCommands.del(key);
		}
	}

	@Override
	public String get(String key) {
		return this.singleMode ? this.singleCommands.get(key) : this.clusterCommands.get(key);
	}

	@Override
	public long incr(String key, long step) {
		long result;
		if (this.singleMode) {
			result = this.singleCommands.incrby(key, step);
		} else {
			result = this.clusterCommands.incrby(key, step);
		}
		return result;
	}

	@Override
	public long decr(String key, long step) {
		long result;
		if (this.singleMode) {
			result = this.singleCommands.decrby(key, step);
		} else {
			result = this.clusterCommands.decrby(key, step);
		}
		return result;
	}

	@Override
	public void destroy() {
		if (this.singleMode) {
			this.singleConnection.close();
		} else {
			this.clusterConnection.close();
		}
	}

	private void process(String key, String value, int expiry) {
		if (this.singleMode) {
			this.singleCommands.setex(key, expiry, value);
		} else {
			this.clusterCommands.setex(key, expiry, value);
		}
	}
}
