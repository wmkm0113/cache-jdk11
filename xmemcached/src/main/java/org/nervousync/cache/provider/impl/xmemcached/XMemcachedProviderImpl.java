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
import org.nervousync.utils.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeoutException;

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

	@Override
	public int defaultPort() {
		return 11211;
	}

	@Override
	public void set(final String key, final String value, final int expire) {
		try {
			this.memcachedClient.set(key, super.expiryTime(expire), value);
		} catch (InterruptedException e) {
			this.logger.error("Data_Operate_Cache_Error", "set");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", "set");
			this.printStackMessage(e);
		}
	}

	@Override
	public void add(final String key, final String value, final int expire) {
		try {
			this.memcachedClient.add(key, super.expiryTime(expire), value);
		} catch (InterruptedException e) {
			this.logger.error("Data_Operate_Cache_Error", "add");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", "add");
			this.printStackMessage(e);
		}
	}

	@Override
	public void replace(final String key, final String value, final int expire) {
		try {
			this.memcachedClient.replace(key, super.expiryTime(expire), value);
		} catch (InterruptedException e) {
			this.logger.error("Data_Operate_Cache_Error", "replace");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", "replace");
			this.printStackMessage(e);
		}
	}

	@Override
	public void expire(final String key, final int expire) {
		try {
			this.memcachedClient.touch(key, super.expiryTime(expire));
		} catch (InterruptedException e) {
			this.logger.error("Data_Operate_Cache_Error", "expire");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", "expire");
			this.printStackMessage(e);
		}
	}

	@Override
	public void touch(final String... keys) {
		try {
			for (String key : keys) {
				this.memcachedClient.touch(key, super.expiryTime(Globals.DEFAULT_VALUE_INT));
			}
		} catch (InterruptedException e) {
			this.logger.error("Data_Operate_Cache_Error", "touch");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", "touch");
			this.printStackMessage(e);
		}
	}

	@Override
	public void delete(final String key) {
		try {
			this.memcachedClient.delete(key);
		} catch (InterruptedException e) {
			this.logger.error("Data_Operate_Cache_Error", "delete");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", "delete");
			this.printStackMessage(e);
		}
	}

	@Override
	public String get(final String key) {
		try {
			return this.memcachedClient.get(key);
		} catch (InterruptedException e) {
			this.logger.error("Data_Operate_Cache_Error", "get");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", "get");
			this.printStackMessage(e);
		}
		return null;
	}

	@Override
	public long incr(final String key, final long step) {
		try {
			return this.memcachedClient.incr(key, step);
		} catch (InterruptedException e) {
			this.logger.error("Data_Operate_Cache_Error", "incr");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", "incr");
			this.printStackMessage(e);
		}
		return Globals.DEFAULT_VALUE_LONG;
	}

	@Override
	public long decr(final String key, final long step) {
		try {
			return this.memcachedClient.decr(key, step);
		} catch (InterruptedException e) {
			this.logger.error("Data_Operate_Cache_Error", "decr");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Data_Operate_Cache_Error", "decr");
			this.printStackMessage(e);
		}
		return Globals.DEFAULT_VALUE_LONG;
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
	 * <h3 class="en-US">Print error stack message</h3>
	 * <h3 class="zhs">打印异常信息</h3>
	 *
	 * @param e <span class="en-US">Exception instance object</span>
	 *          <span class="zh-CN">异常实例对象</span>
	 */
	private void printStackMessage(final Exception e) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Stack_Message_Error", e);
		}
	}

	/**
	 * <h3 class="en-US">Generate connect string by given server address and port number</h3>
	 * <h3 class="zhs">根据给定的服务器地址和端口号生成连接字符串</h3>
	 *
	 * @param serverConfig <h3 class="en-US">Cache server config information</h3>
	 *                     <h3 class="zh-CN">缓存服务器配置信息</h3>
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
	}
}