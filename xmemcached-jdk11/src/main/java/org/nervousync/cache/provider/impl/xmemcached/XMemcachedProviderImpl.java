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

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Memcached cache provider using xmemcached
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 12/23/2020 13:43 PM $
 */
@Provider(name = "XMemcachedProvider", messageKey = "xmemcached.cache.provider.name")
public class XMemcachedProviderImpl extends AbstractProvider {

	/**
	 * Memcached client object
	 */
	private MemcachedClient memcachedClient = null;

	/**
	 * Instantiates a new X memcached provider.
	 */
	public XMemcachedProviderImpl() {
	}

    @Override
    public int defaultPort() {
        return 11211;
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#set(String, String, int)
     */
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

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#add(String, String, int)
     */
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

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#replace(String, String, int)
     */
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

    /*
     * (non-Javadoc)
     * @see AbstractProvider#expire(String, int)
     */
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

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#touch(String...)
     */
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

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#delete(String)
     */
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

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#get(String)
     */
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

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#incr(String, long)
     */
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

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#decr(String, long)
     */
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

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#destroy()
     */
	@Override
	public void destroy() {
		if (this.memcachedClient != null && !this.memcachedClient.isShutdown()) {
			try {
				this.memcachedClient.shutdown();
			} catch (final IOException e) {
				this.logger.error("Destroy_Memcached_Cache_Error");
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Utils", "Stack_Message_Error", e);
				}
			}
		}
	}

	private void printStackMessage(final Exception e) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Utils", "Stack_Message_Error", e);
		}
	}
	
    /*
     * (non-Javadoc)
     * @see AbstractProvider#singletonMode(ServerConfig, String, String)
     */
    protected void singletonMode(final ServerConfig serverConfig,
								 final String userName, final String passWord) throws CacheException {
		this.initConnection(Collections.singletonList(AddrUtil.getOneAddress(this.serverAddress(serverConfig))), 
				new int[]{CacheGlobals.DEFAULT_CACHE_SERVER_WEIGHT}, userName, passWord);
	}

    /*
     * (non-Javadoc)
     * @see AbstractProvider#clusterMode(List, String, String, String)
     */
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
		for (int i = 0 ; i < weightList.size() ; i++) {
			serverWeights[i] = weightList.get(i);
		}
		this.initConnection(serverList, serverWeights, userName, passWord);
	}
	
	private String serverAddress(final ServerConfig serverConfig) {
		if (serverConfig == null) {
			return null;
		}
		return serverConfig.getServerAddress() + ":" + super.serverPort(serverConfig.getServerPort());
	}
	
	private void initConnection(final List<InetSocketAddress> serverList, final int[] serverWeights, 
								final String userName, final String passWord) throws CacheException {
		if (serverList == null || serverWeights == null || serverList.size() != serverWeights.length) {
			return;
		}
		MemcachedClientBuilder clientBuilder =
				new XMemcachedClientBuilder(serverList, serverWeights);
		//  Using binary protocol instead of text protocol, if we use memcached 1.4.0 or later
		clientBuilder.setCommandFactory(new BinaryCommandFactory());

		if (serverList.size() > 1) {
			//  Consistent Hash
			clientBuilder.setSessionLocator(new KetamaMemcachedSessionLocator());
			clientBuilder.setConnectionPoolSize(this.getClientPoolSize());
		}

		if (StringUtils.notBlank(userName) && StringUtils.notBlank(passWord)) {
			serverList.forEach(socketAddress -> 
					clientBuilder.addAuthInfo(socketAddress, AuthInfo.plain(userName, passWord)));
		}
		try {
			this.memcachedClient = clientBuilder.build();
		} catch (IOException e) {
			throw new CacheException(0x000C00000006L, e);
		}
	}
}