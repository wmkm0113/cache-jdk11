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
package org.nervousync.cache.provider.impl.xmemcached;

import jakarta.annotation.Nonnull;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig.ServerConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.Globals;
import org.nervousync.utils.core.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h2 class="en-US">Memcached cache provider using xmemcached</h2>
 * <h2 class="zh-CN">缓存客户端适配器，使用 Xmemcached 实现</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 23, 2020 13:43:19 $
 */
@Provider(name = "XMemcachedProvider", titleKey = "xmemcached.cache.provider.name")
public class XMemcachedProviderImpl extends AbstractProvider {

	/**
	 * <span class="en-US">Memcached client instance object</span>
	 * <span class="zh-CN">Memcached客户端实例对象</span>
	 */
	private MemcachedClient memcachedClient = null;
	private final List<InetSocketAddress> serverList = new ArrayList<>();

	@Override
	public int defaultPort() {
		return 11211;
	}

	@Override
	public boolean copy(@Nonnull final String source, @Nonnull final String destination) {
		String string = this.get(source);
		if (StringUtils.notBlank(string)) {
			this.del(source);
			return this.set(destination, string, Globals.DEFAULT_VALUE_INT);
		}
		return Boolean.FALSE;
	}

	@Override
	public long del(@Nonnull final String... keys) {
		long count = 0L;
		for (String key : keys) {
			try {
				if (this.memcachedClient.delete(key)) {
					count++;
				}
			} catch (InterruptedException | TimeoutException | MemcachedException e) {
				this.logger.error("Data_Operate_Cache_Error", e, "delete");
			}
		}
		return count;
	}

	@Override
	public long exists(@Nonnull final String... keys) {
		long count = 0L;
		for (String key : keys) {
			try {
				if (this.memcachedClient.get(key) != null) {
					count++;
				}
			} catch (InterruptedException | TimeoutException | MemcachedException e) {
				this.logger.error("Data_Operate_Cache_Error", e, "exists");
			}
		}
		return count;
	}

	@Override
	public void expire(@Nonnull final String key, final int expire) {
		try {
			this.memcachedClient.touch(key, super.expiryTime(expire));
		} catch (InterruptedException | TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", e, "expire");
		}
	}

	@Override
	public List<String> keys(@Nonnull final String pattern) {
		for (InetSocketAddress socketAddress : this.serverList) {
			try {
				return this.memcachedClient.stats(socketAddress)
						.keySet()
						.stream()
						.filter(key -> StringUtils.matches(key, pattern))
						.collect(Collectors.toList());
			} catch (InterruptedException | TimeoutException | MemcachedException e) {
				this.logger.error("Data_Operate_Cache_Error", e, "keys");
			}
		}
		return List.of();
	}

	@Override
	public boolean persist(@Nonnull final String key) {
		try {
			return this.memcachedClient.touch(key, super.expiryTime(Globals.DEFAULT_VALUE_INT));
		} catch (InterruptedException | TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", e, "persist");
		}
		return Boolean.FALSE;
	}

	@Override
	public boolean rename(@Nonnull final String key, @Nonnull final String newKey) {
		String string = this.get(key);
		if (StringUtils.notBlank(string)) {
			if (this.set(newKey, string, Globals.DEFAULT_VALUE_INT)) {
				this.del(key);
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	@Override
	public long touch(@Nonnull final String... keys) {
		long count = 0L;
		for (String key : keys) {
			try {
				if (this.memcachedClient.touch(key, super.expiryTime(Globals.DEFAULT_VALUE_INT))) {
					count++;
				}
			} catch (InterruptedException | TimeoutException | MemcachedException e) {
				this.logger.error("Data_Operate_Cache_Error", e, "touch");
			}
		}
		return count;
	}

	@Override
	public long ttl(@Nonnull final String key) {
		//  Memcached not support current operate
		return Globals.DEFAULT_VALUE_LONG;
	}

	@Override
	public boolean add(@Nonnull final String key, @Nonnull final String value, final int expire) {
		try {
			return this.memcachedClient.add(key, super.expiryTime(expire), value);
		} catch (InterruptedException | TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", e, "add");
		}
		return Boolean.FALSE;
	}

	@Override
	public long append(@Nonnull final String key, @Nonnull final String append) {
		String string = this.get(key) + append;
		this.set(key, string, Globals.DEFAULT_VALUE_INT);
		return string.length();
	}

	@Override
	public long decr(@Nonnull final String key, final long step) {
		try {
			return this.memcachedClient.decr(key, step);
		} catch (InterruptedException | TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", e, "decr");
		}
		return Globals.DEFAULT_VALUE_LONG;
	}

	@Override
	public String get(@Nonnull final String key) {
		try {
			return this.memcachedClient.get(key);
		} catch (InterruptedException | TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", e, "get");
		}
		return Globals.DEFAULT_VALUE_STRING;
	}

	@Override
	public String getDel(@Nonnull final String key) {
		String string = this.get(key);
		if (StringUtils.notBlank(string)) {
			this.del(key);
		}
		return string;
	}

	@Override
	public String getEx(@Nonnull final String key, final int expire) {
		String string = this.get(key);
		if (StringUtils.notBlank(string)) {
			this.expire(key, expire);
		}
		return string;
	}

	@Override
	public String getRange(@Nonnull final String key, final int begin, final int end) {
		String string = this.get(key);
		if (StringUtils.notBlank(string)) {
			return string.substring(Math.min(begin, string.length()), Math.min(end, string.length()));
		}
		return Globals.DEFAULT_VALUE_STRING;
	}

	@Override
	public String getSet(@Nonnull final String key, @Nonnull final String value) {
		String string = this.get(key);
		this.set(key, value, Globals.DEFAULT_VALUE_INT);
		return string;
	}

	@Override
	public long incr(@Nonnull final String key, final long step) {
		try {
			return this.memcachedClient.incr(key, step);
		} catch (InterruptedException | TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", e, "incr");
		}
		return Globals.DEFAULT_VALUE_LONG;
	}

	@Override
	public double incrFloat(@Nonnull final String key, final double step) {
		try {
			double current = Double.parseDouble(this.get(key)) - step;
			this.set(key, Double.toString(current), Globals.DEFAULT_VALUE_INT);
			return current;
		} catch (NumberFormatException e) {
			return Globals.DEFAULT_VALUE_DOUBLE;
		}
	}

	@Override
	public String lcs(@Nonnull final String key1, @Nonnull final String key2) {
		String value1 = this.get(key1);
		String value2 = this.get(key2);
		if (StringUtils.isEmpty(value1) || StringUtils.isEmpty(value2)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		StringBuilder commonsBuilder = new StringBuilder();
		for (int i = 0 ; i < value1.length() ; i++) {
			String common = value1.substring(i, i + 1);
			if (value2.contains(common)) {
				commonsBuilder.append(common);
			}
		}
		return commonsBuilder.toString();
	}

	@Override
	public long lcsLen(@Nonnull final String key1, @Nonnull final String key2) {
		return this.lcs(key1, key2).length();
	}

	@Override
	public List<String> mget(@Nonnull final String... keys) {
		return Stream.of(keys).map(this::get).collect(Collectors.toList());
	}

	@Override
	public boolean mset(@Nonnull final String... keyvalues) {
		return super.dataMap(keyvalues)
				.entrySet()
				.stream()
				.allMatch(entry ->
						this.set(entry.getKey(), entry.getValue(), Globals.DEFAULT_VALUE_INT));
	}

	@Override
	public boolean msetnx(@Nonnull final String... keyvalues) {
		Map<String, String> dataMap = super.dataMap(keyvalues);
		if (this.exists(dataMap.keySet().toArray(new String[0])) != dataMap.size()) {
			return Boolean.FALSE;
		}
		return dataMap.entrySet()
				.stream()
				.allMatch(entry ->
						this.set(entry.getKey(), entry.getValue(), Globals.DEFAULT_VALUE_INT));
	}

	@Override
	public boolean replace(@Nonnull final String key, @Nonnull final String value, final int expire) {
		try {
			return this.memcachedClient.replace(key, super.expiryTime(expire), value);
		} catch (InterruptedException | TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", e,"replace");
		}
		return Boolean.FALSE;
	}

	@Override
	public boolean set(@Nonnull final String key, @Nonnull final String value, final int expire) {
		try {
			return this.memcachedClient.set(key, super.expiryTime(expire), value);
		} catch (InterruptedException | TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", e, "set");
		}
		return Boolean.FALSE;
	}

	@Override
	public long setRange(@Nonnull final String key, final int offset, @Nonnull final String value) {
		String current = this.get(key);
		StringBuilder stringBuilder = new StringBuilder();
		if (offset >= 0 && offset < current.length()) {
			stringBuilder.append(current, 0, offset);
		} else {
			stringBuilder.append(current);
		}
		while (stringBuilder.length() < offset) {
			stringBuilder.append(" ");
		}
		stringBuilder.append(value);
		int position = Integer.max(Globals.INITIALIZE_INT_VALUE, offset) + value.length();
		if (position < current.length()) {
			stringBuilder.append(current, position, current.length());
		}
		this.set(key, stringBuilder.toString(), Globals.DEFAULT_VALUE_INT);
		return stringBuilder.length();
	}

	@Override
	public long strLen(@Nonnull final String key) {
		return this.get(key).length();
	}

	@Override
	public void destroy() {
		if (this.memcachedClient != null && !this.memcachedClient.isShutdown()) {
			try {
				this.memcachedClient.shutdown();
			} catch (final IOException e) {
				this.logger.error("Destroy_Memcached_Cache_Error");
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Stack_Message_Error", e);
				}
			}
		}
	}

	@Override
	protected void info() {
		try {
			this.memcachedClient.getStats().values().forEach(infoMap -> this.logger.debug("Server_Info", infoMap));
		} catch (InterruptedException | TimeoutException | MemcachedException ignored) {
		}

	}

	@Override
	protected void singletonMode(final ServerConfig serverConfig,
	                             final String userName, final String passWord) throws CacheException {
		this.initConnection(AddrUtil.getAddresses(this.serverAddress(serverConfig)),
				new int[]{CacheGlobals.DEFAULT_CACHE_SERVER_WEIGHT}, userName, passWord);
	}

	@Override
	protected void clusterMode(final List<ServerConfig> serverConfigList, final String masterName,
	                           final String userName, final String passWord) throws CacheException {
		final List<InetSocketAddress> serverList = new ArrayList<>();
		final List<Integer> weightList = new ArrayList<>();
		serverConfigList.forEach(serverConfig ->
				Optional.ofNullable(this.serverAddress(serverConfig))
						.ifPresent(serverAddress -> {
							serverList.add(AddrUtil.getOneAddress(serverAddress));
							weightList.add(serverConfig.getServerWeight());
						}));
		int[] serverWeights = new int[weightList.size()];
		for (int i = 0; i < weightList.size(); i++) {
			serverWeights[i] = weightList.get(i);
		}
		this.initConnection(serverList, serverWeights, userName, passWord);
	}

	/**
	 * <h3 class="en-US">Generate connect string by given server address and port number</h3>
	 * <h3 class="zhs">根据给定的服务器地址和端口号生成连接字符串</h3>
	 *
	 * @param serverConfig <span class="en-US">Cache server config information</span>
	 *                     <span class="zh-CN">缓存服务器配置信息</span>
	 * @return <span class="en-US">Connect string</span>
	 * <span class="zh-CN">连接字符串</span>
	 */
	private String serverAddress(final ServerConfig serverConfig) {
		if (serverConfig == null) {
			return null;
		}
		return serverConfig.getServerAddress() + ":" + super.serverPort(serverConfig.getServerPort());
	}

	/**
	 * <h3 class="en-US">Initialize connection</h3>
	 * <h3 class="zhs">初始化连接</h3>
	 *
	 * @param serverList    <span class="en-US">Server address list</span>
	 *                      <span class="zh-CN">服务器地址列表</span>
	 * @param serverWeights <span class="en-US">Server weights array</span>
	 *                      <span class="zh-CN">服务器权重数组</span>
	 * @param userName      <span class="en-US">Authenticate username</span>
	 *                      <span class="zh-CN">用于身份验证的用户名</span>
	 * @param passWord      <span class="en-US">Authenticate password</span>
	 *                      <span class="zh-CN">用于身份验证的密码</span>
	 * @throws CacheException <span class="en-US">An error occurred while connecting to the server</span>
	 *                        <span class="zh-CN">连接到服务器的过程中出错</span>
	 */
	private void initConnection(@Nonnull final List<InetSocketAddress> serverList, @Nonnull final int[] serverWeights,
	                            final String userName, final String passWord) throws CacheException {
		if (serverList.size() != serverWeights.length) {
			return;
		}
		XMemcachedClientBuilder clientBuilder = new XMemcachedClientBuilder(serverList, serverWeights);
		//  Using binary protocol instead of text protocol, if we use memcached 1.4.0 or later
		clientBuilder.setCommandFactory(new BinaryCommandFactory());

		//  Force do not resolve InetAddress to resolve can't found authorization information
		clientBuilder.doNotResolveInetAddresses();

		if (StringUtils.notBlank(userName) && StringUtils.notBlank(passWord)) {
			AuthInfo authInfo = AuthInfo.plain(userName, passWord);
			serverList.forEach(socketAddress -> clientBuilder.addAuthInfo(socketAddress, authInfo));
		}

		if (serverList.size() > 1) {
			//  Consistent Hash
			clientBuilder.setSessionLocator(new KetamaMemcachedSessionLocator());
			clientBuilder.setConnectionPoolSize(this.getClientPoolSize());
		}

		try {
			this.memcachedClient = clientBuilder.build();
		} catch (IOException e) {
			throw new CacheException(0x000C00000006L, e);
		}
		this.serverList.clear();
		this.serverList.addAll(serverList);
	}
}